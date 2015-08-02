package com.kolich.blog.components.cache;

import com.gitblit.models.PathModel;
import com.google.common.eventbus.Subscribe;
import com.kolich.blog.ApplicationConfig;
import com.kolich.blog.components.GitRepository;
import com.kolich.blog.components.cache.bus.BlogEventBus;
import com.kolich.blog.protos.Events;
import com.kolich.blog.protos.Events.*;
import com.kolich.blog.protos.Events.CachedContentEvent;
import com.kolich.blog.protos.Events.CachedContentEvent.Operation;
import com.kolich.curacao.annotations.Component;
import com.kolich.curacao.annotations.Injectable;
import com.kolich.curacao.annotations.Required;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.List;

import static com.gitblit.utils.JGitUtils.getFilesInCommit;
import static org.apache.commons.io.FilenameUtils.removeExtension;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public final class MarkdownCacheBuilder {

    private static final Logger logger__ = getLogger(MarkdownCacheBuilder.class);

    private static final String contentRootDir__ = ApplicationConfig.getContentRootDir();

    private final GitRepository git_;
    private final BlogEventBus eventBus_;

    @Injectable
    public MarkdownCacheBuilder(@Required final GitRepository git,
                                @Required final BlogEventBus eventBus) {
        git_ = git;
        eventBus_ = eventBus;
        eventBus_.register(this);
    }

    @Subscribe
    public final void onGitPull(final Events.GitPullEvent e) throws Exception {
        final Git git = git_.getGit();
        final Repository repo = git_.getRepo();
        // Rebuild the new cache using the "updated" content on disk, if any.
        final String pathToContent = FileSystems.getDefault().getPath(contentRootDir__).toString();
        // Alert any listeners that we're starting to read cached content from the Git repo.
        eventBus_.post(StartReadCachedContentEvent.newBuilder().build());
        for (final RevCommit commit : git.log().call()) {
            final List<PathModel.PathChangeModel> files = getFilesInCommit(repo, commit);
            // For each of the files that "changed" (added, removed, modified) in the commit...
            for (final PathModel.PathChangeModel change : files) {
                final String hash = commit.getName(); // Commit SHA-1 hash
                final String name = change.name; // Path to file that was "changed"
                final String title = commit.getShortMessage(); // First line of commit message
                final String message = commit.getFullMessage(); // Entire commit message
                // Commit timestamp, in seconds. Note the conversion to milliseconds because JGit gives us the
                // commit time in seconds... sigh.
                final Long timestamp = commit.getCommitTime() * 1000L;
                // We only care about commits that touched files in the path to our "content root" directory.
                if (!name.startsWith(pathToContent)) {
                    continue;
                }
                // The "work tree" is where files are checked out for viewing and editing.
                final File markdown = new File(repo.getWorkTree(), name);
                final String entityName = removeExtension(markdown.getName());
                final CachedContentEvent.Builder event = CachedContentEvent.newBuilder()
                    .setName(entityName)
                    .setTitle(title)
                    .setMsg(message)
                    .setHash(hash)
                    .setTimestamp(timestamp)
                    .setFile(markdown.getCanonicalPath());
                // Vector based on the change type.
                switch (change.changeType) {
                    case ADD:
                        // If the change type was an add, but the file that was added no longer exists,
                        // then treat the operation as a delete instead.
                        event.setOperation(markdown.exists() ? Operation.ADD : Operation.DELETE);
                        break;
                    case MODIFY:
                        event.setOperation(Operation.MODIFY);
                        break;
                    case DELETE:
                        event.setOperation(Operation.DELETE);
                        break;
                    case RENAME:
                        event.setOperation(Operation.RENAME);
                        break;
                    case COPY:
                        event.setOperation(Operation.COPY);
                        break;
                    default:
                        logger__.debug("Received unknown/unsupported event: {}", event);
                        break;
                }
                // Publish an event onto the event bus.
                eventBus_.post(event.build());
            }
        }
        // Alert any listeners that we're finished reading all cached content from the Git repo.
        eventBus_.post(EndReadCachedContentEvent.newBuilder().build());
    }

}
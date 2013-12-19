package com.kolich.blog.components;

import com.kolich.blog.components.util.MarkdownCacheComponent;
import com.kolich.blog.entities.Entry;
import com.kolich.curacao.annotations.Component;
import com.kolich.curacao.annotations.Injectable;
import org.eclipse.jgit.diff.DiffEntry;

import javax.annotation.Nullable;
import java.io.File;

@Component
public final class EntryCache extends MarkdownCacheComponent<Entry> {

    private static final String ENTRIES_CONTENT_DIR_NAME = "entries";

    @Injectable
    public EntryCache(final GitRepository git) {
        super(git);
    }

    @Override
    public final MarkdownEntityBuilder<Entry> getBuilder() {
        return new MarkdownEntityBuilder<Entry>() {
            @Override
            public Entry build(final String name,
                               final File markdown,
                               final String hash,
                               final Long timestamp,
                               final DiffEntry.ChangeType changeType) {
                return new Entry(name, markdown, hash, timestamp, changeType);
            }
        };
    }

    @Override
    public final String getContentDirectory() {
        return ENTRIES_CONTENT_DIR_NAME;
    }

    @Nullable
    public final Entry getEntry(final String name) {
        return get(name);
    }

}

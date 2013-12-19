package com.kolich.blog.entities;

import org.eclipse.jgit.diff.DiffEntry;

import java.io.File;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class MarkdownDrivenContent {

    private final String name_;
    private final File markdown_;
    private final String hash_;
    private final Long timestamp_;
    private final DiffEntry.ChangeType changeType_;

    public MarkdownDrivenContent(final String name,
                                 final File markdown,
                                 final String hash,
                                 final Long timestamp,
                                 final DiffEntry.ChangeType changeType) {
        name_ = checkNotNull(name, "Content name cannot be null.");
        markdown_ = checkNotNull(markdown, "Markdown file cannot be null.");
        hash_ = checkNotNull(hash, "Git commit hash cannot be null.");
        timestamp_ = checkNotNull(timestamp, "Commit timestamp cannot be null.");
        changeType_ = checkNotNull(changeType, "Change type cannot be null.");
    }

    public final String getName() {
        return name_;
    }

    public final File getMarkdown() {
        return markdown_;
    }

    public final String getHash() {
        return hash_;
    }

    public final Long getTimestamp() {
        return timestamp_;
    }

    public final DiffEntry.ChangeType getChangeType() {
        return changeType_;
    }

}

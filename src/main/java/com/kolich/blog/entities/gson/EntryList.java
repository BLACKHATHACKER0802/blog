package com.kolich.blog.entities.gson;

import com.google.gson.annotations.SerializedName;
import com.kolich.blog.entities.Entry;

import javax.annotation.Nonnull;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class EntryList extends GsonAppendableBlogEntity {

    @SerializedName("entries")
    private final List<Entry> entries_;

    public EntryList(@Nonnull final List<Entry> entries) {
        entries_ = checkNotNull(entries, "Entry list cannot be null.");
    }

}
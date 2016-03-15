/*
 * Copyright (C) 2016 Ordnance Survey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.os.elements.search.android.recentmanager.impl.provider.content.searchresult;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uk.os.elements.search.android.recentmanager.impl.provider.content.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code searchresult} table.
 */
public class SearchResultCursor extends AbstractCursor implements SearchResultModel {
    public SearchResultCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(SearchResultColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, " +
                    "which is not allowed according to the model definition");
        return res;
    }

    /**
     * the type of search result
     * Cannot be {@code null}.
     */
    @NonNull
    public String getType() {
        String res = getStringOrNull(SearchResultColumns.TYPE);
        if (res == null)
            throw new NullPointerException("The value of 'type' in the database was null, " +
                    "which is not allowed according to the model definition");
        return res;
    }

    /**
     * the unique id of the search result
     * Cannot be {@code null}.
     */
    @NonNull
    public String getFeatureid() {
        String res = getStringOrNull(SearchResultColumns.FEATUREID);
        if (res == null)
            throw new NullPointerException("The value of 'featureid' in the database was null, " +
                    "which is not allowed according to the model definition");
        return res;
    }

    /**
     * the timestamp that search result was last pressed
     * Can be {@code null}.
     */
    @Nullable
    public Long getAccessed() {
        Long res = getLongOrNull(SearchResultColumns.ACCESSED);
        return res;
    }

    /**
     * feature name
     * Can be {@code null}.
     */
    @Nullable
    public String getName() {
        String res = getStringOrNull(SearchResultColumns.NAME);
        return res;
    }

    /**
     * feature geo-context
     * Can be {@code null}.
     */
    @Nullable
    public String getContext() {
        String res = getStringOrNull(SearchResultColumns.CONTEXT);
        return res;
    }

    /**
     * Get the {@code x} value.
     * Can be {@code null}.
     */
    @Nullable
    public Double getX() {
        Double res = getDoubleOrNull(SearchResultColumns.X);
        return res;
    }

    /**
     * Get the {@code y} value.
     * Can be {@code null}.
     */
    @Nullable
    public Double getY() {
        Double res = getDoubleOrNull(SearchResultColumns.Y);
        return res;
    }

    /**
     * Get the {@code srid} value.
     * Can be {@code null}.
     */
    @Nullable
    public Integer getSrid() {
        Integer res = getIntegerOrNull(SearchResultColumns.SRID);
        return res;
    }

    /**
     * Get the {@code minx} value.
     * Can be {@code null}.
     */
    @Nullable
    public Double getMinx() {
        Double res = getDoubleOrNull(SearchResultColumns.MINX);
        return res;
    }

    /**
     * Get the {@code miny} value.
     * Can be {@code null}.
     */
    @Nullable
    public Double getMiny() {
        Double res = getDoubleOrNull(SearchResultColumns.MINY);
        return res;
    }

    /**
     * Get the {@code maxx} value.
     * Can be {@code null}.
     */
    @Nullable
    public Double getMaxx() {
        Double res = getDoubleOrNull(SearchResultColumns.MAXX);
        return res;
    }

    /**
     * Get the {@code maxy} value.
     * Can be {@code null}.
     */
    @Nullable
    public Double getMaxy() {
        Double res = getDoubleOrNull(SearchResultColumns.MAXY);
        return res;
    }
}

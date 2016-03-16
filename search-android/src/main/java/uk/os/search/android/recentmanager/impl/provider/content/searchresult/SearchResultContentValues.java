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

package uk.os.search.android.recentmanager.impl.provider.content.searchresult;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uk.os.search.android.recentmanager.impl.provider.content.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code searchresult} table.
 */
public class SearchResultContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return SearchResultColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable SearchResultSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(),
                where == null ? null : where.args());
    }

    /**
     * the type of search result
     */
    public SearchResultContentValues putType(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("type must not be null");
        mContentValues.put(SearchResultColumns.TYPE, value);
        return this;
    }

    /**
     * the unique id of the search result
     */
    public SearchResultContentValues putFeatureid(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("featureid must not be null");
        mContentValues.put(SearchResultColumns.FEATUREID, value);
        return this;
    }

    /**
     * the timestamp that search result was last pressed
     */
    public SearchResultContentValues putAccessed(@Nullable Long value) {
        mContentValues.put(SearchResultColumns.ACCESSED, value);
        return this;
    }

    public SearchResultContentValues putAccessedNull() {
        mContentValues.putNull(SearchResultColumns.ACCESSED);
        return this;
    }

    /**
     * feature name
     */
    public SearchResultContentValues putName(@Nullable String value) {
        mContentValues.put(SearchResultColumns.NAME, value);
        return this;
    }

    public SearchResultContentValues putNameNull() {
        mContentValues.putNull(SearchResultColumns.NAME);
        return this;
    }

    /**
     * feature geo-context
     */
    public SearchResultContentValues putContext(@Nullable String value) {
        mContentValues.put(SearchResultColumns.CONTEXT, value);
        return this;
    }

    public SearchResultContentValues putContextNull() {
        mContentValues.putNull(SearchResultColumns.CONTEXT);
        return this;
    }

    public SearchResultContentValues putX(@Nullable Double value) {
        mContentValues.put(SearchResultColumns.X, value);
        return this;
    }

    public SearchResultContentValues putXNull() {
        mContentValues.putNull(SearchResultColumns.X);
        return this;
    }

    public SearchResultContentValues putY(@Nullable Double value) {
        mContentValues.put(SearchResultColumns.Y, value);
        return this;
    }

    public SearchResultContentValues putYNull() {
        mContentValues.putNull(SearchResultColumns.Y);
        return this;
    }

    public SearchResultContentValues putSrid(@Nullable Integer value) {
        mContentValues.put(SearchResultColumns.SRID, value);
        return this;
    }

    public SearchResultContentValues putSridNull() {
        mContentValues.putNull(SearchResultColumns.SRID);
        return this;
    }

    public SearchResultContentValues putMinx(@Nullable Double value) {
        mContentValues.put(SearchResultColumns.MINX, value);
        return this;
    }

    public SearchResultContentValues putMinxNull() {
        mContentValues.putNull(SearchResultColumns.MINX);
        return this;
    }

    public SearchResultContentValues putMiny(@Nullable Double value) {
        mContentValues.put(SearchResultColumns.MINY, value);
        return this;
    }

    public SearchResultContentValues putMinyNull() {
        mContentValues.putNull(SearchResultColumns.MINY);
        return this;
    }

    public SearchResultContentValues putMaxx(@Nullable Double value) {
        mContentValues.put(SearchResultColumns.MAXX, value);
        return this;
    }

    public SearchResultContentValues putMaxxNull() {
        mContentValues.putNull(SearchResultColumns.MAXX);
        return this;
    }

    public SearchResultContentValues putMaxy(@Nullable Double value) {
        mContentValues.put(SearchResultColumns.MAXY, value);
        return this;
    }

    public SearchResultContentValues putMaxyNull() {
        mContentValues.putNull(SearchResultColumns.MAXY);
        return this;
    }
}

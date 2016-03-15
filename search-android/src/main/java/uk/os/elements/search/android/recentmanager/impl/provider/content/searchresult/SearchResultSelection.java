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

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import uk.os.elements.search.android.recentmanager.impl.provider.content.base.AbstractSelection;

/**
 * Selection for the {@code searchresult} table.
 */
public class SearchResultSelection extends AbstractSelection<SearchResultSelection> {
    @Override
    protected Uri baseUri() {
        return SearchResultColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code SearchresultCursor} object, which is positioned before the first entry, or null.
     */
    public SearchResultCursor query(ContentResolver contentResolver, String[] projection,
                                    String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new SearchResultCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null)}.
     */
    public SearchResultCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null)}.
     */
    public SearchResultCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public SearchResultSelection id(long... value) {
        addEquals("searchresult." + SearchResultColumns._ID, toObjectArray(value));
        return this;
    }

    public SearchResultSelection type(String... value) {
        addEquals(SearchResultColumns.TYPE, value);
        return this;
    }

    public SearchResultSelection typeNot(String... value) {
        addNotEquals(SearchResultColumns.TYPE, value);
        return this;
    }

    public SearchResultSelection typeLike(String... value) {
        addLike(SearchResultColumns.TYPE, value);
        return this;
    }

    public SearchResultSelection typeContains(String... value) {
        addContains(SearchResultColumns.TYPE, value);
        return this;
    }

    public SearchResultSelection typeStartsWith(String... value) {
        addStartsWith(SearchResultColumns.TYPE, value);
        return this;
    }

    public SearchResultSelection typeEndsWith(String... value) {
        addEndsWith(SearchResultColumns.TYPE, value);
        return this;
    }

    public SearchResultSelection featureid(String... value) {
        addEquals(SearchResultColumns.FEATUREID, value);
        return this;
    }

    public SearchResultSelection featureidNot(String... value) {
        addNotEquals(SearchResultColumns.FEATUREID, value);
        return this;
    }

    public SearchResultSelection featureidLike(String... value) {
        addLike(SearchResultColumns.FEATUREID, value);
        return this;
    }

    public SearchResultSelection featureidContains(String... value) {
        addContains(SearchResultColumns.FEATUREID, value);
        return this;
    }

    public SearchResultSelection featureidStartsWith(String... value) {
        addStartsWith(SearchResultColumns.FEATUREID, value);
        return this;
    }

    public SearchResultSelection featureidEndsWith(String... value) {
        addEndsWith(SearchResultColumns.FEATUREID, value);
        return this;
    }

    public SearchResultSelection accessed(Long... value) {
        addEquals(SearchResultColumns.ACCESSED, value);
        return this;
    }

    public SearchResultSelection accessedNot(Long... value) {
        addNotEquals(SearchResultColumns.ACCESSED, value);
        return this;
    }

    public SearchResultSelection accessedGt(long value) {
        addGreaterThan(SearchResultColumns.ACCESSED, value);
        return this;
    }

    public SearchResultSelection accessedGtEq(long value) {
        addGreaterThanOrEquals(SearchResultColumns.ACCESSED, value);
        return this;
    }

    public SearchResultSelection accessedLt(long value) {
        addLessThan(SearchResultColumns.ACCESSED, value);
        return this;
    }

    public SearchResultSelection accessedLtEq(long value) {
        addLessThanOrEquals(SearchResultColumns.ACCESSED, value);
        return this;
    }

    public SearchResultSelection name(String... value) {
        addEquals(SearchResultColumns.NAME, value);
        return this;
    }

    public SearchResultSelection nameNot(String... value) {
        addNotEquals(SearchResultColumns.NAME, value);
        return this;
    }

    public SearchResultSelection nameLike(String... value) {
        addLike(SearchResultColumns.NAME, value);
        return this;
    }

    public SearchResultSelection nameContains(String... value) {
        addContains(SearchResultColumns.NAME, value);
        return this;
    }

    public SearchResultSelection nameStartsWith(String... value) {
        addStartsWith(SearchResultColumns.NAME, value);
        return this;
    }

    public SearchResultSelection nameEndsWith(String... value) {
        addEndsWith(SearchResultColumns.NAME, value);
        return this;
    }

    public SearchResultSelection context(String... value) {
        addEquals(SearchResultColumns.CONTEXT, value);
        return this;
    }

    public SearchResultSelection contextNot(String... value) {
        addNotEquals(SearchResultColumns.CONTEXT, value);
        return this;
    }

    public SearchResultSelection contextLike(String... value) {
        addLike(SearchResultColumns.CONTEXT, value);
        return this;
    }

    public SearchResultSelection contextContains(String... value) {
        addContains(SearchResultColumns.CONTEXT, value);
        return this;
    }

    public SearchResultSelection contextStartsWith(String... value) {
        addStartsWith(SearchResultColumns.CONTEXT, value);
        return this;
    }

    public SearchResultSelection contextEndsWith(String... value) {
        addEndsWith(SearchResultColumns.CONTEXT, value);
        return this;
    }

    public SearchResultSelection x(Double... value) {
        addEquals(SearchResultColumns.X, value);
        return this;
    }

    public SearchResultSelection xNot(Double... value) {
        addNotEquals(SearchResultColumns.X, value);
        return this;
    }

    public SearchResultSelection xGt(double value) {
        addGreaterThan(SearchResultColumns.X, value);
        return this;
    }

    public SearchResultSelection xGtEq(double value) {
        addGreaterThanOrEquals(SearchResultColumns.X, value);
        return this;
    }

    public SearchResultSelection xLt(double value) {
        addLessThan(SearchResultColumns.X, value);
        return this;
    }

    public SearchResultSelection xLtEq(double value) {
        addLessThanOrEquals(SearchResultColumns.X, value);
        return this;
    }

    public SearchResultSelection y(Double... value) {
        addEquals(SearchResultColumns.Y, value);
        return this;
    }

    public SearchResultSelection yNot(Double... value) {
        addNotEquals(SearchResultColumns.Y, value);
        return this;
    }

    public SearchResultSelection yGt(double value) {
        addGreaterThan(SearchResultColumns.Y, value);
        return this;
    }

    public SearchResultSelection yGtEq(double value) {
        addGreaterThanOrEquals(SearchResultColumns.Y, value);
        return this;
    }

    public SearchResultSelection yLt(double value) {
        addLessThan(SearchResultColumns.Y, value);
        return this;
    }

    public SearchResultSelection yLtEq(double value) {
        addLessThanOrEquals(SearchResultColumns.Y, value);
        return this;
    }

    public SearchResultSelection srid(Integer... value) {
        addEquals(SearchResultColumns.SRID, value);
        return this;
    }

    public SearchResultSelection sridNot(Integer... value) {
        addNotEquals(SearchResultColumns.SRID, value);
        return this;
    }

    public SearchResultSelection sridGt(int value) {
        addGreaterThan(SearchResultColumns.SRID, value);
        return this;
    }

    public SearchResultSelection sridGtEq(int value) {
        addGreaterThanOrEquals(SearchResultColumns.SRID, value);
        return this;
    }

    public SearchResultSelection sridLt(int value) {
        addLessThan(SearchResultColumns.SRID, value);
        return this;
    }

    public SearchResultSelection sridLtEq(int value) {
        addLessThanOrEquals(SearchResultColumns.SRID, value);
        return this;
    }

    public SearchResultSelection minx(Double... value) {
        addEquals(SearchResultColumns.MINX, value);
        return this;
    }

    public SearchResultSelection minxNot(Double... value) {
        addNotEquals(SearchResultColumns.MINX, value);
        return this;
    }

    public SearchResultSelection minxGt(double value) {
        addGreaterThan(SearchResultColumns.MINX, value);
        return this;
    }

    public SearchResultSelection minxGtEq(double value) {
        addGreaterThanOrEquals(SearchResultColumns.MINX, value);
        return this;
    }

    public SearchResultSelection minxLt(double value) {
        addLessThan(SearchResultColumns.MINX, value);
        return this;
    }

    public SearchResultSelection minxLtEq(double value) {
        addLessThanOrEquals(SearchResultColumns.MINX, value);
        return this;
    }

    public SearchResultSelection miny(Double... value) {
        addEquals(SearchResultColumns.MINY, value);
        return this;
    }

    public SearchResultSelection minyNot(Double... value) {
        addNotEquals(SearchResultColumns.MINY, value);
        return this;
    }

    public SearchResultSelection minyGt(double value) {
        addGreaterThan(SearchResultColumns.MINY, value);
        return this;
    }

    public SearchResultSelection minyGtEq(double value) {
        addGreaterThanOrEquals(SearchResultColumns.MINY, value);
        return this;
    }

    public SearchResultSelection minyLt(double value) {
        addLessThan(SearchResultColumns.MINY, value);
        return this;
    }

    public SearchResultSelection minyLtEq(double value) {
        addLessThanOrEquals(SearchResultColumns.MINY, value);
        return this;
    }

    public SearchResultSelection maxx(Double... value) {
        addEquals(SearchResultColumns.MAXX, value);
        return this;
    }

    public SearchResultSelection maxxNot(Double... value) {
        addNotEquals(SearchResultColumns.MAXX, value);
        return this;
    }

    public SearchResultSelection maxxGt(double value) {
        addGreaterThan(SearchResultColumns.MAXX, value);
        return this;
    }

    public SearchResultSelection maxxGtEq(double value) {
        addGreaterThanOrEquals(SearchResultColumns.MAXX, value);
        return this;
    }

    public SearchResultSelection maxxLt(double value) {
        addLessThan(SearchResultColumns.MAXX, value);
        return this;
    }

    public SearchResultSelection maxxLtEq(double value) {
        addLessThanOrEquals(SearchResultColumns.MAXX, value);
        return this;
    }

    public SearchResultSelection maxy(Double... value) {
        addEquals(SearchResultColumns.MAXY, value);
        return this;
    }

    public SearchResultSelection maxyNot(Double... value) {
        addNotEquals(SearchResultColumns.MAXY, value);
        return this;
    }

    public SearchResultSelection maxyGt(double value) {
        addGreaterThan(SearchResultColumns.MAXY, value);
        return this;
    }

    public SearchResultSelection maxyGtEq(double value) {
        addGreaterThanOrEquals(SearchResultColumns.MAXY, value);
        return this;
    }

    public SearchResultSelection maxyLt(double value) {
        addLessThan(SearchResultColumns.MAXY, value);
        return this;
    }

    public SearchResultSelection maxyLtEq(double value) {
        addLessThanOrEquals(SearchResultColumns.MAXY, value);
        return this;
    }
}

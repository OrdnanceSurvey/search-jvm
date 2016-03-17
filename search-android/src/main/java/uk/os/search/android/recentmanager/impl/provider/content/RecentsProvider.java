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

package uk.os.search.android.recentmanager.impl.provider.content;

import java.util.Arrays;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import timber.log.Timber;
import uk.os.search.android.BuildConfig;
import uk.os.search.android.recentmanager.impl.provider.content.base.BaseContentProvider;
import uk.os.search.android.recentmanager.impl.provider.content.searchresult.SearchResultColumns;

public class RecentsProvider extends BaseContentProvider {

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

    public static final String AUTHORITY = SearchContract.CONTENT_AUTHORITY + ".recents";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;

    private static final int URI_TYPE_SEARCHRESULT = 0;
    private static final int URI_TYPE_SEARCHRESULT_ID = 1;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, SearchResultColumns.TABLE_NAME, URI_TYPE_SEARCHRESULT);
        URI_MATCHER.addURI(AUTHORITY, SearchResultColumns.TABLE_NAME + "/#",
                URI_TYPE_SEARCHRESULT_ID);
    }

    @Override
    protected SQLiteOpenHelper createSqLiteOpenHelper() {
        return SearchRecentsSQLiteOpenHelper.getInstance(getContext());
    }

    @Override
    protected boolean hasDebug() {
        return DEBUG;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_SEARCHRESULT:
                return TYPE_CURSOR_DIR + SearchResultColumns.TABLE_NAME;
            case URI_TYPE_SEARCHRESULT_ID:
                return TYPE_CURSOR_ITEM + SearchResultColumns.TABLE_NAME;

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (DEBUG) Timber.d("insert uri=%s values=%s", uri, values);
        return super.insert(uri, values);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (DEBUG) Timber.d("bulkInsert uri=%s values.length=%d", uri,
                values.length);
        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG) Timber.d("update uri=%s values=%s selection=%s selectionArgs=%s",
                uri, values, selection, Arrays.toString(selectionArgs));
        return super.update(uri, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) Timber.d("delete uri=%s selection=%s selectionArgs=%s", uri, selection,
                Arrays.toString(selectionArgs));
        return super.delete(uri, selection, selectionArgs);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        if (DEBUG)
            Timber.d("query uri=%s selection=%s selectionArgs=%s sortOrder=%s groupBy=%s " +
                    "having=%s limit=%s",
                    uri, selection, Arrays.toString(selectionArgs), sortOrder,
                    uri.getQueryParameter(BaseContentProvider.QUERY_GROUP_BY),
                    uri.getQueryParameter(BaseContentProvider.QUERY_HAVING),
                    uri.getQueryParameter(BaseContentProvider.QUERY_LIMIT));
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = URI_MATCHER.match(uri);
        switch (matchedId) {
            case URI_TYPE_SEARCHRESULT:
            case URI_TYPE_SEARCHRESULT_ID:
                res.table = SearchResultColumns.TABLE_NAME;
                res.idColumn = SearchResultColumns._ID;
                res.tablesWithJoins = SearchResultColumns.TABLE_NAME;
                res.orderBy = SearchResultColumns.DEFAULT_ORDER;
                break;

            default:
                throw new IllegalArgumentException("The uri '" + uri +
                        "' is not supported by this ContentProvider");
        }

        switch (matchedId) {
            case URI_TYPE_SEARCHRESULT_ID:
                id = uri.getLastPathSegment();
        }
        if (id != null) {
            if (selection != null) {
                res.selection = res.table + "." + res.idColumn + "=" + id +
                        " and (" + selection + ")";
            } else {
                res.selection = res.table + "." + res.idColumn + "=" + id;
            }
        } else {
            res.selection = selection;
        }
        return res;
    }
}

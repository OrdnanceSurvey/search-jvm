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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import timber.log.Timber;

import uk.os.search.SearchResult;
import uk.os.search.android.BuildConfig;
import uk.os.search.android.recentmanager.impl.provider.content.searchresult.SearchResultColumns;

/**
 * Implement your custom database creation or upgrade code here.
 *
 * This file will not be overwritten if you re-run the content provider generator.
 */
public class SearchRecentsSQLiteOpenHelperCallbacks {

    private final boolean DEBUG = BuildConfig.DEBUG;

    public void onOpen(final Context context, final SQLiteDatabase db) {
        if (DEBUG) Timber.d("onOpen");
        // Insert your db open code here.
    }

    public void onPreCreate(final Context context, final SQLiteDatabase db) {
        if (DEBUG) Timber.d("onPreCreate");
        // Insert your db creation code here. This is called before your tables are created.
    }

    public void onPostCreate(final Context context, final SQLiteDatabase db) {
        if (DEBUG) Timber.d("onPostCreate");
        // Insert your db creation code here. This is called after your tables are created.
    }

    /**
     * Note: the caller performs this within a transaction.  DO NOT perform a transaction within
     * here.  If an exception is thrown let it bubble up to avoid a borked database.
     */
    public void onUpgrade(final Context context, final SQLiteDatabase db, final int oldVersion,
                          final int newVersion) {
        if (DEBUG) Timber.d("Upgrading database from version %d to %d", oldVersion, newVersion);
        // Insert your upgrading code here.
        switch (oldVersion) {
            case 1: {
                db.execSQL("ALTER TABLE " + SearchResultColumns.TABLE_NAME +
                        " ADD COLUMN " + SearchResultColumns.SRID + " INTEGER DEFAULT 27700");
                db.execSQL("ALTER TABLE " + SearchResultColumns.TABLE_NAME +
                        " ADD COLUMN " + SearchResultColumns.TYPE + " TEXT DEFAULT '" +
                        SearchResult.class.getSimpleName() + "'");
                break;
            }
            default:
                // fall through
        }
    }
}

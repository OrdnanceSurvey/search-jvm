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

package uk.os.elements.search.android.recentmanager.impl.provider.content;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import uk.os.elements.search.android.recentmanager.impl.provider.content.searchresult.SearchResultColumns;

public class SearchRecentsSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_FILE_NAME = "search_recents.db";
    private static final int DATABASE_VERSION = 2;
    private static SearchRecentsSQLiteOpenHelper sInstance;
    private final Context mContext;
    private final SearchRecentsSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:off
    public static final String SQL_CREATE_TABLE_SEARCHRESULT = "CREATE TABLE IF NOT EXISTS "
            + SearchResultColumns.TABLE_NAME + " ( "
            + SearchResultColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SearchResultColumns.TYPE + " TEXT NOT NULL, "
            + SearchResultColumns.FEATUREID + " TEXT NOT NULL, "
            + SearchResultColumns.ACCESSED + " INTEGER, "
            + SearchResultColumns.NAME + " TEXT, "
            + SearchResultColumns.CONTEXT + " TEXT, "
            + SearchResultColumns.X + " REAL, "
            + SearchResultColumns.Y + " REAL, "
            + SearchResultColumns.SRID + " INTEGER, "
            + SearchResultColumns.MINX + " REAL, "
            + SearchResultColumns.MINY + " REAL, "
            + SearchResultColumns.MAXX + " REAL, "
            + SearchResultColumns.MAXY + " REAL "
            + ", CONSTRAINT unique_name UNIQUE (featureid) ON CONFLICT REPLACE"
            + " );";

    // @formatter:on

    public static SearchRecentsSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static SearchRecentsSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */
    private static SearchRecentsSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new SearchRecentsSQLiteOpenHelper(context);
    }

    private SearchRecentsSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new SearchRecentsSQLiteOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static SearchRecentsSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new SearchRecentsSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private SearchRecentsSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new SearchRecentsSQLiteOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_SEARCHRESULT);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}

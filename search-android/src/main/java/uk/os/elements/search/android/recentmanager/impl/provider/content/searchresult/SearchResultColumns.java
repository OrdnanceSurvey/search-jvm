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

import android.net.Uri;
import android.provider.BaseColumns;

import uk.os.elements.search.android.recentmanager.impl.provider.content.RecentsProvider;

/**
 * The Serialized SearchResult
 */
public class SearchResultColumns implements BaseColumns {
    public static final String TABLE_NAME = "searchresult";
    public static final Uri CONTENT_URI = Uri.parse(RecentsProvider.CONTENT_URI_BASE + "/" +
            TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * the type of search result
     */
    public static final String TYPE = "type";

    /**
     * the unique id of the search result
     */
    public static final String FEATUREID = "featureid";

    /**
     * the timestamp that search result was last pressed
     */
    public static final String ACCESSED = "accessed";

    /**
     * feature name
     */
    public static final String NAME = "name";

    /**
     * feature geo-context
     */
    public static final String CONTEXT = "context";

    public static final String X = "x";

    public static final String Y = "y";

    public static final String SRID = "srid";

    public static final String MINX = "minx";

    public static final String MINY = "miny";

    public static final String MAXX = "maxx";

    public static final String MAXY = "maxy";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            TYPE,
            FEATUREID,
            ACCESSED,
            NAME,
            CONTEXT,
            X,
            Y,
            SRID,
            MINX,
            MINY,
            MAXX,
            MAXY
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(TYPE) || c.contains("." + TYPE)) return true;
            if (c.equals(FEATUREID) || c.contains("." + FEATUREID)) return true;
            if (c.equals(ACCESSED) || c.contains("." + ACCESSED)) return true;
            if (c.equals(NAME) || c.contains("." + NAME)) return true;
            if (c.equals(CONTEXT) || c.contains("." + CONTEXT)) return true;
            if (c.equals(X) || c.contains("." + X)) return true;
            if (c.equals(Y) || c.contains("." + Y)) return true;
            if (c.equals(SRID) || c.contains("." + SRID)) return true;
            if (c.equals(MINX) || c.contains("." + MINX)) return true;
            if (c.equals(MINY) || c.contains("." + MINY)) return true;
            if (c.equals(MAXX) || c.contains("." + MAXX)) return true;
            if (c.equals(MAXY) || c.contains("." + MAXY)) return true;
        }
        return false;
    }

}

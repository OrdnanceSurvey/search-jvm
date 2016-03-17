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

package uk.os.search.android.providers.opennames.service.model;

import java.io.Serializable;

/**
 * generated code
 */
public class Header implements Serializable {
    private String maxresults;

    private String query;

    private String format;

    private String totalresults;

    private String offset;

    private String uri;

    public String getMaxresults() {
        return maxresults;
    }

    public String getQuery() {
        return query;
    }

    public String getFormat() {
        return format;
    }

    public String getTotalresults() {
        return totalresults;
    }

    public String getOffset() {
        return offset;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "ClassPojo [maxresults = " + maxresults + ", query = " + query + ", format = " +
                format + ", totalresults = " + totalresults + ", offset = " + offset + ", uri = " +
                uri + "]";
    }
}

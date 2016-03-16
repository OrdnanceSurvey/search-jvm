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

package uk.os.search.android.providers;

import java.util.Collections;
import java.util.List;

import uk.os.search.SearchResult;

public class ProviderResponse {

    private final String mSource;
    private final Throwable mThrowable;
    private final List<SearchResult> mSearchResults;

    public ProviderResponse(String source, List<SearchResult> searchResults) {
        this(source, searchResults, null);
    }

    public ProviderResponse(String source, Throwable throwable) {
        this(source, Collections.<SearchResult>emptyList(), throwable);
    }

    public ProviderResponse(String source, List<SearchResult> searchResults, Throwable throwable) {
        if (searchResults == null) {
            throw new IllegalArgumentException("search results is null");
        }
        mSource = source;
        mSearchResults = searchResults;
        mThrowable = throwable;
    }

    public Throwable getError() {
        return mThrowable;
    }

    public List<SearchResult> getSearchResults() {
        return mSearchResults;
    }

    public String getSource() {
        return mSource;
    }

    public boolean hasError() {
        return mThrowable != null;
    }

}

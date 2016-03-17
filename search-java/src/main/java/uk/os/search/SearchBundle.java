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

package uk.os.search;

import java.util.ArrayList;
import java.util.List;

import uk.os.search.android.providers.ProviderResponse;

public class SearchBundle {

    private final ProviderResponse mRecents;
    private final List<ProviderResponse> mRemaining;

    public SearchBundle(ProviderResponse recents, List<ProviderResponse> remaining) {
        mRecents = recents;
        mRemaining = remaining;
    }

    public List<Throwable> getErrors() {
        List<Throwable> result = new ArrayList<>();
        if (mRecents.hasError()) {
            result.add(mRecents.getError());
        }
        for (ProviderResponse providerResponse : mRemaining) {
            if (providerResponse.hasError()) {
                result.add(providerResponse.getError());
            }
        }
        return result;
    }

    public List<SearchResult> getRecents() {
        return mRecents.getSearchResults();
    }

    public List<SearchResult> getRemaining() {
        List<SearchResult> response = new ArrayList<>();
        for (ProviderResponse providerResponse : mRemaining) {
            response.addAll(providerResponse.getSearchResults());
        }
        return response;
    }

    protected ProviderResponse getRecentsResponse() {
        return mRecents;
    }

    protected List<ProviderResponse> getRemainingResponses() {
        return mRemaining;
    }
}

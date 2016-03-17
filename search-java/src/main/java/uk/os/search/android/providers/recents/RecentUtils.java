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

package uk.os.search.android.providers.recents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.os.search.SearchResult;
import uk.os.search.android.providers.ProviderResponse;

public class RecentUtils {

    private static final Logger sLogger =
            Logger.getLogger(RecentUtils.class.getSimpleName());

    public static void removeRecentsFromSearchResults(List<SearchResult> recents, List<ProviderResponse> providerResponses, RecentsManager staleDataHandler) {
        Map<String, SearchResult> index = new HashMap<>();
        for (SearchResult result : recents) {
            index.put(result.getId(), result);
        }

        for (ProviderResponse providerResponse : providerResponses) {
            List<SearchResult> search = providerResponse.getSearchResults();
            List<SearchResult> removals = new ArrayList<>();
            for (SearchResult result : search) {
                String key = result.getId();

                SearchResult existing = index.get(key);
                boolean stale = existing != null && !existing.equals(result);

                if (stale) {
                    sLogger.log(Level.WARNING, String
                            .format("Stale Data Detected.\nExisting: %s\nLatest: %s",
                                    index.get(key).toString(), result.toString()));
                    staleDataHandler.updateRecent(result);
                    recents.set(recents.indexOf(existing), result);
                }

                boolean hasRecent = existing != null;
                if (hasRecent) {
                    removals.add(result);
                }
            }
            search.removeAll(removals);
        }

    }
}

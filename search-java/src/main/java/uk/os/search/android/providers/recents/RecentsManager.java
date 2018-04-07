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

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import uk.os.search.SearchResult;

public interface RecentsManager {

    /**
     * @param searchTerm a user supplied description of a location
     * @return matched search results ordered by most recent first
     */
    Observable<List<SearchResult>> query(String searchTerm);

    /**
     * @param ids a list IDs representing search results to be returned
     * @return matched search results ordered by most recent first
     */
    Observable<List<SearchResult>> queryById(String... ids);

    /**
     * @param maxResults the maximum number of a recent search results to be returned
     * @return recent search results ordered by most recent first
     */
    Observable<List<SearchResult>> last(int maxResults);

    /**
     * note: [sh/c]ould store the actual query they entered
     * @param searchResult add a search result to the recent queries list.
     * @return a Completable which terminates successfully or errors
     */
    Completable saveRecent(SearchResult searchResult);

    /**
     * If stale recent data is detected then the latest data can be returned here to update
     * @param latest latest data to replace any existing SearchResult with a given ID
     * @return a Completable which terminates successfully or errors
     */
    Completable updateRecent(SearchResult latest);
}

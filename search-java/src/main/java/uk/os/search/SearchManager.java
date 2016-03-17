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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.FuncN;
import uk.os.search.android.providers.Provider;
import uk.os.search.android.providers.ProviderResponse;
import uk.os.search.android.providers.addresses.AddressesProvider;
import uk.os.search.android.providers.bng.GridReferenceProvider;
import uk.os.search.android.providers.latlon.LatLonProvider;
import uk.os.search.android.providers.opennames.OpennamesProvider;
import uk.os.search.android.providers.recents.RecentUtils;
import uk.os.search.android.providers.recents.RecentsManager;

public class SearchManager {

    public static final class Builder {
        List<Provider> providers = new ArrayList<>();
        RecentsManager recentsManager;

        public Builder() {
            providers.addAll(Arrays.asList(getDefaultProviders()));
            recentsManager = null;
        }

        public Builder setProviders(List<Provider> providers) {
            this.providers = providers;
            return this;
        }

        public Builder setProviders(Provider... providers) {
            this.providers = Arrays.asList(providers);
            return this;
        }

        public Builder addOpenNames(String apiKey) {
            this.providers.add(new OpennamesProvider.Builder(apiKey).build());
            return this;
        }

        public Builder addPlaces(String apiKey) {
            this.providers.add(new AddressesProvider.Builder(apiKey).build());
            return this;
        }

        /**
         * @param recentsManager to be used by the {@link SearchManager}
         * @return this builder for fluent construction
         */
        public Builder setRecentsManager(RecentsManager recentsManager) {
            this.recentsManager = recentsManager;
            return this;
        }

        public SearchManager build() {
            return new SearchManager(this);
        }

        private static Provider[] getDefaultProviders() {
            List<Provider> providers = new ArrayList<>();
            providers.add(new GridReferenceProvider());
            providers.add(new LatLonProvider());
            return providers.toArray(new Provider[providers.size()]);
        }
    }

    private final RecentsManager mRecentsManager;
    private final List<Provider> mProviders = new ArrayList<>();

    public SearchManager() {
        this(new Builder());
    }

    private SearchManager(Builder builder) {
        mRecentsManager = builder.recentsManager;
        mProviders.addAll(builder.providers);
    }

    public final Observable<SearchBundle> query(final String searchTerm) {

        List<Observable<ProviderResponse>> streams = new ArrayList<>();

        for (final Provider p : mProviders) {
            Observable<ProviderResponse> stream = p.query(searchTerm).map(new Func1<List<SearchResult>, ProviderResponse>() {
                @Override
                public ProviderResponse call(List<SearchResult> searchResults) {
                    return new ProviderResponse(p.getClass().getSimpleName(), searchResults);
                }
            }).onErrorReturn(new Func1<Throwable, ProviderResponse>() {
                @Override
                public ProviderResponse call(Throwable throwable) {
                    return new ProviderResponse(p.getClass().getSimpleName(),
                            throwable);
                }
            });
            streams.add(stream);
        }

        Observable<List<ProviderResponse>> allResults = Observable.zip(streams, new FuncN<List<ProviderResponse>>() {
            @Override
            public List<ProviderResponse> call(Object... args) {
                List<ProviderResponse> list = new ArrayList<>();
                for (Object o : args) {
                    list.add((ProviderResponse)o);
                }
                return list;
            }
        });

        boolean hasRecents = mRecentsManager != null;
        if (!hasRecents) {
            return allResults.map(new Func1<List<ProviderResponse>, SearchBundle>() {
                @Override
                public SearchBundle call(List<ProviderResponse> providerResponses) {
                    ProviderResponse recents = new ProviderResponse(Void.class.getName(),
                            new ArrayList<SearchResult>());
                    return new SearchBundle(recents, providerResponses);
                }
            });
        } else {
            return allResults.flatMap(new Func1<List<ProviderResponse>,
                    Observable<ProviderResponse>>() {
                @Override
                public Observable<ProviderResponse> call(List<ProviderResponse> providerResponses) {
                    // check search resultset for even more recents (not found by recents matcher)
                    List<String> idsToCheck = new ArrayList<>();
                    for (ProviderResponse providerResponse : providerResponses) {
                        List<SearchResult> searchResults = providerResponse.getSearchResults();
                        for (SearchResult result : searchResults) {
                            String key = result.getId();
                            idsToCheck.add(key);
                        }
                    }

                    final String sourceRecents = RecentsManager.class.getSimpleName();
                    boolean hasIdsToCheck = idsToCheck.size() > 0;
                    if (!hasIdsToCheck) {
                        List<SearchResult> empty = Collections.emptyList();
                        ProviderResponse emptyProviderResponse = new ProviderResponse(sourceRecents,
                                empty);
                        return Observable.just(emptyProviderResponse);
                    } else {
                        String[] ids = idsToCheck.toArray(new String[idsToCheck.size()]);
                        return mRecentsManager.queryById(ids).map(new Func1<List<SearchResult>, ProviderResponse>() {
                            @Override
                            public ProviderResponse call(List<SearchResult> searchResults) {
                                return new ProviderResponse(sourceRecents, searchResults);
                            }
                        }).onErrorReturn(new Func1<Throwable, ProviderResponse>() {
                            @Override
                            public ProviderResponse call(Throwable throwable) {
                                return new ProviderResponse(sourceRecents, throwable);
                            }
                        });
                    }
                }
            }, new Func2<List<ProviderResponse>, ProviderResponse, SearchBundle>() {
                @Override
                public SearchBundle call(List<ProviderResponse> providerResponses, ProviderResponse recentsResponse) {
                    // essentially 'searchResponse.getSearchResults().removeAll(recents)'
                    // only execute a side effect here to update stale references
                    // TODO consider how best to update recents
                    boolean hasRecents = recentsResponse.getSearchResults().size() > 0;
                    if (hasRecents) {
                        RecentUtils
                                .removeRecentsFromSearchResults(recentsResponse.getSearchResults(),
                                        providerResponses, mRecentsManager);
                    }
                    return new SearchBundle(recentsResponse, providerResponses);
                }
            }).zipWith(mRecentsManager.query(searchTerm).map(new Func1<List<SearchResult>, ProviderResponse>() {
                @Override
                public ProviderResponse call(List<SearchResult> searchResults) {
                    return new ProviderResponse(RecentsManager.class.getSimpleName(), searchResults);
                }
            }).onErrorReturn(new Func1<Throwable, ProviderResponse>() {
                        @Override
                        public ProviderResponse call(Throwable throwable) {
                            return new ProviderResponse(RecentsManager.class.getSimpleName(), throwable);
                        }
                    }), new Func2<SearchBundle, ProviderResponse, SearchBundle>() {
                @Override
                public SearchBundle call(SearchBundle searchBundle, ProviderResponse localMatches) {
                    if (localMatches.hasError()) {
                        return new SearchBundle(localMatches, searchBundle.getRemainingResponses());
                    } else {
                        // TODO: handle recent scoring here for that perfect ordering ;)
                        List<SearchResult> recentsPass2 = concateExcludeDuplicates(localMatches.getSearchResults(),
                                searchBundle.getRecents());
                        final String sourceRecents = RecentsManager.class.getSimpleName();
                        return new SearchBundle(new ProviderResponse(sourceRecents, recentsPass2),
                                searchBundle.getRemainingResponses());
                    }
                }
            });
        }
    }

    private List<SearchResult> concateExcludeDuplicates(List<SearchResult> list1,
                                                        List<SearchResult> list2) {
        List<SearchResult> dest = new ArrayList<>(list1.size());
        dest.addAll(list1);

        Map<String, SearchResult> index = new HashMap<>();
        for (SearchResult result : list1) {
            index.put(result.getId(), result);
        }

        for (SearchResult searchResult : list2) {
            if (!index.containsKey(searchResult.getId())) {
                dest.add(searchResult);
            }
        }
        return dest;
    }
}

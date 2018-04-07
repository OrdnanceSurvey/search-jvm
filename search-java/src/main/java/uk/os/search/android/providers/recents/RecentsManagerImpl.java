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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.CompletableSubject;
import uk.os.search.SearchResult;
import uk.os.search.android.providers.Provider;

/**
 * In-memory RecentsManager
 *
 * Mostly useful for demos, testing etc. as a better recents might use a persistent storage
 */
public class RecentsManagerImpl implements Provider, RecentsManager {

    private Map<String, SearchResult> mIndex = Collections.synchronizedMap(new HashMap<String, SearchResult>());
    private List<SearchResult> mSearchResults = Collections.synchronizedList(new ArrayList<SearchResult>());

    @Override
    public Observable<List<SearchResult>> last(int maxResults) {
        synchronized (mSearchResults) {
            // Note: sublist is only a view and therefore must be copied out.
            // http://stackoverflow.com/questions/27925960/concurrent-modification-exception-for-this-code-block-help-please
            final List<SearchResult> results = new ArrayList<>(mSearchResults.subList(0,
                    Math.min(mSearchResults.size(), maxResults - 1)));
            Collections.reverse(results);
            return Observable.just(results);
        }
    }

    @Override
    public Observable<List<SearchResult>> query(String searchTerm) {
        final String term = sanitised(searchTerm);

        return Observable.create(new ObservableOnSubscribe<List<SearchResult>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SearchResult>> emitter) throws Exception {
                try {
                    List<SearchResult> results = new ArrayList<>();
                    synchronized (mSearchResults) {
                        for (SearchResult result : mSearchResults) {
                            String lower = sanitised(result.getName() + " " + result.getContext());
                            if (lower.contains(term)) {
                                results.add(result);
                            }
                        }
                    }
                    if (!emitter.isDisposed()) {
                        Collections.reverse(results);
                        emitter.onNext(results);
                    }
                    if (!emitter.isDisposed()) {
                        emitter.onComplete();
                    }
                } catch (Exception e) {
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }

            }
        })
            .onErrorReturn(new Function<Throwable, List<SearchResult>>() {
                @Override
                public List<SearchResult> apply(Throwable throwable) throws Exception {
                    return Collections.emptyList();
                }
            })
            .subscribeOn(Schedulers.newThread());
    }

    @Override
    public Observable<List<SearchResult>> queryById(final String... ids) {
        return Observable.create(new ObservableOnSubscribe<List<SearchResult>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SearchResult>> emitter) throws Exception {
                try {
                    List<SearchResult> results = new ArrayList<>();
                    synchronized (mSearchResults) {
                        for (String id : ids) {
                            if (mIndex.containsKey(id)) {
                                results.add(mIndex.get(id));
                            }
                        }
                    }
                    if (!emitter.isDisposed()) {
                        emitter.onNext(results);

                    }
                    if (!emitter.isDisposed()) {
                        emitter.onComplete();
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }

    @Override
    public Completable saveRecent(final SearchResult searchResult) {
        //DisposableCompletableObserver
        final CompletableSubject subject = CompletableSubject.create();

        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                try {
                    synchronized (mSearchResults) {
                        String key = searchResult.getId();
                        boolean alreadyThere = mIndex.containsKey(searchResult.getId());

                        if (alreadyThere) {
                            // remove it to fudge ordering in this demo
                            SearchResult existing = mIndex.get(key);
                            mSearchResults.remove(existing);
                            mIndex.remove(key);
                        }

                        mSearchResults.add(searchResult);
                        mIndex.put(searchResult.getId(), searchResult);
                        if (mSearchResults.size() > 100) {
                            SearchResult oldest = mSearchResults.get(0);
                            String oldestKey = oldest.getId();
                            mSearchResults.remove(oldest);
                            mIndex.remove(oldestKey);
                        }
                    }

                    if (!emitter.isDisposed()) {
                        emitter.onComplete();
                    }
                } catch (Exception e) {
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread())
        .subscribe(subject);
        return subject;
    }

    @Override
    public Completable updateRecent(final SearchResult latest) {
        CompletableSubject subject = CompletableSubject.create();

        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                try {
                    synchronized (mSearchResults) {
                        for (int i = 0; i < mSearchResults.size(); i++) {
                            SearchResult searchResult = mSearchResults.get(i);
                            String id = searchResult.getId() != null ? searchResult.getId() : "";
                            String latestId = latest.getId() != null ? latest.getId() : "";
                            if (id.equals(latestId)) {
                                mSearchResults.set(i, latest);
                                break;
                            }
                        }
                    }
                    if (!emitter.isDisposed()) {
                        emitter.onComplete();
                    }
                } catch (Exception e) {
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }
            }
        })
            .subscribeOn(Schedulers.newThread())
            .subscribe(subject);
        return subject;
    }

    private String sanitised(String string) {
        return string.toLowerCase(Locale.getDefault());
    }
}

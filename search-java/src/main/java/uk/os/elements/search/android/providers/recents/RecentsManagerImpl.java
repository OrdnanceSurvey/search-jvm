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

package uk.os.elements.search.android.providers.recents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;
import uk.os.elements.search.SearchResult;
import uk.os.elements.search.android.providers.Provider;

public class RecentsManagerImpl implements Provider, RecentsManager {

    // TODO: either use guava or rationalise what is happening
    private Map<String, SearchResult> mIndex = new HashMap<>();
    private List<SearchResult> mSearchResults = new ArrayList<>();

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

        return Observable.create(new Observable.OnSubscribe<List<SearchResult>>() {
            @Override
            public void call(Subscriber<? super List<SearchResult>> subscriber) {
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
                    if (!subscriber.isUnsubscribed()) {
                        Collections.reverse(results);
                        subscriber.onNext(results);
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        }).onErrorReturn(new Func1<Throwable, List<SearchResult>>() {
            @Override
            public List<SearchResult> call(Throwable throwable) {
                return Collections.emptyList();
            }
        })
        .subscribeOn(Schedulers.newThread());
    }

    @Override
    public Observable<List<SearchResult>> queryById(final String... ids) {
        return Observable.create(new Observable.OnSubscribe<List<SearchResult>>() {
            @Override
            public void call(Subscriber<? super List<SearchResult>> subscriber) {
                try {
                    List<SearchResult> results = new ArrayList<>();

                    synchronized (mSearchResults) {
                        for (String id : ids) {
                            if (mIndex.containsKey(id)) {
                                results.add(mIndex.get(id));
                            }
                        }
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(results);

                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }

    @Override
    public Observable<Void> saveRecent(final SearchResult searchResult) {
        final AsyncSubject<Void> subject = AsyncSubject.create();
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
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

                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onCompleted();
                        }
                    }
                } catch (Exception e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread())
        .subscribe(subject);
        return subject;
    }

    @Override
    public Observable<Void> updateRecent(final SearchResult latest) {
        final AsyncSubject<Void> subject = AsyncSubject.create();
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
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
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onCompleted();
                        }
                    }
                } catch (Exception e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        })
        .subscribeOn(Schedulers.newThread())
        .subscribe(subject);
        return subject;
    }

    private String sanitised(String string) {
        return string.toLowerCase();
    }
}

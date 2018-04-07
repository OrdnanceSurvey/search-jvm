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

package uk.os.search.android.recentmanager.impl.provider;

import android.content.Context;
import android.database.Cursor;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.squareup.sqlbrite3.BriteContentResolver;
import com.squareup.sqlbrite3.SqlBrite;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.CompletableSubject;
import uk.os.search.SearchResult;
import uk.os.search.android.ContentObservable;
import uk.os.search.android.providers.bng.OsGridReference;
import uk.os.search.android.providers.latlon.LatLonResult;
import uk.os.search.android.providers.recents.RecentsManager;
import uk.os.search.android.recentmanager.impl.provider.content.searchresult.SearchResultColumns;
import uk.os.search.android.recentmanager.impl.provider.content.searchresult.SearchResultContentValues;
import uk.os.search.android.recentmanager.impl.provider.content.searchresult.SearchResultSelection;

public class RecentsManagerImpl implements RecentsManager {

    private final Context mContext;
    private final BriteContentResolver mResolver;

    public RecentsManagerImpl(Context context) {
        mContext = context;

        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        mResolver = sqlBrite.wrapContentProvider(context.getContentResolver(), Schedulers.io());
    }

    @Override
    public Observable<List<SearchResult>> query(String query) {
        final String term = sanitised(query);

        String restriction = String.format(Locale.getDefault(), "lower(%s || %s) LIKE ?",
                SearchResultColumns.NAME, SearchResultColumns.CONTEXT);
        String[] restrictionArgs = new String[]{"%" + term + "%"};

        return mResolver.createQuery(SearchResultColumns.CONTENT_URI, null,
            restriction, restrictionArgs, SearchResultColumns.ACCESSED + " DESC", false).mapToList(new Function<Cursor, SearchResult>() {
            @Override
            public SearchResult apply(Cursor cursor) throws Exception {
                String featureId = cursor
                    .getString(cursor.getColumnIndex(SearchResultColumns.FEATUREID));
                String name = cursor.getString(cursor.getColumnIndex(SearchResultColumns.NAME));
                String context = cursor
                    .getString(cursor.getColumnIndex(SearchResultColumns.CONTEXT));
                Double x = cursor.getDouble(cursor.getColumnIndex(SearchResultColumns.X));
                Double y = cursor.getDouble(cursor.getColumnIndex(SearchResultColumns.Y));
                int srid = cursor.getInt(cursor.getColumnIndex(SearchResultColumns.SRID));
                Double minX = cursor.getDouble(cursor.getColumnIndex(SearchResultColumns.MINX));
                Double minY = cursor.getDouble(cursor.getColumnIndex(SearchResultColumns.MINY));
                Double maxX = cursor.getDouble(cursor.getColumnIndex(SearchResultColumns.MAXX));
                Double maxY = cursor.getDouble(cursor.getColumnIndex(SearchResultColumns.MAXY));
                return new SearchResult(featureId, name, context, new Point(x, y),
                    new Envelope(minX, minY, maxX, maxY), SpatialReference.create(srid));
            }
        }).subscribeOn(Schedulers.newThread()).doOnNext(new Consumer<List<SearchResult>>() {
          @Override
          public void accept(List<SearchResult> searchResults) throws Exception {
            System.out.println("Results: " + searchResults.size());
          }
        });
    }

    @Override
    public Observable<List<SearchResult>> queryById(String... ids) {
      SearchResultSelection selection = new SearchResultSelection();
      selection.featureid(ids);
      Cursor cursor = selection.query(mContext.getContentResolver());

      if (cursor == null) {
        List<SearchResult> empty = Collections.emptyList();
        return Observable.just(empty);
      }

      return ContentObservable.fromCursor(cursor).map(new Function<Cursor, SearchResult>() {
        @Override
        public SearchResult apply(Cursor cursor) throws Exception {
          return from(cursor);
        }
      }).toList().toObservable().subscribeOn(Schedulers.newThread());
    }

    @Override
    public Observable<List<SearchResult>> last(int i) {
        SearchResultSelection selection = new SearchResultSelection();
        selection.limit(i);
        Cursor cursor = selection.query(mContext.getContentResolver(), null,
                SearchResultColumns.ACCESSED + " DESC");

        if (cursor == null) {
            List<SearchResult> empty = Collections.emptyList();
            return Observable.just(empty);
        }

        return ContentObservable.fromCursor(cursor).map(new Function<Cursor, SearchResult>() {
          @Override
          public SearchResult apply(Cursor cursor) throws Exception {
            return from(cursor);
          }
        }).toList().toObservable().subscribeOn(Schedulers.newThread());
    }

    @Override
    public Completable saveRecent(final SearchResult searchResult) {
      final CompletableSubject subject = CompletableSubject.create();

        Completable.fromAction(new Action() {
          @Override
          public void run() throws Exception {
            setRecent(searchResult);
          }
        }).subscribeOn(Schedulers.newThread()).subscribe(subject);
        return subject;
    }

    @Override
    public Completable updateRecent(final SearchResult searchResult) {
      final CompletableSubject subject = CompletableSubject.create();

      Completable.fromAction(new Action() {
        @Override
        public void run() throws Exception {
          SearchResultContentValues values = getValues(searchResult);
          SearchResultSelection selection = new SearchResultSelection();
          selection.featureid(searchResult.getId());
          values.update(mContext.getContentResolver(), selection);
        }
      }).subscribeOn(Schedulers.newThread()).subscribe(subject);
      return subject;
    }

    private boolean anyNull(Cursor cursor, String[] columns) {
        for (String column : columns) {
            if (cursor.isNull(cursor.getColumnIndex(column))) {
                return true;
            }
        }
        return false;
    }

    private SearchResult from(Cursor cursor) {
        String featureId = cursor.getString(cursor.getColumnIndex(SearchResultColumns.FEATUREID));
        String name = cursor.getString(cursor.getColumnIndex(SearchResultColumns.NAME));
        String context = cursor.getString(cursor.getColumnIndex(SearchResultColumns.CONTEXT));
        Double x = cursor.getDouble(cursor.getColumnIndex(SearchResultColumns.X));
        Double y = cursor.getDouble(cursor.getColumnIndex(SearchResultColumns.Y));
        int srid = cursor.getInt(cursor.getColumnIndex(SearchResultColumns.SRID));
        Envelope envelope = getEnvelope(cursor);
        String type = cursor.getString(cursor.getColumnIndex(SearchResultColumns.TYPE));
        if (type.equalsIgnoreCase(LatLonResult.class.getSimpleName())) {
            return new LatLonResult(featureId, name, context, new Point(x, y), envelope,
                    SpatialReference.create(srid));
        } else if (type.equalsIgnoreCase(OsGridReference.class.getSimpleName())) {
            return new OsGridReference(name, x.intValue(), y.intValue(), envelope);
        } else {
            return new SearchResult(featureId, name, context, new Point(x, y), envelope,
                    SpatialReference.create(srid));
        }
    }

    private Envelope getEnvelope(Cursor cursor) {
        String[] envelopeColumns = new String[]{SearchResultColumns.MINX, SearchResultColumns.MINY,
                SearchResultColumns.MAXX, SearchResultColumns.MAXY};
        boolean hasEmptyEnvelope = anyNull(cursor, envelopeColumns);
        if (hasEmptyEnvelope) {
            return new Envelope();
        }
        Double minX = cursor.getDouble(cursor.getColumnIndex(SearchResultColumns.MINX));
        Double minY = cursor.getDouble(cursor.getColumnIndex(SearchResultColumns.MINY));
        Double maxX = cursor.getDouble(cursor.getColumnIndex(SearchResultColumns.MAXX));
        Double maxY = cursor.getDouble(cursor.getColumnIndex(SearchResultColumns.MAXY));
        return new Envelope(minX, minY, maxX, maxY);
    }

    private SearchResultContentValues getValues(SearchResult searchResult) {
        SearchResultContentValues values = new SearchResultContentValues();
        values.putType(searchResult.getClass().getSimpleName());
        values.putAccessed(System.currentTimeMillis());
        values.putFeatureid(searchResult.getId());
        values.putName(searchResult.getName());
        values.putContext(searchResult.getContext());
        Point point = searchResult.getPoint();
        values.putX(point.getX());
        values.putY(point.getY());
        values.putSrid(searchResult.getSpatialReference().getID());
        Envelope envelope = searchResult.getEnvelope();
        if (envelope != null) {
            if (envelope.isEmpty()) {
                values.putMinxNull();
                values.putMinyNull();
                values.putMaxxNull();
                values.putMaxyNull();
            } else {
                values.putMinx(envelope.getXMin());
                values.putMiny(envelope.getYMin());
                values.putMaxx(envelope.getXMax());
                values.putMaxy(envelope.getYMax());
            }
        }
        return values;
    }

    private String sanitised(String string) {
        return string.toLowerCase(Locale.getDefault());
    }

    private Void setRecent(SearchResult searchResult) {
        SearchResultContentValues values = getValues(searchResult);
        mContext.getContentResolver().insert(values.uri(), values.values());
        return null;
    }
}

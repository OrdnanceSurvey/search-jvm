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

package uk.os.search.android.providers.addresses;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import rx.Observable;
import uk.os.search.SearchResult;
import uk.os.search.android.providers.addresses.service.model.Header;
import uk.os.search.android.providers.addresses.service.model.Result;
import uk.os.search.android.providers.addresses.service.model.ServerResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TransformTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenEmptyGazetteerData() {
        ServerResponse serverResponse = new ServerResponse() {
            @Override
            public Header getHeader() {
                return new Header();
            }

            @Override
            public List<Result> getResults() {
                Result r = null;
                return Arrays.asList(r);
            }
        };
        Observable<ServerResponse> response = Observable.just(serverResponse);
        List<Observable<ServerResponse>> list = new ArrayList<>();
        list.add(response);
        Transform.toSearchResults(list).toBlocking().first();
    }

    @Test
    public void shouldTransformServerResponseToSearchResults() {
        Observable<ServerResponse> response = Observable.just(Util.getServerResponse());
        List<Observable<ServerResponse>> list = new ArrayList<>();
        list.add(response);
        Observable<List<SearchResult>> serverResults = Transform.toSearchResults(list);
        List<SearchResult> results = serverResults.toBlocking().first();
        assertTrue(results.size() == 1);

        SearchResult result = results.get(0);

        // Check id
        String expected = "123456789";
        String actual = result.getId();
        assertEquals(expected, actual);

        // Check name
        expected = "Ordnance Survey";
        actual = result.getName();
        assertEquals(expected, actual);

        // Check context
        expected = "Adanac Drive, Southampton, United Kingdom, SO16 0AS";
        actual = result.getContext();
        assertEquals(expected, actual);

        // Check point
        Point expectedPoint = new Point(-1.470691, 50.938015);
        Point actualPoint = result.getPoint();
        assertEquals(expectedPoint, actualPoint);

        // Check envelope is empty as point result
        Envelope actualEnvelope = result.getEnvelope();
        assertTrue(actualEnvelope.isEmpty());

        // Verify EPSG:4326 result
        SpatialReference spatialReference = result.getSpatialReference();
        assertTrue(spatialReference.getID() == 4326);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForInvalidSrid() {
        ServerResponse sr = new ServerResponse() {
            @Override
            public Header getHeader() {
                return new Header() {
                    @Override
                    public String getOutput_srs() {
                        return "invalid:4326";
                    }
                };
            }
        };
        Observable<ServerResponse> response = Observable.just(sr);

        // unsupported srid exception
        Transform.toSearchResults(Arrays.asList(response)).toBlocking().first();
    }

    @Test
    public void shouldUseEllipsisWhenNonMatchingFeatureNameAndLongerThan10() {
        ServerResponse sr = Mockito.mock(ServerResponse.class);
        final Result r = Mockito.mock(Result.class);
        final Result.Dpa d = Mockito.mock(Result.Dpa.class);

        when(sr.getHeader()).then(new Answer<Header>() {
            @Override
            public Header answer(InvocationOnMock invocation) throws Throwable {
                return new Header(){
                    @Override
                    public String getOutput_srs() {
                        return "EPSG:4326";
                    }
                };
            }
        });
        when(sr.getResults()).then(new Answer<List<Result>>() {
            @Override
            public List<Result> answer(InvocationOnMock invocation) throws Throwable {
                return Arrays.asList(r);
            }
        });

        when(r.getGazetteerEntry()).then(new Answer<Result.Dpa>() {
            @Override
            public Result.Dpa answer(InvocationOnMock invocation) throws Throwable {
                return d;
            }
        });

        when(d.getADDRESS()).then(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "Unusual Feature Address without commas";
            }
        });
        when(d.getX_COORDINATE()).then(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "-0.1";
            }
        });
        when(d.getY_COORDINATE()).then(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "51.1";
            }
        });

        List<SearchResult> result = Transform.toSearchResults(Arrays.asList(Observable.just(sr))).toBlocking().first();
        SearchResult searchResult = result.get(0);

        // evidently further consideration could be made
        String nameExpected = "Unusual Fe...";
        String nameActual = searchResult.getName();
        String contextExpected = "ature Address without comma";
        String contextActual = searchResult.getContext();
        assertEquals(nameExpected, nameActual);
        assertEquals(contextExpected, contextActual);
    }
}

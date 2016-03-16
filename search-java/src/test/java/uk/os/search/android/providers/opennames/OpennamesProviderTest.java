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

package uk.os.search.android.providers.opennames;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import rx.Observable;
import uk.os.search.android.providers.opennames.service.SearchApi;
import uk.os.search.android.providers.opennames.service.model.ServerResponse;

import static org.mockito.Mockito.*;

public class OpennamesProviderTest {

    private final static String API_KEY = "niceTry";
    private final static String LONDON = "London";
    private final static String EMPTY = "";

    @Test
    public void shouldQueryBackendEvenIfEmpty() {
        SearchApi searchApi = Mockito.mock(SearchApi.class);
        when(searchApi.search(anyString(), anyString())).then(new Answer<Observable<ServerResponse>>() {
            @Override
            public Observable<ServerResponse> answer(InvocationOnMock invocation) throws Throwable {
                return Observable.just(new ServerResponse());
            }
        });

        OpennamesProvider opennamesProvider = new OpennamesProvider.Builder(API_KEY).setSearchApi(searchApi).build();
        opennamesProvider.query(LONDON).toBlocking().first();
        verify(searchApi, times(1)).search(API_KEY, LONDON);

        opennamesProvider.query(EMPTY).toBlocking().first();
        verify(searchApi, times(1)).search(API_KEY, EMPTY);
    }
}

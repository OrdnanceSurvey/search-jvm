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

package uk.os.search.android.recentmanager.impl.provider.content;

import java.lang.reflect.Field;

import timber.log.Timber;

public class SearchContract {
    public static final String CONTENT_AUTHORITY = initAuthority();

    private static String initAuthority() {
        String authority = "uk.os.search";

        try {
            ClassLoader loader = SearchContract.class.getClassLoader();

            Class<?> clz = loader.loadClass("uk.os.search.android.recentmanager.impl.provider.SearchContentProviderAuthority");
            Field declaredField = clz.getDeclaredField("CONTENT_AUTHORITY");

            authority = declaredField.get(null).toString();
        } catch (Exception e) {
            Timber.e(e, "problem initialising the content provider authority");
        }

        return authority;
    }
}

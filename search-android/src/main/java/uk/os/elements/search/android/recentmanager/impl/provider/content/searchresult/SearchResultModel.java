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

package uk.os.elements.search.android.recentmanager.impl.provider.content.searchresult;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uk.os.elements.search.android.recentmanager.impl.provider.content.base.BaseModel;

/**
 * The Serialized SearchResult
 */
public interface SearchResultModel extends BaseModel {

    /**
     * the type of search result
     * Cannot be {@code null}.
     */
    @NonNull
    String getType();

    /**
     * the unique id of the search result
     * Cannot be {@code null}.
     */
    @NonNull
    String getFeatureid();

    /**
     * the timestamp that search result was last pressed
     * Can be {@code null}.
     */
    @Nullable
    Long getAccessed();

    /**
     * feature name
     * Can be {@code null}.
     */
    @Nullable
    String getName();

    /**
     * feature geo-context
     * Can be {@code null}.
     */
    @Nullable
    String getContext();

    /**
     * Get the {@code x} value.
     * Can be {@code null}.
     */
    @Nullable
    Double getX();

    /**
     * Get the {@code y} value.
     * Can be {@code null}.
     */
    @Nullable
    Double getY();

    /**
     * Get the {@code srid} value.
     * Can be {@code null}.
     */
    @Nullable
    Integer getSrid();

    /**
     * Get the {@code minx} value.
     * Can be {@code null}.
     */
    @Nullable
    Double getMinx();

    /**
     * Get the {@code miny} value.
     * Can be {@code null}.
     */
    @Nullable
    Double getMiny();

    /**
     * Get the {@code maxx} value.
     * Can be {@code null}.
     */
    @Nullable
    Double getMaxx();

    /**
     * Get the {@code maxy} value.
     * Can be {@code null}.
     */
    @Nullable
    Double getMaxy();
}

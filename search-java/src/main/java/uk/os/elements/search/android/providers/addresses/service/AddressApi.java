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

package uk.os.elements.search.android.providers.addresses.service;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import uk.os.elements.search.android.providers.addresses.service.model.ServerResponse;

/**
 * Java interface to the Places API
 *
 * TODO: consider adding remaining parameters
 *
 * @see <a href="https://apidocs.os.uk/docs/os-places-overview">https://apidocs.os.uk/docs/os-places-overview</a>
 * @see <a href="https://apidocs.os.uk/docs/os-places-dpa-output">https://apidocs.os.uk/docs/os-places-dpa-output</a>
 */
public interface AddressApi {

    /**
     * Query the find API
     * https://api.ordnancesurvey.co.uk/places/v1/addresses/find?query={@param value}&output_srs=EPSG:4326&key={@param apiKey}
     * @param apiKey valid OS Places API key
     * @param value the query term, e.g. London Road Southampton
     * @return matching address candidates
     */
    @GET("/places/v1/addresses/find?maxresults=25")
    Observable<ServerResponse> find(@Query("key") String apiKey, @Query("query") String value);


    /**
     * Query the postcode API
     * https://api.ordnancesurvey.co.uk/places/v1/addresses/postcode?postcode={@param value}&output_srs=EPSG:4326&dataset=dpa&key={@param apiKey}
     * @param apiKey valid OS Places API key, e.g. SO16 or SO16 0AS
     * @param value the query term
     * @return matching address candidates
     */
    @GET("/places/v1/addresses/postcode?maxresults=25")
    Observable<ServerResponse> postcode(@Query("key") String apiKey, @Query("postcode") String value);


    /**
     * Query the bounding box API
     * https://api.ordnancesurvey.co.uk/places/v1/addresses/bbox?bbox={@param bbox}&srs=EPSG:4326&dataset=dpa&key={@param apiKey}
     * @param apiKey valid OS Places API key
     * @param bbox describing the lower left and upper right point lat/lon coordinates, e.g. 51.631876,-1.40,51.64,-1.39
     * @return matching address candidates
     */
    @GET("/places/v1/addresses/bbox?srs=EPSG:4326&dataset=dpa")
    Observable<ServerResponse> bbox(@Query("key") String apiKey,
                                    @Query("bbox") String bbox);

    /**
     * Query the Unique Property Reference Number (UPRN) API
     * https://api.ordnancesurvey.co.uk/places/v1/addresses/uprn?uprn={@param value}&dataset=dpa&output_srs=EPSG:4326&key={@param value}
     * @param apiKey valid OS Places API key
     * @param value the full UPRN value, e.g. 10012093013
     * @return matching address candidates
     */
    @GET("/places/v1/addresses/uprn?dataset=dpa&output_srs=EPSG:4326")
    Observable<ServerResponse> uprn(@Query("key") String apiKey, @Query("uprn") String value);


    /**
     * https://api.ordnancesurvey.co.uk/places/v1/addresses/radius?point={@param point}&srs=EPSG:4326&radius=190&dataset=dpa&key={@param apiKey}
     * @param apiKey valid OS Places API key
     * @param point the centroid of the buffer, e.g. 50.938089,-1.470624
     * @return matching address candidates
     */
    @GET("/places/v1/addresses/radius?srs=EPSG:4326&radius=190&dataset=dpa")
    Observable<ServerResponse> radius(@Query("key") String apiKey, @Query("point") String point);


    /**
     * Query the nearest address API
     * Provides the nearest address to a given coordinate
     * https://api.ordnancesurvey.co.uk/places/v1/addresses/nearest?point={@param point}&srs=EPSG:4326&key={@param apiKey}
     * @param apiKey valid OS Places API key
     * @param point the input coordinate for the nearest query, e.g. 50.938089,-1.470624
     * @return matching address candidates
     */
    @GET("/places/v1/addresses/nearest?srs=EPSG:4326")
    Observable<ServerResponse> nearest(@Query("key") String apiKey, @Query("point") String point);
}

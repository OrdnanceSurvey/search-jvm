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
 * Java interface to the OS Places API
 *
 * OS Places searches in real time the most comprehensive address database in Great Britain – with over 39 million
 * entries – AddressBase Premium.
 *
 * Supported SRS values are: EPSG:27700, EPSG:4326, EPSG:3857, EPSG:4258
 *
 * @see <a href="https://www.ordnancesurvey.co.uk/business-and-government/products/os-places/">https://www.ordnancesurvey.co.uk/business-and-government/products/os-places/</a>
 * @see <a href="https://apidocs.os.uk/docs/os-places-overview">https://apidocs.os.uk/docs/os-places-overview</a>
 * @see <a href="https://apidocs.os.uk/docs/os-places-dpa-output">https://apidocs.os.uk/docs/os-places-dpa-output</a>
 */
public interface AddressApi {

    /**
     * Query the find API
     * <a href="https://apidocs.os.uk/docs/os-places-find">https://apidocs.os.uk/docs/os-places-find</a>
     *
     * {@code https://api.ordnancesurvey.co.uk/places/v1/addresses/find?query={value}&output_srs=EPSG:4326&key={apiKey}}
     *
     * @param apiKey valid OS Places API key
     * @param value the query term, e.g. London Road Southampton
     * @param maxResults the maximum number of results to be returned - maximum 100
     * @param outputSrs the intended output spatial reference system, e.g. EPSG:4326
     * @return matching address candidates
     */
    @GET("/places/v1/addresses/find")
    Observable<ServerResponse> find(@Query("key") String apiKey,
                                    @Query("query") String value,
                                    @Query("maxresults") int maxResults,
                                    @Query("output_srs") String outputSrs);


    /**
     * Query the postcode API
     * <a href="https://apidocs.os.uk/docs/os-places-postcode">https://apidocs.os.uk/docs/os-places-postcode</a>
     *
     * {@code https://api.ordnancesurvey.co.uk/places/v1/addresses/postcode?postcode={value}&output_srs=EPSG:4326&dataset=dpa&key={apiKey}}
     *
     * @param apiKey valid OS Places API key, e.g. SO16 or SO16 0AS
     * @param value the query term
     * @param maxResults the maximum number of results to be returned - maximum 100
     * @param outputSrs the intended output spatial reference system, e.g. EPSG:4326
     * @return matching address candidates
     */
    @GET("/places/v1/addresses/postcode")
    Observable<ServerResponse> postcode(@Query("key") String apiKey,
                                        @Query("postcode") String value,
                                        @Query("maxresults") int maxResults,
                                        @Query("output_srs") String outputSrs);


    /**
     * Query the bounding box API
     * <a href="https://apidocs.os.uk/docs/os-places-bbox">https://apidocs.os.uk/docs/os-places-bbox</a>
     *
     * {@code https://api.ordnancesurvey.co.uk/places/v1/addresses/bbox?bbox={bbox}&srs=EPSG:4326&dataset=dpa&key={apiKey}}
     *
     * Note: whist the web API supports DPA and LPI datasets, provided by the Royal Mail and local authority
     *       respectively, this Java implementation only supports the DPA dataset due to different data models.
     *
     * @param apiKey valid OS Places API key
     * @param bbox describing the lower left and upper right point lat/lon coordinates, e.g. 51.631876,-1.40,51.64,-1.39
     * @param srs the input spatial reference system for the input coordinates, e.g. EPSG:4326
     * @param outputSrs the intended output spatial reference system, e.g. EPSG:4326
     * @return matching address candidates
     */
    @GET("/places/v1/addresses/bbox?dataset=dpa")
    Observable<ServerResponse> bbox(@Query("key") String apiKey,
                                    @Query("bbox") String bbox,
                                    @Query("srs") String srs,
                                    @Query("output_srs") String outputSrs);

    /**
     * Query the Unique Property Reference Number (UPRN) API
     * <a href="https://apidocs.os.uk/docs/os-places-uprn">https://apidocs.os.uk/docs/os-places-uprn</a>
     *
     * {@code https://api.ordnancesurvey.co.uk/places/v1/addresses/uprn?uprn={value}&dataset=dpa&output_srs=EPSG:4326&key={value}}
     *
     * @param apiKey valid OS Places API key
     * @param value the full UPRN value, e.g. 10012093013
     * @param outputSrs the intended output spatial reference system, e.g. EPSG:4326
     * @return matching address candidates
     */
    @GET("/places/v1/addresses/uprn?dataset=dpa")
    Observable<ServerResponse> uprn(@Query("key") String apiKey,
                                    @Query("uprn") String value,
                                    @Query("output_srs") String outputSrs);


    /**
     * Query the radius API
     * <a href="https://apidocs.os.uk/docs/os-places-radius">https://apidocs.os.uk/docs/os-places-radius</a>
     *
     * {@code https://api.ordnancesurvey.co.uk/places/v1/addresses/radius?point={point}&srs=EPSG:4326&radius=190&dataset=dpa&key={apiKey}}
     *
     * @param apiKey valid OS Places API key
     * @param point the centroid of the buffer, e.g. 50.938089,-1.470624
     * @param srs = the spatial reference system for the input coordinate set, e.g. EPSG:4326
     * @param radius buffer radius specified in meters (two decimal places may be provided to achieve cm accuracy)
     * @param outputSrs the intended output spatial reference system, e.g. EPSG:4326
     * @return matching address candidates
     */
    @GET("/places/v1/addresses/radius?srs=EPSG:4326&radius=190&dataset=dpa")
    Observable<ServerResponse> radius(@Query("key") String apiKey,
                                      @Query("point") String point,
                                      @Query("srs") String srs,
                                      @Query("radius") float radius,
                                      @Query("output_srs") String outputSrs);


    /**
     * Query the nearest address API
     * Provides the nearest address to a given coordinate
     * <a href="https://apidocs.os.uk/docs/os-places-nearest">https://apidocs.os.uk/docs/os-places-nearest</a>
     *
     * {@code https://api.ordnancesurvey.co.uk/places/v1/addresses/nearest?point={point}&srs=EPSG:4326&key={apiKey}}
     *
     * @param apiKey valid OS Places API key
     * @param point the input coordinate for the nearest query, e.g. 50.938089,-1.470624
     * @param srs = the spatial reference system for the input coordinate set, e.g. EPSG:4326
     * @param outputSrs the intended output spatial reference system, e.g. EPSG:4326
     * @return matching address candidates
     */
    @GET("/places/v1/addresses/nearest")
    Observable<ServerResponse> nearest(@Query("key") String apiKey,
                                       @Query("point") String point,
                                       @Query("srs") String srs,
                                       @Query("output_srs") String outputSrs);
}

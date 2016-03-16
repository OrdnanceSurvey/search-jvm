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

package uk.os.search.android.providers.opennames.service.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * generated code
 */
public class ServerResponse implements Serializable {

    private static final long serialVersionUID = -1477961267112066338L;

    @SerializedName("header") private Header mHeader;
    @SerializedName("results") private List<Result> mResults = Collections.emptyList();

    public Header getHeader() {
        return mHeader;
    }

    public List<Result> getResults() {
        return mResults;
    }
}

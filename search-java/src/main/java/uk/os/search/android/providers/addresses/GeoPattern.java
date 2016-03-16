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

import java.util.regex.Pattern;

final class GeoPattern {

    private GeoPattern() { }

    /**
     * A Unique Property Reference Number (UPRN) is the persistent unique identifier that underpins the AddressBase
     * products range.
     *
     * UPRNs are integers (numbers) that can be up to 12 digits in length; they can therefore be less than 12 digits
     * long and do not require leading zeros. However some gazetteer database applications will pad UPRNs that are less
     * than 12 digits with zeros.
     *
     * @see <a href="https://www.ordnancesurvey.co.uk/business-and-government/help-and-support/products/addressbase-technical-faqs.html">https://www.ordnancesurvey.co.uk/business-and-government/help-and-support/products/addressbase-technical-faqs.html</a>
     */
    private static final Pattern UPRN_PATTERN = Pattern.compile("(^\\d{1,12}$)");

    /**
     * TODO: WARNING - THIS REGEX IS UNAUTHORISED.  MEETING SCHEDULED FOR 2016-02-11
     */
    private static final Pattern POSTCODE_PARTIAL_PATTERN =
            Pattern.compile("^[A-Za-z][A-Za-z]?\\d\\d?[ ]?\\d?[A-Za-z]?[A-Za-z]?$");

    /**
     * @param query String to query
     * @return true is the query should be sent to the postcode endpoint
     */
    protected static boolean isUprnCandidate(String query) {
        return UPRN_PATTERN.matcher(query).matches();
    }

    /**
     * @param query String to query
     * @return true is the query should be sent to the postcode endpoint
     */
    protected static boolean isPostcodeCandidate(String query) {
        return POSTCODE_PARTIAL_PATTERN.matcher(query).matches();
    }
}

// TODO: consider other patterns
// POSTCODE,"(GIR 0AA)|((([A-Z][0-9][0-9]?)|(([A-Z][A-Z][0-9][0-9]?)|(([A-Z][0-9][A-HJKSTUW])|([A-Z][A-Z][0-9][ABEHMNPRVWXY])))) [0-9][A-Z]{2})"
// POSTCODEINCODE,"(GIR)|((([A-Z][0-9][0-9]?)|(([A-Z][A-Z][0-9][0-9]?)|(([A-Z][0-9][A-HJKSTUW])|([A-Z][A-Z][0-9][ABEHMNPRVWXY])))))"
// COLLAPSEDPOSTCODE,"(GIR 0AA)|((([A-Z][0-9][0-9]?)|(([A-Z][A-Z][0-9][0-9]?)|(([A-Z][0-9][A-HJKSTUW])|([A-Z][A-Z][0-9][ABEHMNPRVWXY]))))[0-9][A-Z]{2})"

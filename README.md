## Java Search ##

**Master Build Status:** [![Circle CI](https://circleci.com/gh/OrdnanceSurvey/search-jvm/tree/master.svg?style=svg)](https://circleci.com/gh/OrdnanceSurvey/search-jvm/tree/master)

This library provides search results from:

* Grid References
* Latitude / Longitudes
* [OS Open Names](https://www.ordnancesurvey.co.uk/business-and-government/products/os-open-names-api.html)
* [OS Places](https://www.ordnancesurvey.co.uk/business-and-government/products/os-places/)

It can be easily extended to include other data sources.

## Use it ##

include a gradle reference:

```gradle
    compile 'uk.os.search:search:0.1.0'
```

build the search manager:

```java
    SearchManager searchManager = new SearchManager.Builder()
        .addPlaces("places-api-key")
        .addOpenNames("open-names-api-key")
        .build();
```

Use it:

```java
    String value = "SU400100";

    mSubscription = mSearchManager.query(value)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<SearchBundle>() {
            @Override
            public void call(SearchBundle searchBundle) {
                mSearchPresenter.present(searchBundle);
                mSubscription.unsubscribe();
                mSubscription = null;
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            // note: this should never happen - time will tell
            mSearchPresenter.presentError(throwable);
            mSubscription.unsubscribe();
            mSubscription = null;
            }
        });
```

Some example queries:

* SU41
* SU4010
* SU400100
* SU40001000
* 51.50722, -0.1275
* 51.50722 -0.1275
* 51°N 0°E
* 51°30'N 0°07'W
* 51°30'26.0"N 0°07'39.0"W
* 51° 30' 26" N, 0° 7' 39" W
* 51:30.433, -0:7.65
* 51:30:26.0 -0:07:39.0
* Edinburgh
* London Road, Southampton
* 10 Downing Street, London

(commas and spaces can be used interchangeably)

Examples of output:

 * *name* 51°30'26.0"N 0°07'39.0"W *context* 51.507220 -0.127500
 * *name* SU 4000 1000 *context* Easting: 440000  Northing: 110000
 * *name* SU 400 100 *context* Easting: 440000  Northing: 110000
 * *name* SU4010 *context* Easting: 440000  Northing: 110000
 * *name* SU41 *context* Easting: 440000  Northing: 110000
 * *name* Edinburgh *context* Edinburgh, Scotland, EH1
 * *name* London Road *context* Southampton, South East, England, SO15
 * *name* 10 DOWNING STREET *context* LONDON, CITY OF WESTMINSTER, SW1A 2AA


# Maintainer Repo Configuration #
The repository is integrated with CircleCI and therefore no manual
configuration is necessary.

Should manual configuration become necessary, the maintainer should consider
adding the following to their properties file:

    ~/.gradle/gradle.properties
       mavenUser=SonatypeNexusUser
       mavenPassword=SonatypeNexusPassword
       signing.keyId=mykeyabcd12345abcd
       signing.password=mykeyringpassword
       signing.secretKeyRingFile=/Users/my_username/.gnupg/secring.gpg       


If you are working on a CI environment, such as CircleCI, you can specify
gradle environmental variables using the "ORG_GRADLE_PROJECT" prefix:

    export ORG_GRADLE_PROJECT_mavenUser="nexusUser"
    export ORG_GRADLE_PROJECT_mavenPassword="nexusPassword"

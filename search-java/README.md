# Java Search #

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


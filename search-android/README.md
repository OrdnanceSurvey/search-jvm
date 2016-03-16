# Android Setup #

Download
--------

Download the latest JAR or grab via Gradle:
```groovy
compile 'uk.os.elements.search.android.search-android:search-android:0.1.0'
```
or Maven:
```xml
<dependency>
  <groupId>uk.os.elements.search.android.search-android</groupId>
  <artifactId>search-android</artifactId>
  <version>0.1.0</version>
</dependency>
```

Configure
---------

Reference the SearchActivity or reference your own subclass
```xml
   <activity android:name="uk.os.elements.search.android.app.SearchActivity">
      <meta-data android:name="opennames" android:value="api key" />
      <meta-data android:name="places" android:value="api key" />
   </activity>
```
_No API key yet?  Just remove the meta-data elements and test with BNG or lat / lon values such as '51 0'_

Include a content provider to store recent results
```xml
   <provider android:name="uk.os.elements.search.android.recentmanager.impl.provider.content.RecentsProvider"
             android:authorities="uk.os.elements.search.android.yourauthority.recents"
             android:exported="false" />
```

Specify your authority in Java
```java
package uk.os.elements.search.android.recentmanager.impl.provider;

public class SearchContentProviderAuthority {
   public static final String CONTENT_AUTHORITY = "uk.os.elements.search.android.demo";
}
```


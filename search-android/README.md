# Android Setup #

Download
--------

Download the latest JAR or grab via Gradle:
```groovy
compile 'uk.os.search:android:1.0.0'
```
or Maven:
```xml
<dependency>
  <groupId>uk.os.search</groupId>
  <artifactId>android</artifactId>
  <version>1.0.0</version>
</dependency>
```

Configure
---------

Reference the SearchActivity or reference your own subclass
```xml
   <activity android:name="uk.os.search.android.app.SearchActivity">
      <meta-data android:name="opennames" android:value="api key" />
      <meta-data android:name="places" android:value="api key" />
   </activity>
```
_No API key yet?  Just remove the meta-data elements and test with BNG or lat / lon values such as '51 0'_

Include a content provider to store recent results
```xml
   <provider android:name="uk.os.search.android.recentmanager.impl.provider.content.RecentsProvider"
             android:authorities="uk.os.search.android.yourauthority.recents"
             android:exported="false" />
```

Specify your authority in Java
```java
package uk.os.search.android.recentmanager.impl.provider;

public class SearchContentProviderAuthority {
   public static final String CONTENT_AUTHORITY = "uk.os.search.android.demo";
}
```


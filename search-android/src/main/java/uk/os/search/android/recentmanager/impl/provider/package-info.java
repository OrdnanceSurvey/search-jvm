/**
 * When using {@link uk.os.search.android.recentmanager.impl.provider.RecentsManagerImpl}
 * one must create the following definition in the android application.
 *
 * package uk.os.search.android.recentmanager.impl.provider;
 *
 * public class SearchContentProviderAuthority {
 *      public static final String CONTENT_AUTHORITY = "com.example.app.data.baz";
 * }
 *
 * The application manifest must also be updated to contain
 *
 * <provider
 *      android:name="uk.os.search.android.recentmanager.impl.provider.content.RecentsProvider"
 *      android:authorities="com.example.app.data.baz.recents"
 *      android:exported="false" />
 *
 * Source: http://stackoverflow.com/questions/10790919/android-having-provider-authority-in-the-app-project/10791144#10791144
 */
package uk.os.search.android.recentmanager.impl.provider;
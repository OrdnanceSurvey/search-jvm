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

package uk.os.search.android.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import uk.os.search.android.R;
import uk.os.search.android.providers.bng.GridReferenceProvider;
import uk.os.search.android.providers.latlon.LatLonProvider;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import uk.os.search.SearchBundle;
import uk.os.search.SearchManager;
import uk.os.search.SearchResult;
import uk.os.search.android.providers.Provider;
import uk.os.search.android.providers.recents.RecentsManager;
import uk.os.search.android.recentmanager.impl.provider.RecentsManagerImpl;
import uk.os.search.android.util.RxUtil;
import uk.os.search.android.util.ViewUtil;

public class SearchActivity extends AppCompatActivity {

    private enum State {
        INIT,
        SEARCH,
        RECENTS_ONLY
    }

    private static final int REQUEST_CODE = 1;

    private SearchManager mSearchManager;
    private RecentsManager mRecentsManager;

    private RecyclerView mRecyclerView;
    private EditText mSearchView;
    private Toolbar mToolbar;
    private ImageButton mClear;
    private ImageButton mMic;
    private ImageButton mMicHollow;

    private View mRecents;
    private View mErrorRecents;
    private View mActiveLine;
    private View mEmptyLine;
    private View mErrorNetwork;
    private View mErrorHttp;
    private View mErrorConversion;
    private View mNoSearchResults;
    private View mErrorCatchAll;

    private SearchAdapter mSearchAdapter;
    private boolean mSupportsVoiceRecognition;
    private RecentsOnlyAdapter mRecentsAdapter;
    private QueryConcern mQueryConcern = new QueryConcern();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            if (matches.size() > 0) {
                String first = matches.get(0);
                mSearchView.setText(first);
                if (mSearchView.hasFocus()) {
                    mSearchView.setSelection(mSearchView.getText().length());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        boolean isEmpty = mSearchView.getText().toString().isEmpty();
        if (isEmpty) {
            super.onBackPressed();
        } else {
            onClear();
        }
    }

    /**
     * @return a recents manager to be used by the search activity
     */
    protected RecentsManager getRecentsManager() {
        return new RecentsManagerImpl(this);
    }

    /**
     * @return a list of providers to be searched
     */
    protected List<Provider> getProviders() {
        List<Provider> searchProviders = new ArrayList<>();
        searchProviders.add(new GridReferenceProvider());
        searchProviders.add(new LatLonProvider());
        return searchProviders;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SearchTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        mRecentsManager = getRecentsManager();
        mSearchManager = getSearchManager(mRecentsManager);

        mRecyclerView = (RecyclerView) findViewById(R.id.search_results);
        mSearchView = (EditText) findViewById(R.id.search_searchview);
        mToolbar = (Toolbar) findViewById(R.id.search_toolbar);
        mClear = (ImageButton) findViewById(R.id.search_clear);
        mMic = (ImageButton) findViewById(R.id.search_mic);
        mMicHollow = (ImageButton) findViewById(R.id.search_mic_hollow);
        mRecents = findViewById(R.id.search_recents);
        mErrorRecents = findViewById(R.id.search_error_recents);
        mActiveLine = findViewById(R.id.search_searchview_line);
        mEmptyLine = findViewById(R.id.search_searchview_line_empty);
        mErrorNetwork = findViewById(R.id.search_error_network);
        mErrorHttp = findViewById(R.id.search_error_http);
        mErrorConversion = findViewById(R.id.search_error_conversion);
        mNoSearchResults = findViewById(R.id.search_no_results);
        mErrorCatchAll = findViewById(R.id.search_error_catch_all);

        mSearchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                updateSearchViewState();
            }
        });

        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.search_voice_prompt));
                startActivityForResult(intent, REQUEST_CODE);
            }
        };
        mMic.setOnClickListener(l);
        mMicHollow.setOnClickListener(l);

        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                mQueryConcern.setSearchText(text.toString());
                updateSearchViewState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(android.app.SearchManager.QUERY);
            mSearchView.setText(query);
        }

        // toolbar
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_action_back_light_blue300);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // list (RecyclerView with pleasant dividers)
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext(),
                getResources().getDimensionPixelSize(R.dimen.default_container_margin)));

        // recent results only adapter
        mRecentsAdapter = new RecentsOnlyAdapter(new SearchAdapter.SearchListener() {
            @Override
            public void onSelected(SearchResult searchResult) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", searchResult);
                setResult(RESULT_OK, returnIntent);
                mRecentsManager.saveRecent(searchResult);
                finish();
            }
        });

        // search results only adapter
        mSearchAdapter = new SearchAdapter(this, new SearchAdapter.SearchListener() {
            @Override
            public void onSelected(SearchResult searchResult) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", searchResult);
                setResult(RESULT_OK, returnIntent);
                mRecentsManager.saveRecent(searchResult);
                finish();
            }
        });

        mSearchView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean hasText = !mSearchView.getText().toString().isEmpty();
                boolean upAction = event.getAction() == KeyEvent.ACTION_UP;
                boolean keyEnter = keyCode == KeyEvent.KEYCODE_ENTER;

                if (hasText && upAction && keyEnter) {
                    ViewUtil.closeKeyboard(v);
                    v.cancelLongPress();
                    mQueryConcern.query();
                }
                return false;
            }
        });

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClear();
            }
        });

        mSupportsVoiceRecognition = isVoiceEnabled();

        updateSearchViewState();
        mQueryConcern.init();
    }

    private void onClear() {
        mSearchView.getText().clear();
    }

    private SearchManager getSearchManager(RecentsManager recentsManager) {
        SearchManager.Builder builder = new SearchManager.Builder()
                .setRecentsManager(recentsManager)
                .setProviders(getProviders());
        String opennames = VariableUtils.getStringFromManifest(this, "opennames");
        String places = VariableUtils.getStringFromManifest(this, "places");
        if (!opennames.isEmpty()) {
            builder = builder.addOpenNames(opennames);
        }
        if (!places.isEmpty()) {
            builder = builder.addPlaces(places);
        }
        return builder.build();
    }

    private boolean isVoiceEnabled() {
        // Check to see if a recognition activity is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        return activities.size() > 0;
    }

    private void updateSearchViewState() {
        boolean hasFocus = mSearchView.hasFocus();
        boolean hasText = mSearchView.getText().length() > 0;

        boolean clearEnabled = false;
        boolean micEnabled = false;
        boolean micHollowEnabled = false;

        if (hasFocus && hasText) {
            clearEnabled = true;
        } else if (hasFocus && !hasText) {
            micEnabled = true;
        } else if (!hasFocus && hasText) {
            micHollowEnabled = true;
        } else if (!hasFocus && !hasText) {
            micHollowEnabled = true;
        }

        mActiveLine.setVisibility(hasText ? View.VISIBLE : View.GONE);
        mEmptyLine.setVisibility(hasText ? View.GONE : View.VISIBLE);
        mClear.setVisibility(clearEnabled ? View.VISIBLE : View.GONE);
        if (mSupportsVoiceRecognition) {
            mMic.setVisibility(micEnabled ? View.VISIBLE : View.GONE);
            mMicHollow.setVisibility(micHollowEnabled ? View.VISIBLE : View.GONE);
        } else {
            mMic.setVisibility(View.GONE);
            mMicHollow.setVisibility(View.GONE);
        }
    }

    private class QueryConcern {

        private static final int MSG_QUERY = 0;
        private static final int MSG_START = 1;

        private static final long DELAY_IN_MILLISECONDS = 1000L;
        private final SearchPresenter mSearchPresenter;
        private final RecentsOnlyPresenter mRecentsOnlyPresenter;
        private State mState = State.INIT;

        private Handler mHandler = new SafeHandler(SearchActivity.this, new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_QUERY:
                        query();
                        break;
                    case MSG_START: {
                        setSearchText(mSearchView.getText().toString());
                        break;
                    }
                    default:
                        throw new IllegalArgumentException("unsupported message " + msg);
                }
                return true;
            }
        });

        private Subscription mSubscription;
        private Subscription mRecentsSubscription;

        public QueryConcern() {
            mSearchPresenter = new SearchPresenter();
            mRecentsOnlyPresenter = new RecentsOnlyPresenter();
        }

        public void init() {
            mSearchPresenter.init();
            mRecentsOnlyPresenter.init();
            mHandler.sendEmptyMessage(MSG_START);
        }

        public void setSearchText(String query) {
            State current = query.length() == 0 ? State.RECENTS_ONLY : State.SEARCH;
            boolean changePresenter = mState != current;
            mState = current;

            switch (mState) {
                case SEARCH: {
                    if (changePresenter) {
                        mRecentsOnlyPresenter.stop();
                        mSearchPresenter.start();
                    }
                    querySoon();
                    break;
                }
                case RECENTS_ONLY: {
                    if (changePresenter) {
                        mSearchPresenter.stop();
                        mRecentsOnlyPresenter.start();
                    }
                    queryTopRecents();
                    break;
                }
                case INIT: {
                    mRecentsOnlyPresenter.start();
                    mState = State.RECENTS_ONLY;
                    break;
                }
                default:
                    throw new IllegalArgumentException("unsupported state");
            }
        }

        private void queryTopRecents() {
            mRecentsSubscription = mRecentsManager
                    .last(10)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<SearchResult>>() {
                        @Override
                        public void call(List<SearchResult> recents) {
                            mRecentsOnlyPresenter.present(recents);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            // ignore
                            mRecentsOnlyPresenter.present(throwable);
                            mRecentsSubscription.unsubscribe();
                            mRecentsSubscription = null;
                        }
                    });
        }

        public void query() {
            mHandler.removeMessages(MSG_QUERY);
            query(mSearchView.getText().toString());
        }

        public void querySoon() {
            mHandler.removeMessages(MSG_QUERY);
            mHandler.sendEmptyMessageDelayed(MSG_QUERY, DELAY_IN_MILLISECONDS);
        }

        private void query(String value) {
            RxUtil.unsubscribe(mSubscription);
            if (value.isEmpty()) {
                return;
            }
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
        }
    }

    private class RecentsOnlyPresenter {

        public void init() {
            hideViews();
        }

        public void present(List<SearchResult> recents) {
            clearErrors();
            mRecentsAdapter.setData(recents);

            boolean oldAdapter = mRecyclerView.getAdapter() != mRecentsAdapter;
            if (oldAdapter) {
                mRecyclerView.setAdapter(mRecentsAdapter);
            }

            mRecents.setVisibility(recents.size() == 0 ? View.GONE : View.VISIBLE);
        }

        public void present(Throwable throwable) {
            Timber.e(throwable, "recent search results are broken");
            mRecents.setVisibility(View.GONE);
            mErrorRecents.setVisibility(View.VISIBLE);
        }

        public void stop() {
            hideViews();
            mRecentsAdapter.removeData();
        }

        public void start() {
            boolean oldAdapter = mRecyclerView.getAdapter() != mRecentsAdapter;
            if (oldAdapter) {
                mRecyclerView.setAdapter(mRecentsAdapter);
            }
            hideViews();
        }

        private void clearErrors() {
            mErrorRecents.setVisibility(View.GONE);
        }

        private void hideViews() {
            mRecents.setVisibility(View.GONE);
            mErrorRecents.setVisibility(View.GONE);
        }
    }

    private class SearchPresenter {

        private boolean mError = false;

        public void init() {
            stop();
        }

        public void start() {
            boolean oldAdapter = mRecyclerView.getAdapter() != mSearchAdapter;
            if (oldAdapter) {
                mRecyclerView.setAdapter(mSearchAdapter);
            }
            clearErrors();
        }

        public void stop() {
            clearErrors();
            mNoSearchResults.setVisibility(View.GONE);
            mSearchAdapter.removeData();
        }

        public void present(SearchBundle searchBundle) {
            clearErrors();
            for (Throwable throwable : searchBundle.getErrors()) {
                presentError(throwable);
            }
            presentResults(searchBundle);
            refreshNoSearchResults();
        }

        public void presentResults(SearchBundle searchBundle) {
            String highlight = mSearchView.getText().toString();

            List<SearchResult> recentResults = searchBundle.getRecents();
            List<SearchResult> remainingResults = searchBundle.getRemaining();

            //checkUkGrids(remainingResults);
            mSearchAdapter.setData(highlight, remainingResults, recentResults);
            mSearchAdapter.notifyDataSetChanged();
        }

        public void presentError(Throwable throwable) {
            mError = true;
            Timber.e(throwable, "error performing search");

            if (throwable instanceof UnknownHostException) {
                Timber.e("network error");
                mErrorNetwork.setVisibility(View.VISIBLE);
            } else if (throwable instanceof IllegalArgumentException) {
                Timber.e(throwable, "data conversion error");
                mErrorConversion.setVisibility(View.VISIBLE);
            } else if (throwable instanceof HttpException) {
                Timber.e(throwable, "HTTP error");
                mErrorHttp.setVisibility(View.VISIBLE);
                Toast.makeText(SearchActivity.this, ((HttpException)throwable).message(),
                        Toast.LENGTH_SHORT).show();
            } else {
                Timber.e(throwable, "random error");
                mErrorCatchAll.setVisibility(View.VISIBLE);
            }
        }

        private void refreshNoSearchResults() {
            boolean hasQuery = mSearchView.getText().length() > 0;
            boolean noResults = mSearchAdapter.getItemCount() == 0;
            if (noResults && hasQuery && !mError) {
                mNoSearchResults.setVisibility(View.VISIBLE);
            } else {
                mNoSearchResults.setVisibility(View.GONE);
            }
        }

        private void clearErrors() {
            mErrorNetwork.setVisibility(View.GONE);
            mErrorHttp.setVisibility(View.GONE);
            mErrorConversion.setVisibility(View.GONE);
            mErrorCatchAll.setVisibility(View.GONE);
        }
    }

    private static class VariableUtils {

        static String getStringFromManifest(Activity activity, String s) {
            String result = "";
            try {
                ActivityInfo activityInfo = activity.getPackageManager()
                        .getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
                return activityInfo.metaData.getString(s, result);
            } catch (NullPointerException | PackageManager.NameNotFoundException e) {
                Timber.e(e, "error getting metadata");
            }
            return result;
        }
    }

    /**
     * Issues documented:
     * 1) https://groups.google.com/forum/#!msg/android-developers/1aPZXZG6kWk/lIYDavGYn5UJ
     * 2) http://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler
     */
    private static class SafeHandler extends Handler {
        private final WeakReference<Activity> mActivity;
        private final Callback mCallback;

        public SafeHandler(Activity activity, Handler.Callback callback) {
            mActivity = new WeakReference<>(activity);
            mCallback = callback;
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivity.get();
            if (activity != null) {
                mCallback.handleMessage(msg);
            }
        }
    }
}

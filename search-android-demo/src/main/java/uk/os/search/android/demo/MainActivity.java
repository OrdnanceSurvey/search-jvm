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

package uk.os.search.android.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import uk.os.elements.search.SearchResult;
import uk.os.elements.search.android.app.SearchActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SEARCH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SEARCH) {
            if (resultCode == Activity.RESULT_OK) {
                SearchResult searchResult = (SearchResult) data.getSerializableExtra("result");
                Toast.makeText(this, createMessage(searchResult), Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onSearchRequested(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SEARCH);
    }

    private String createMessage(SearchResult searchResult) {
        return String.format("%s - great choice!", searchResult.getName());
    }
}

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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import uk.os.search.SearchResult;
import uk.os.search.android.R;

public class RecentsOnlyAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final List<SearchResult> mDataset = new ArrayList<>();
    private final SearchAdapter.SearchListener mListener;

    public RecentsOnlyAdapter(@NonNull SearchAdapter.SearchListener listener) {
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder holder, int position) {
        final SearchResult searchResult = mDataset.get(position);
        holder.mLine1.setText(searchResult.getName());
        holder.mLine2.setText(searchResult.getContext());
        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSelected(searchResult);
            }
        });
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item,
                parent, false);
        v.findViewById(R.id.search_item_recent).setVisibility(View.GONE);
        return new SearchAdapter.ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void removeData() {
        mDataset.clear();
        notifyDataSetChanged();
    }

    public void setData(List<SearchResult> recents) {
        mDataset.clear();
        mDataset.addAll(recents);
        notifyDataSetChanged();
    }
}

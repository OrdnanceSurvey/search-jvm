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

package uk.os.elements.search.android.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.os.elements.search.SearchResult;
import uk.os.elements.search.android.R;
import uk.os.elements.search.android.providers.bng.OsGridReference;
import uk.os.elements.search.android.providers.latlon.LatLonResult;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private static final int TYPE_RECENT = 0;
    private static final int TYPE_ITEM = 1;

    private final Context mContext;
    private final List<SearchResult> mDataset = new ArrayList<>();
    private int mLastRecent = -1;

    private String mQuery = "";
    private SearchListener mListener;

    public interface SearchListener {
        void onSelected(SearchResult searchResult);
    }

    public SearchAdapter(@NonNull Context context, SearchListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void removeData() {
        mDataset.clear();
        notifyDataSetChanged();
    }

    public void setData(String query, List<SearchResult> remainingResults,
                        List<SearchResult> recentResults) {
        mLastRecent = recentResults.size() - 1;

        mQuery = query;
        mDataset.clear();
        mDataset.addAll(recentResults);
        mDataset.addAll(remainingResults);
        notifyDataSetChanged();
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item,
                parent, false);
        switch (viewType) {
            case TYPE_RECENT:
                v.findViewById(R.id.search_item_recent).setVisibility(View.VISIBLE);
                break;
            case TYPE_ITEM:
                v.findViewById(R.id.search_item_recent).setVisibility(View.GONE);
                break;
            default:
                throw new IllegalArgumentException("unknown view type");
        }

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        boolean isPositionRecent = position <= mLastRecent;
        if (isPositionRecent) {
            return TYPE_RECENT;
        }

        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SearchResult searchResult = mDataset.get(position);
        holder.mLine1.setText(searchResult.getName());
        holder.mLine2.setText(searchResult.getContext());
        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSelected(searchResult);
                }
            }
        });
        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSelected(searchResult);
                }
            }
        });

        String text = searchResult.getName();
        String highlight = mQuery;

        if (searchResult instanceof OsGridReference || searchResult instanceof LatLonResult) {
            highlight = searchResult.getName();
        }

        int color = ContextCompat.getColor(mContext, R.color.search_text_highlight);
        TextView textView = holder.mLine1;
        setColor(textView, text, highlight, color);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void setColor(TextView view, String fulltext, String subtext, int color) {
        if (subtext.isEmpty()) {
            return;
        }

        view.setText(fulltext, TextView.BufferType.SPANNABLE);
        Spannable str = (Spannable) view.getText();

        List<Integer> indexes = indexesOf(fulltext.toLowerCase(Locale.getDefault()),
                subtext.toLowerCase(Locale.getDefault()));
        for (Integer index : indexes) {
            str.setSpan(new ForegroundColorSpan(color), index, index + subtext.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private static List<Integer> indexesOf(String text, String subtext) {
        List<Integer> results = new ArrayList<>();
        int index = text.indexOf(subtext, 0);
        while (index != -1) {
            int currentIndex = text.indexOf(subtext, index);
            results.add(currentIndex);
            int nextIndex = text.indexOf(subtext, currentIndex + subtext.length());
            index = nextIndex;
        }
        return results;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View mContainer = null;
        ImageView mImageView = null;
        TextView mLine1 = null;
        TextView mLine2 = null;

        public ViewHolder(View itemView) {
            super(itemView);
            mContainer = itemView.findViewById(R.id.search_item);
            mImageView = (ImageView) itemView.findViewById(R.id.icon);
            mLine1 = (TextView) itemView.findViewById(R.id.firstLine);
            mLine2 = (TextView) itemView.findViewById(R.id.secondLine);
        }
    }
}

package com.mct.auto_clicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.database.domain.Configure;

import java.util.List;

public class ConfigureListSimpleAdapter extends RecyclerView.Adapter<ConfigureListSimpleAdapter.ConfigureViewHolder> {

    private final Context mContext;
    private final List<Configure> mListConfigures;
    private final OnConfigureChooseListener mOnConfigureChooseListener;

    public interface OnConfigureChooseListener {
        void onConfigureChoose(Configure configure);
    }

    public ConfigureListSimpleAdapter(Context mContext, List<Configure> mListConfigures, OnConfigureChooseListener mOnConfigureChooseListener) {
        this.mContext = mContext;
        this.mListConfigures = mListConfigures;
        this.mOnConfigureChooseListener = mOnConfigureChooseListener;
    }

    @NonNull
    @Override
    public ConfigureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_configure_simple, parent, false);
        return new ConfigureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfigureViewHolder holder, int position) {
        Configure configure = mListConfigures.get(position);
        if (configure == null) {
            return;
        }
        holder.tvName.setText(configure.getName());
        int actionSize = configure.getActions().size();
        String detail = mContext.getResources().getQuantityString(R.plurals.item_configure_detail, actionSize, actionSize);
        holder.tvDetails.setText(detail);
        holder.itemView.setOnClickListener(view -> {
            if (mOnConfigureChooseListener != null) {
                mOnConfigureChooseListener.onConfigureChoose(configure);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListConfigures != null) {
            return mListConfigures.size();
        }
        return 0;
    }

    static class ConfigureViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvDetails;

        public ConfigureViewHolder(@NonNull View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_name);
            tvDetails = view.findViewById(R.id.tv_details);
        }
    }
}

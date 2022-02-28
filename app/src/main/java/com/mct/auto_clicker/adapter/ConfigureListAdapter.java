package com.mct.auto_clicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.mct.auto_clicker.R;
import com.mct.auto_clicker.database.domain.Configure;

import java.util.ArrayList;
import java.util.List;

public class ConfigureListAdapter extends RecyclerView.Adapter<ConfigureListAdapter.ConfigureListViewHolder> {

    private final Context mContext;
    private List<Configure> mListConfigure;
    private final ItemConfigureListener mItemConfigureListener;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private boolean isChoosing;

    public ConfigureListAdapter(Context mContext, ItemConfigureListener mItemConfigureListener) {
        this.mContext = mContext;
        this.mItemConfigureListener = mItemConfigureListener;
    }

    public void addConfigure(Configure configure) {
        if (mListConfigure == null) {
            mListConfigure = new ArrayList<>();
        }
        mListConfigure.add(0, configure);
        notifyItemInserted(0);
    }

    public void deleteConfigure(int position) {
        if (mListConfigure != null) {
            mListConfigure.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setListConfigure(List<Configure> mListConfigure) {
        this.mListConfigure = mListConfigure;
        notifyDataSetChanged();
    }

    public void setChoosing(boolean choosing) {
        isChoosing = choosing;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ConfigureListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_configure, parent, false);
        return new ConfigureListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfigureListViewHolder holder, int position) {
        Configure configure = mListConfigure.get(position);
        if (configure == null) {
            return;
        }
        binderHelper.bind(holder.swrLayout, String.valueOf(configure.getId()));
        holder.tvName.setText(configure.getName());
        int actionSize = configure.getActions().size();
        String detail = mContext.getResources().getQuantityString(R.plurals.item_configure_detail, actionSize, actionSize);
        holder.tvDetails.setText(detail);

        if (isChoosing) {
            holder.layoutCheckBox.setVisibility(View.VISIBLE);
            holder.layoutButtons.setVisibility(View.GONE);
            holder.cbChoose.setChecked(configure.isChoose());
            binderHelper.lockSwipe(String.valueOf(configure.getId()));
            binderHelper.closeLayout(String.valueOf(configure.getId()));
        } else {
            holder.layoutCheckBox.setVisibility(View.GONE);
            holder.layoutButtons.setVisibility(View.VISIBLE);
            binderHelper.unlockSwipe(String.valueOf(configure.getId()));
        }

        holder.item.setOnClickListener(view -> {
            if (isChoosing) {
                configure.setChoose(!configure.isChoose());
                holder.cbChoose.setChecked(configure.isChoose());
                mItemConfigureListener.onClick(configure, holder.getAdapterPosition());
            }
        });
        holder.item.setOnLongClickListener(view -> {
            if (!isChoosing) {
                mItemConfigureListener.onItemLongClick(configure, holder.getAdapterPosition());
            }
            return isChoosing;
        });
        holder.btnPlay.setOnClickListener(view -> {
            mItemConfigureListener.onStart(configure, holder.getAdapterPosition());
        });
        holder.btnRename.setOnClickListener(view -> {
            mItemConfigureListener.onRename(configure, holder.getAdapterPosition());
        });
        holder.btnCopy.setOnClickListener(view -> {
            mItemConfigureListener.onCopy(configure, holder.getAdapterPosition());
        });
        holder.btnDelete.setOnClickListener(view -> {
            mItemConfigureListener.onDelete(configure, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        if (mListConfigure != null) {
            return mListConfigure.size();
        }
        return 0;
    }


    static class ConfigureListViewHolder extends RecyclerView.ViewHolder {

        SwipeRevealLayout swrLayout;
        View item, layoutCheckBox, layoutButtons;
        TextView tvName, tvDetails;
        CheckBox cbChoose;
        ImageButton btnPlay, btnRename, btnCopy, btnDelete;

        public ConfigureListViewHolder(@NonNull View view) {
            super(view);
            swrLayout = view.findViewById(R.id.swr_layout);
            item = view.findViewById(R.id.item);
            layoutCheckBox = view.findViewById(R.id.layout_check_box);
            layoutButtons = view.findViewById(R.id.layout_buttons);
            tvName = view.findViewById(R.id.tv_name);
            tvDetails = view.findViewById(R.id.tv_details);
            cbChoose = view.findViewById(R.id.cb_choose);
            btnPlay = view.findViewById(R.id.btn_play);
            btnRename = view.findViewById(R.id.btn_rename);
            btnCopy = view.findViewById(R.id.btn_copy);
            btnDelete = view.findViewById(R.id.btn_delete);
        }
    }

    public interface ItemConfigureListener {
        void onStart(Configure configure, int position);

        void onRename(Configure configure, int position);

        void onCopy(Configure configure, int position);

        void onDelete(Configure configure, int position);

        void onClick(Configure configure, int position);

        void onItemLongClick(Configure configure, int position);

    }
}

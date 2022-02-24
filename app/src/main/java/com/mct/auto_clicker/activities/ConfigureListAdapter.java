package com.mct.auto_clicker.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private StartConfigureListener startConfigureListener;
    private RenameConfigureListener renameConfigureListener;
    private DeleteConfigureListener deleteConfigureListener;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public ConfigureListAdapter(Context mContext) {
        this.mContext = mContext;
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

    public void setStartConfigureListener(StartConfigureListener startConfigureListener) {
        this.startConfigureListener = startConfigureListener;
    }

    public void setRenameConfigureListener(RenameConfigureListener renameConfigureListener) {
        this.renameConfigureListener = renameConfigureListener;
    }

    public void setDeleteConfigureListener(DeleteConfigureListener deleteConfigureListener) {
        this.deleteConfigureListener = deleteConfigureListener;
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
        int actionSize = 0;
        if (configure.getActions() != null) {
            actionSize = configure.getActions().size();
        }
        String detail = mContext.getResources().getQuantityString(R.plurals.item_configure_detail, actionSize, actionSize);
        holder.tvDetails.setText(detail);
        holder.item.setOnClickListener(view -> {
            if (startConfigureListener != null)
                startConfigureListener.onStart(configure, holder.getAdapterPosition());
        });
        holder.btnRename.setOnClickListener(view -> {
            if (renameConfigureListener != null)
                renameConfigureListener.onRename(configure, holder.getAdapterPosition());
        });
        holder.btnDelete.setOnClickListener(view -> {
            if (deleteConfigureListener != null)
                deleteConfigureListener.onDelete(configure, holder.getAdapterPosition());
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
        View item;
        TextView tvName, tvDetails;
        ImageButton btnRename, btnDelete;

        public ConfigureListViewHolder(@NonNull View view) {
            super(view);
            swrLayout = view.findViewById(R.id.swr_layout);
            item = view.findViewById(R.id.item);
            tvName = view.findViewById(R.id.tv_name);
            tvDetails = view.findViewById(R.id.tv_details);
            btnRename = view.findViewById(R.id.btn_rename);
            btnDelete = view.findViewById(R.id.btn_delete);
        }
    }

    public interface StartConfigureListener {
        void onStart(Configure configure, int position);
    }

    public interface RenameConfigureListener {
        void onRename(Configure configure, int position);
    }

    public interface DeleteConfigureListener {
        void onDelete(Configure configure, int position);
    }
}

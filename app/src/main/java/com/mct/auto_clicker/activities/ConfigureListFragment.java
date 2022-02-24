package com.mct.auto_clicker.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.overlays.dialog.DialogHelper;
import com.mct.auto_clicker.presenter.ConfigurePermissionPresenter;

public class ConfigureListFragment extends Fragment {

    private RecyclerView rcvConfigureList;
    private ConfigureListAdapter configureListAdapter;
    private ConfigurePermissionPresenter configurePresenter;

    private AlertDialog dialog = null;

    public interface OnConfigureClickedListener {
        void onClicked(Configure configure);
    }

    public static ConfigureListFragment newInstance() {
        return new ConfigureListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_configure_list, container, false);
        rcvConfigureList = view.findViewById(R.id.rcv_configure_list);
        initToolBar(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        configurePresenter = new ConfigurePermissionPresenter(requireContext());
        configureListAdapter = new ConfigureListAdapter(requireContext());
        configureListAdapter.setListConfigure(configurePresenter.getAllConfigure());
        configureListAdapter.setStartConfigureListener((configure, position) -> ((OnConfigureClickedListener) requireActivity()).onClicked(configure));
        configureListAdapter.setRenameConfigureListener(this::onRenameClicked);
        configureListAdapter.setDeleteConfigureListener(this::onDeleteClicked);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        rcvConfigureList.setAdapter(configureListAdapter);
        rcvConfigureList.setLayoutManager(layoutManager);
    }

    private void initToolBar(@NonNull View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showDialog(AlertDialog newDialog) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = newDialog;
        newDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        newDialog.setOnDismissListener(dialogInterface -> dialog = null);
        newDialog.show();
    }

    private void onCreateClicked() {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit, null);
        EditText edtName = view.findViewById(R.id.tv_name);
        showDialog(new AlertDialog.Builder(requireContext())
                .setCustomTitle(DialogHelper.getTitleView(requireContext(), R.layout.view_dialog_title, R.string.dialog_title_add_configure, R.drawable.ic_add, R.color.textTitle))
                .setView(view)
                .setPositiveButton(R.string.save, (dialogInterface, i) -> {
                    if (!edtName.getText().toString().isEmpty()) {
                        long id = configurePresenter.createConfigure(edtName.getText().toString());
                        if (id != -1) {
                            Configure configure = configurePresenter.getConfigure(id);
                            configureListAdapter.addConfigure(configure);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create());
    }

    private void onRenameClicked(@NonNull Configure configure, int position) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit, null);
        EditText edtName = view.findViewById(R.id.tv_name);
        edtName.setText(configure.getName());
        edtName.selectAll();
        showDialog(new AlertDialog.Builder(requireContext())
                .setCustomTitle(DialogHelper.getTitleView(requireContext(), R.layout.view_dialog_title, R.string.dialog_title_rename_configure, R.drawable.ic_rename, R.color.textTitle))
                .setView(view)
                .setPositiveButton(R.string.save, (dialogInterface, i) -> {
                    if (!edtName.getText().toString().isEmpty()) {
                        configurePresenter.renameConfigure(configure, edtName.getText().toString());
                        configureListAdapter.notifyItemChanged(position);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create());
    }

    private void onDeleteClicked(@NonNull Configure configure, int position) {
        showDialog(new AlertDialog.Builder(requireContext())
                .setCustomTitle(DialogHelper.getTitleView(requireContext(), R.layout.view_dialog_title, R.string.dialog_title_delete_configure, R.drawable.ic_trash, R.color.textTitle))
                .setMessage(requireContext().getResources().getString(R.string.dialog_desc_delete_configure_message, configure.getName()))
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    configurePresenter.deleteConfigure(configure);
                    configureListAdapter.deleteConfigure(position);
                })
                .setNegativeButton(R.string.cancel, null)
                .create());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.e("ddd", "onOptionsItemSelected: " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.menu_add_configure:
                onCreateClicked();
                return true;
            case android.R.id.home:
                ((AutoClickerActivity) requireActivity()).removeFragmentFromMainFrame();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_configure_list_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}

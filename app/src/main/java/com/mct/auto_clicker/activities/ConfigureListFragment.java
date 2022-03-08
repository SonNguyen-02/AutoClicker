package com.mct.auto_clicker.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.adapter.ConfigureListAdapter;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.overlays.dialog.DialogHelper;
import com.mct.auto_clicker.presenter.ConfigurePermissionPresenter;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigureListFragment extends Fragment implements ConfigureListAdapter.ItemConfigureListener {

    private List<Configure> mListConfigure;
    private RecyclerView rcvConfigureList;
    private ConfigureListAdapter configureListAdapter;
    private ConfigurePermissionPresenter configurePresenter;

    private AlertDialog dialog = null;
    private Toolbar toolbarChooseItemDelete;

    public interface OnConfigureClickedListener {
        void onClicked(Configure configure);
    }

    @NonNull
    public static ConfigureListFragment newInstance() {
        return new ConfigureListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configure_list, container, false);
        rcvConfigureList = view.findViewById(R.id.rcv_configure_list);

        toolbarChooseItemDelete = view.findViewById(R.id.toolbar_choose_item_delete);
        initToolBar(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        configurePresenter = new ConfigurePermissionPresenter(requireContext());
        configureListAdapter = new ConfigureListAdapter(requireContext(), this);
        mListConfigure = configurePresenter.getAllConfigure();
        configureListAdapter.setListConfigure(mListConfigure);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        rcvConfigureList.setAdapter(configureListAdapter);
        rcvConfigureList.setLayoutManager(layoutManager);
    }

    private void initToolBar(@NonNull View view) {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(view.findViewById(R.id.toolbar));
        toolbarChooseItemDelete.setNavigationOnClickListener(v -> hideToolBarChooseItemDelete());
        toolbarChooseItemDelete.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_delete) {
                onDeleteConfiguresClicked();
                return true;
            }
            return false;
        });
    }

    private void showDialog(AlertDialog newDialog, boolean isShowKeyboard) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = newDialog;
//        newDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        if (isShowKeyboard) {
            showKeyboard(requireContext());
        }
        newDialog.setOnDismissListener(dialogInterface -> {
            if (isShowKeyboard) {
                closeKeyboard(requireContext());
            }
            dialog = null;
        });
        newDialog.show();
    }

    private void onCreateClicked() {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit, null);
        EditText edtName = view.findViewById(R.id.tv_name);
        edtName.requestFocus();
        showDialog(new AlertDialog.Builder(requireContext())
                .setCustomTitle(DialogHelper.getTitleView(requireContext(), R.layout.view_dialog_title, R.string.dialog_title_add_configure, R.drawable.ic_add, R.color.textTitle))
                .setView(view)
                .setPositiveButton(R.string.save, (dialogInterface, i) -> {
                    if (!TextUtils.isEmpty(edtName.getText())) {
                        String name = edtName.getText().toString().trim();
                        long id = configurePresenter.createConfigure(name);
                        if (id != -1) {
                            Configure configure = configurePresenter.getConfigure(id);
                            configureListAdapter.addConfigure(configure);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create(), true);
    }

    private void onDeleteConfiguresClicked() {
        List<Configure> listConfigureDelete = mListConfigure.stream().filter(Configure::isChoose).collect(Collectors.toList());
        showDialog(new AlertDialog.Builder(requireContext())
                .setCustomTitle(DialogHelper.getTitleView(requireContext(), R.layout.view_dialog_title, R.string.dialog_title_delete_configure, R.drawable.ic_trash, R.color.textTitle))
                .setMessage(requireContext().getResources().getString(R.string.dialog_desc_delete_configures_message, listConfigureDelete.size()))
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    mListConfigure.removeAll(listConfigureDelete);
                    configurePresenter.deleteConfigures(listConfigureDelete);
                    hideToolBarChooseItemDelete();
                })
                .setNegativeButton(R.string.cancel, null)
                .create(), false);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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

    @Override
    public void onStart(@NonNull Configure configure, int position) {
        ((OnConfigureClickedListener) requireActivity()).onClicked(configurePresenter.getConfigure(configure.getId()));
    }

    @Override
    public void onRename(@NonNull Configure configure, int position) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit, null);
        EditText edtName = view.findViewById(R.id.tv_name);
        edtName.setText(configure.getName());
        edtName.requestFocus();
        edtName.selectAll();
        showDialog(new AlertDialog.Builder(requireContext())
                .setCustomTitle(DialogHelper.getTitleView(requireContext(), R.layout.view_dialog_title, R.string.dialog_title_rename_configure, R.drawable.ic_rename, R.color.textTitle))
                .setView(view)
                .setPositiveButton(R.string.save, (dialogInterface, i) -> {
                    if (!TextUtils.isEmpty(edtName.getText())) {
                        String name = edtName.getText().toString().trim();
                        configurePresenter.renameConfigure(configure, name);
                        configureListAdapter.notifyItemChanged(position);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create(), true);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCopy(@NonNull Configure configure, int position) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit, null);
        EditText edtName = view.findViewById(R.id.tv_name);
        edtName.setText(configurePresenter.getSuffixConfig(configure.getName(), true));
        edtName.requestFocus();
        edtName.selectAll();
        showDialog(new AlertDialog.Builder(requireContext())
                .setCustomTitle(DialogHelper.getTitleView(requireContext(), R.layout.view_dialog_title, R.string.dialog_title_copy_configure, R.drawable.ic_copy, R.color.textTitle))
                .setView(view)
                .setPositiveButton(R.string.save, (dialogInterface, i) -> {
                    Configure copyConfigure = configure.deepCopy();
                    copyConfigure.cleanUpIds();
                    if (!TextUtils.isEmpty(edtName.getText())) {
                        String name = edtName.getText().toString().trim();
                        copyConfigure.setName(name);
                    }
                    long id = configurePresenter.createConfigure(copyConfigure);
                    if (id != -1) {
                        copyConfigure = configurePresenter.getConfigure(id);
                        configureListAdapter.addConfigure(copyConfigure);
                    } else {
                        Toast.makeText(requireContext(), "Something wrong. Please try again!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create(), true);

    }

    @Override
    public void onDelete(@NonNull Configure configure, int position) {
        showDialog(new AlertDialog.Builder(requireContext())
                .setCustomTitle(DialogHelper.getTitleView(requireContext(), R.layout.view_dialog_title, R.string.dialog_title_delete_configure, R.drawable.ic_trash, R.color.textTitle))
                .setMessage(requireContext().getResources().getString(R.string.dialog_desc_delete_configure_message, configure.getName()))
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    configurePresenter.deleteConfigure(configure);
                    configureListAdapter.deleteConfigure(position);
                })
                .setNegativeButton(R.string.cancel, null)
                .create(), false);
    }

    @Override
    public void onClick(@NonNull Configure configure, int position) {
        int total = (int) mListConfigure.stream().filter(Configure::isChoose).count();
        if (total == 0) {
            hideToolBarChooseItemDelete();
        } else {
            setToolbarChooseItemDeleteTitle(total);
        }
    }

    @Override
    public void onItemLongClick(@NonNull Configure configure, int position) {
        toolbarChooseItemDelete.setVisibility(View.VISIBLE);
        setToolbarChooseItemDeleteTitle(1);
        mListConfigure.forEach(it -> it.setChoose(false));
        configure.setChoose(true);
        configureListAdapter.setChoosing(true);
    }

    private void setToolbarChooseItemDeleteTitle(int n) {
        toolbarChooseItemDelete.setTitle(MessageFormat.format(requireContext()
                .getString(R.string.fragment_choose_item_title), String.valueOf(n)));
    }

    private void hideToolBarChooseItemDelete() {
        toolbarChooseItemDelete.setVisibility(View.GONE);
        configureListAdapter.setChoosing(false);
    }

    private void showKeyboard(@NonNull Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void closeKeyboard(@NonNull Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

}

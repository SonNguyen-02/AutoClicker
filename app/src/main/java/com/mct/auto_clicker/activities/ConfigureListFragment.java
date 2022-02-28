package com.mct.auto_clicker.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.mct.auto_clicker.adapter.ConfigureListAdapter;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.overlays.dialog.DialogHelper;
import com.mct.auto_clicker.presenter.ConfigurePermissionPresenter;

import java.text.MessageFormat;
import java.util.ArrayList;
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
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_configure_list, container, false);
        rcvConfigureList = view.findViewById(R.id.rcv_configure_list);
        toolbarChooseItemDelete = view.findViewById(R.id.toolbar_choose_item_delete);
        initToolBar(view);
        initToolBarChooseItemDelete();
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
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initToolBarChooseItemDelete() {
        getToolbarLogoIcon(toolbarChooseItemDelete).setOnClickListener(view -> hideToolBarChooseItemDelete());
        toolbarChooseItemDelete.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_delete) {
                onDeleteConfiguresClicked();
                return true;
            }
            return false;
        });
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

    @Override
    public void onStart(@NonNull Configure configure, int position) {
        ((OnConfigureClickedListener) requireActivity()).onClicked(configure.deepCopy());
    }

    @Override
    public void onRename(@NonNull Configure configure, int position) {
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

    @Override
    public void onCopy(@NonNull Configure configure, int position) {
        Configure copyConfigure = configure.deepCopy();
        copyConfigure.cleanUpIds();
        long id = configurePresenter.createConfigure(copyConfigure);
        if (id != -1) {
            copyConfigure = configurePresenter.getConfigure(id);
            configureListAdapter.addConfigure(copyConfigure);
        } else {
            Toast.makeText(requireContext(), "Something wrong. Please try again!", Toast.LENGTH_SHORT).show();
        }
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
                .create());
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

    private static View getToolbarLogoIcon(@NonNull Toolbar toolbar) {
        //check if contentDescription previously was set
        boolean hadContentDescription = TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        View logoIcon = null;
        if (potentialViews.size() > 0) {
            logoIcon = potentialViews.get(0);
        }
        //Clear content description if not previously present
        if (hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }
}

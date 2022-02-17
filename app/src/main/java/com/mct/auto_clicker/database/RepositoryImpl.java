package com.mct.auto_clicker.database;

import androidx.annotation.NonNull;

import com.mct.auto_clicker.database.domain.Action;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.database.room.AutoClickDatabase;
import com.mct.auto_clicker.database.room.dao.ActionDAO;
import com.mct.auto_clicker.database.room.dao.ConfigureDAO;
import com.mct.auto_clicker.database.room.dao.EntityListUpdater;
import com.mct.auto_clicker.database.room.entity.ActionEntity;
import com.mct.auto_clicker.database.room.entity.ConfigureEntity;

import java.util.List;
import java.util.stream.Collectors;

public class RepositoryImpl extends Repository {

    private final ActionDAO actionDAO;

    private final ConfigureDAO configureDAO;

    private final EntityListUpdater<ActionEntity, Long> actionsUpdater = new EntityListUpdater<>(0L, action -> action.id);

    public RepositoryImpl(@NonNull AutoClickDatabase database) {
        actionDAO = database.actionDAO();
        configureDAO = database.configureDAO();
    }

    @Override
    public Long addConfigure(@NonNull Configure configure) {
        Long id = configureDAO.add(configure.toEntity());
        if (id == -1) return id;
        List<ActionEntity> mList = configure.toConfigureAndAction().actions;
        mList.forEach(it -> it.configureId = id);
        actionDAO.addActions(mList);
        return id;
    }

    @Override
    public void updateConfigure(@NonNull Configure configure) {
        configureDAO.update(configure.toEntity());

        List<ActionEntity> oldActions = actionDAO.getActions(configure.getId());
        actionsUpdater.refreshUpdateValues(oldActions, configure.toConfigureAndAction().actions);
        actionsUpdater.getToBeAdded().forEach(it -> it.configureId = configure.getId());

        actionDAO.addActions(actionsUpdater.getToBeAdded());
        actionDAO.updateActions(actionsUpdater.getToBeUpdated());
        actionDAO.deleteActions(actionsUpdater.getToBeRemoved());
    }

    @Override
    public void deleteConfigure(@NonNull Configure configure) {
        actionDAO.deleteActions(configure.toConfigureAndAction().actions);
        configureDAO.delete(configure.toEntity());
    }

    @Override
    public Configure getConfigure(Long configureId) {
        return configureDAO.getConfigure(configureId).toConfigure();
    }

    @Override
    public List<Configure> getConfigures() {
        return configureDAO.getConfiguresAndAction().stream().map(ConfigureEntity.ConfigureAndAction::toConfigure).collect(Collectors.toList());
    }

    @Override
    public Long addAction(@NonNull Action action) {
        return actionDAO.add(action.toEntity());
    }

    @Override
    public void updateAction(@NonNull Action action) {
        actionDAO.update(action.toEntity());
    }

    @Override
    public void deleteAction(@NonNull Action action) {
        actionDAO.delete(action.toEntity());
    }

    @Override
    public Action getAction(Long actionId) {
        return actionDAO.getAction(actionId).toAction();
    }

    @Override
    public List<Action> getActions(Long configureId) {
        return actionDAO.getActions(configureId).stream().map(ActionEntity::toAction).collect(Collectors.toList());
    }
}

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
    public long getCountConfigures() {
        return configureDAO.getCountConfigure();
    }

    @Override
    public List<Configure> getAllConfigures() {
        return configureDAO.getConfigureAndAction().stream().map(ConfigureEntity.ConfigureAndAction::toConfigure).collect(Collectors.toList());
    }

    @Override
    public Configure getConfigure(Long configureId) {
        ConfigureEntity.ConfigureAndAction c = configureDAO.getConfigureAndAction(configureId);
        if (c != null) {
            return c.toConfigure();
        }
        return null;
    }

    @Override
    public Long addConfigure(@NonNull Configure configure) {
        Long id = configureDAO.add(configure.toEntity());
        if (id == -1) return id;
        if (!configure.getActions().isEmpty()) {
            List<ActionEntity> mList = configure.toConfigureAndAction().actions;
            mList.forEach(it -> it.configureId = id);
            actionDAO.adds(mList);
        }
        return id;
    }

    @Override
    public void updateConfigure(@NonNull Configure configure) {
        configureDAO.update(configure.toEntity());

        List<ActionEntity> oldActions = actionDAO.getActionsByConfigure(configure.getId());
        actionsUpdater.refreshUpdateValues(oldActions, configure.toConfigureAndAction().actions);
        actionsUpdater.getToBeAdded().forEach(it -> it.configureId = configure.getId());

        actionDAO.adds(actionsUpdater.getToBeAdded());
        actionDAO.updates(actionsUpdater.getToBeUpdated());
        actionDAO.deletes(actionsUpdater.getToBeRemoved());
    }

    @Override
    public void deleteConfigure(@NonNull Configure configure) {
        configureDAO.delete(configure.toEntity());
    }

    @Override
    public void deleteConfigures(@NonNull List<Configure> configures) {
        configureDAO.deletes(configures.stream().map(Configure::toEntity).collect(Collectors.toList()));
    }

    @Override
    public List<Action> getAllActions() {
        return actionDAO.getAllActions().stream().map(ActionEntity::toAction).collect(Collectors.toList());
    }

    @Override
    public List<Action> getActionsByConfigure(Long configureId) {
        return actionDAO.getActionsByConfigure(configureId).stream().map(ActionEntity::toAction).collect(Collectors.toList());
    }

    @Override
    public Action getAction(Long actionId) {
        return actionDAO.getAction(actionId).toAction();
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
    public void deleteActions(@NonNull List<Action> actions) {
        actionDAO.deletes(actions.stream().map(Action::toEntity).collect(Collectors.toList()));
    }
}

package com.forcetower.uefs.vm.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.v4.util.Pair;

import com.forcetower.uefs.db.dao.DisciplineClassItemDao;
import com.forcetower.uefs.db.dao.DisciplineDao;
import com.forcetower.uefs.db.dao.DisciplineGroupDao;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.DisciplineClassItem;
import com.forcetower.uefs.db.entity.DisciplineGroup;
import com.forcetower.uefs.rep.sgrs.DisciplinesRepository;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class DisciplinesViewModel extends ViewModel {
    private final DisciplineDao disciplineDao;
    private final DisciplineGroupDao groupDao;
    private final DisciplineClassItemDao itemDao;
    private final DisciplinesRepository repository;

    private LiveData<List<Discipline>> allDisciplines;

    private LiveData<Discipline> disciplineLiveData;
    private LiveData<DisciplineGroup> disciplineGroupLiveData;
    private MediatorLiveData<Pair<Discipline, DisciplineGroup>> mediatorAssociative;

    private MediatorLiveData<Resource<Integer>> fetchDetails;
    private boolean detailsStarted = false;
    private boolean detailsFinished = true;

    private LiveData<List<DisciplineClassItem>> classItemsLiveData;

    @Inject
    DisciplinesViewModel(DisciplineDao disciplineDao, DisciplineGroupDao groupDao,
                         DisciplinesRepository repository, DisciplineClassItemDao itemDao) {
        this.disciplineDao = disciplineDao;
        this.groupDao = groupDao;
        this.mediatorAssociative = new MediatorLiveData<>();
        this.repository = repository;
        this.itemDao = itemDao;
        this.fetchDetails = new MediatorLiveData<>();
    }

    public LiveData<List<Discipline>> getAllDisciplines() {
        if (allDisciplines == null)
            allDisciplines = disciplineDao.getAllDisciplines();

        return allDisciplines;
    }

    public LiveData<Discipline> findDisciplineFromGroupId(int groupId) {
        if (disciplineLiveData == null)
            disciplineLiveData = disciplineDao.getDisciplineFromGroup(groupId);
        return disciplineLiveData;
    }

    public LiveData<DisciplineGroup> findDisciplineGroup(int groupId) {
        if (disciplineGroupLiveData == null)
            disciplineGroupLiveData = groupDao.getDisciplineGroupById(groupId);
        return disciplineGroupLiveData;
    }

    public LiveData<Pair<Discipline, DisciplineGroup>> getAssociate(int groupId) {
        LiveData<Discipline> dcpSrc = findDisciplineFromGroupId(groupId);
        mediatorAssociative.addSource(dcpSrc, discipline -> {
            LiveData<DisciplineGroup> grpSrc = findDisciplineGroup(groupId);
            mediatorAssociative.addSource(grpSrc, group -> {
                mediatorAssociative.postValue(new Pair<>(discipline, group));
            });
        });
        return mediatorAssociative;
    }

    public LiveData<List<DisciplineGroup>> getDisciplineGroups(int disciplineId) {
        return groupDao.getDisciplineGroups(disciplineId);
    }

    public List<DisciplineGroup> getDisciplineGroupsDir(int disciplineId) {
        return groupDao.getDisciplineGroupsDirect(disciplineId);
    }

    public void triggerFetchDetails() {
        detailsStarted = false;
    }

    public LiveData<Resource<Integer>> fetchDetails(int groupId) {
        Timber.d("Fetch details triggered");
        if (!detailsStarted) {
            detailsFinished = false;
            detailsStarted = true;
            LiveData<Resource<Integer>> repSrc = repository.getClassDetails(groupId);
            fetchDetails.addSource(repSrc, resource -> {
                //noinspection ConstantConditions
                if (resource.status == Status.LOADING)
                    fetchDetails.postValue(resource);
                else {
                    Timber.d("%s", resource.status.name());
                    detailsFinished = true;
                    fetchDetails.removeSource(repSrc);
                    fetchDetails.postValue(resource);
                }
            });
        }
        return fetchDetails;
    }

    public boolean isDetailsFinished() {
        return detailsFinished;
    }

    public void setDetailsFinished(boolean detailsFinished) {
        this.detailsFinished = detailsFinished;
    }

    public LiveData<List<DisciplineClassItem>> getDisciplineItems(int groupId) {
        if (classItemsLiveData == null) {
            classItemsLiveData = itemDao.getDisciplineClassItemsFromGroup(groupId);
        }
        return classItemsLiveData;
    }

    public void ignoreGroup(int groupId) {
        repository.ignoreGroup(groupId);
    }

    public void restoreGroup(int groupId) {
        repository.restoreGroup(groupId);
    }
}

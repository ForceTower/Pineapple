package com.forcetower.uefs.vm.service;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.forcetower.uefs.db_service.entity.CreditsMention;
import com.forcetower.uefs.db_service.helper.CreditAndMentions;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.service.ServiceRepository;
import com.forcetower.uefs.service.UNEService;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by João Paulo on 03/06/2018.
 */
public class ServiceGeneralViewModel extends ViewModel {
    private final ServiceRepository repository;
    private LiveData<Resource<List<CreditAndMentions>>> creditsSrc;

    @Inject
    ServiceGeneralViewModel(ServiceRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<CreditAndMentions>>> getCredits() {
        if (creditsSrc == null) creditsSrc = repository.getCredits();
        return creditsSrc;
    }
}

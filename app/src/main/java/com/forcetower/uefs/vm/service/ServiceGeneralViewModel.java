package com.forcetower.uefs.vm.service;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.forcetower.uefs.db_service.entity.AboutField;
import com.forcetower.uefs.db_service.entity.Course;
import com.forcetower.uefs.db_service.entity.QuestionAnswer;
import com.forcetower.uefs.db_service.helper.CreditAndMentions;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.service.ServiceRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by João Paulo on 03/06/2018.
 */
public class ServiceGeneralViewModel extends ViewModel {
    private final ServiceRepository repository;
    private LiveData<Resource<List<CreditAndMentions>>> creditsSrc;
    private LiveData<Resource<List<QuestionAnswer>>> faqSrc;
    private LiveData<Resource<List<AboutField>>> aboutSrc;
    private LiveData<Resource<List<Course>>> coursesSrc;

    @Inject
    ServiceGeneralViewModel(ServiceRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<CreditAndMentions>>> getCredits() {
        if (creditsSrc == null) creditsSrc = repository.getCredits();
        return creditsSrc;
    }

    public LiveData<Resource<List<QuestionAnswer>>> getFAQ() {
        if (faqSrc == null) faqSrc = repository.getFAQ();
        return faqSrc;
    }

    public LiveData<Resource<List<AboutField>>> getAbout() {
        if (aboutSrc == null) aboutSrc = repository.getAbout();
        return aboutSrc;
    }

    public LiveData<Resource<List<Course>>> getCourses() {
        if (coursesSrc == null)
            coursesSrc = repository.getCourses();
        return coursesSrc;
    }
}

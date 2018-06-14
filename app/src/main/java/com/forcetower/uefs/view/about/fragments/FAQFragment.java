package com.forcetower.uefs.view.about.fragments;

import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentAboutFaqBinding;
import com.forcetower.uefs.db_service.entity.QuestionAnswer;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.view.about.adapters.AboutFAQAdapter;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.service.ServiceGeneralViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by João Paulo on 18/04/2018.
 */
public class FAQFragment extends Fragment implements Injectable {
    @Inject
    UEFSViewModelFactory viewModelFactory;

    private AboutFAQAdapter faqAdapter;
    private FragmentAboutFaqBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about_faq, container, false);
        setupRecycler();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ServiceGeneralViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(ServiceGeneralViewModel.class);
        viewModel.getFAQ().observe(this, this::onFAQUpdate);
    }

    private void setupRecycler() {
        faqAdapter = new AboutFAQAdapter(new ArrayList<>());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(faqAdapter);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void onFAQUpdate(@NonNull Resource<List<QuestionAnswer>> listResource) {
        switch (listResource.status) {
            case SUCCESS:
                Timber.d("Success Loading FAQ");
                insertItems(listResource.data);
                break;
            case ERROR:
                Timber.d("Error Loading FAQ");
                Timber.d("Error message: " + listResource.message);
                Timber.d("Error code: " + listResource.code);
            case LOADING:
                Timber.d("Loading FAQ");
                insertItems(listResource.data);
        }
    }

    private void insertItems(List<QuestionAnswer> items) {
        if (items == null) return;

        faqAdapter.setItems(items);
    }
}

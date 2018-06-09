package com.forcetower.uefs.view.about.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentAboutBinding;
import com.forcetower.uefs.db_service.entity.AboutField;
import com.forcetower.uefs.db_service.helper.CreditAndMentions;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.about.adapters.AboutAdapter;
import com.forcetower.uefs.view.about.adapters.CreditsAdapter;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.service.ServiceGeneralViewModel;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static com.forcetower.uefs.util.NetworkUtils.openLink;
import static com.forcetower.uefs.util.WordUtils.validString;

/**
 * Created by João Paulo on 18/04/2018.
 */
public class AboutFragment extends Fragment implements Injectable {
    private CreditsAdapter creditsAdapter;
    private AboutAdapter aboutAdapter;

    @Inject
    UEFSViewModelFactory viewModelFactory;

    private FragmentAboutBinding binding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false);

        setupAboutRecycler();
        setupCreditsRecycler();

        binding.includeAboutFaq.setOnClickListener(v -> navigateToFAQ());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ServiceGeneralViewModel generalViewModel = ViewModelProviders.of(this, viewModelFactory).get(ServiceGeneralViewModel.class);
        generalViewModel.getCredits().observe(this, this::onCreditsChanged);
        generalViewModel.getAbout().observe(this, this::onAboutChanged);
    }

    private void onAboutChanged(Resource<List<AboutField>> listAboutResource) {
        Timber.d("About Status: %s", listAboutResource.status);
        if (listAboutResource.data != null) {
            aboutAdapter.setAbout(listAboutResource.data);
        }
    }

    private void onCreditsChanged(Resource<List<CreditAndMentions>> listCreditsResource) {
        Timber.d("Credits Status: %s", listCreditsResource.status);
        if (listCreditsResource.data != null && !listCreditsResource.data.isEmpty()) {
            creditsAdapter.setMentions(listCreditsResource.data);
        } else if (listCreditsResource.data != null) {
            binding.includeAboutCredits.rvCredits.setVisibility(View.GONE);
        }
    }

    private void navigateToFAQ() {
        Fragment fragment = new FAQFragment();
        if (VersionUtils.isLollipop()) {
            fragment.setEnterTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.END, getResources().getConfiguration().getLayoutDirection())));
            fragment.setAllowEnterTransitionOverlap(false);
            fragment.setAllowReturnTransitionOverlap(false);
        }

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupCreditsRecycler() {
        creditsAdapter = new CreditsAdapter();
        creditsAdapter.setOnMentionClickListener(mention -> {
            if (validString(mention.getLink())) openLink(requireContext(), mention.getLink());
        });
        binding.includeAboutCredits.rvCredits.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.includeAboutCredits.rvCredits.setAdapter(creditsAdapter);
        binding.includeAboutCredits.rvCredits.setNestedScrollingEnabled(false);
    }

    private void setupAboutRecycler() {
        aboutAdapter = new AboutAdapter();
        aboutAdapter.setListener(about -> {
            if (about.getLink() != null) openLink(requireContext(), about.getLink());
        });
        binding.recyclerAbout.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerAbout.setAdapter(aboutAdapter);
        binding.recyclerAbout.setNestedScrollingEnabled(false);
    }
}

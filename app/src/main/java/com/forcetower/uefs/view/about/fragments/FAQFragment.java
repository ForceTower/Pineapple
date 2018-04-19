package com.forcetower.uefs.view.about.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.QuestionAnswer;
import com.forcetower.uefs.util.MockUtils;
import com.forcetower.uefs.view.about.adapters.AboutFAQAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 18/04/2018.
 */
public class FAQFragment extends Fragment {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private AboutFAQAdapter faqAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_faq, container, false);
        ButterKnife.bind(this, view);
        setupRecycler();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        insertItems();
    }

    private void setupRecycler() {
        faqAdapter = new AboutFAQAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(faqAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void insertItems() {
        List<QuestionAnswer> items = MockUtils.getFAQ();
        faqAdapter.setItems(items);
    }
}

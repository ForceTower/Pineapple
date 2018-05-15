package com.forcetower.uefs.view.about.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
public class FAQFragment extends androidx.fragment.app.Fragment {
    @BindView(R.id.recycler_view)
    androidx.recyclerview.widget.RecyclerView recyclerView;

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
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(faqAdapter);
        recyclerView.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator());
    }

    private void insertItems() {
        List<QuestionAnswer> items = MockUtils.getFAQ();
        faqAdapter.setItems(items);
    }
}

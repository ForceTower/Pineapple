package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.DisciplineClassItem;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.connected.adapters.ClassesAdapter;
import com.forcetower.uefs.vm.DisciplinesViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DisciplineClassesFragment extends Fragment implements Injectable {
    public static final String INTENT_GROUP_ID = "group_id";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private ClassesAdapter classesAdapter;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ActivityController controller;

    public static Fragment getFragment(int groupId) {
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_GROUP_ID, groupId);

        Fragment fragment = new DisciplineClassesFragment();
        fragment.setArguments(bundle);
        fragment.setEnterTransition(new Fade());
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discipline_classes, container, false);
        ButterKnife.bind(this, view);

        controller.changeTitle(R.string.discipline_classes);
        controller.getTabLayout().setVisibility(View.INVISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        classesAdapter = new ClassesAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(classesAdapter);


        if (getArguments() != null) {
            int groupId = getArguments().getInt(INTENT_GROUP_ID, 0);

            DisciplinesViewModel disciplinesVM = ViewModelProviders.of(this, viewModelFactory).get(DisciplinesViewModel.class);
            disciplinesVM.getDisciplineItems(groupId).observe(this, this::onItemsUpdate);
        }

        return view;
    }

    private void onItemsUpdate(List<DisciplineClassItem> disciplineClassItems) {
        Timber.d("Class items update!");
        classesAdapter.setClasses(disciplineClassItems);
    }
}

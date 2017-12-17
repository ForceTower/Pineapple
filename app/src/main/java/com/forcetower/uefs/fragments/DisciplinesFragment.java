package com.forcetower.uefs.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.ClassDetailsActivity;
import com.forcetower.uefs.adapters.ui.AllDisciplinesAdapter;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDetails;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassGroup;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 17/12/2017.
 */

public class DisciplinesFragment extends Fragment {
    private RecyclerView rvAllDisciplines;

    public DisciplinesFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disciplines, container, false);
        rvAllDisciplines = view.findViewById(R.id.rv_all_disciplines);

        setupAllDisciplines();

        return view;
    }

    private void setupAllDisciplines() {
        SagresProfile profile = SagresProfile.getCurrentProfile();
        if (profile == null) {
            Log.e(APP_TAG, "Profile is null");
            return;
        }

        rvAllDisciplines.setNestedScrollingEnabled(false);
        AllDisciplinesAdapter adapter = new AllDisciplinesAdapter(getContext(), profile.getClassesDetails(), classClicked);
        rvAllDisciplines.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAllDisciplines.setAdapter(adapter);
        rvAllDisciplines.setNestedScrollingEnabled(false);
    }

    private OnDisciplineClickListener classClicked = new OnDisciplineClickListener() {
        @Override
        public void onDisciplineClick(SagresClassDetails details, SagresClassGroup group) {
            ClassDetailsActivity.startActivity(getContext(), details.getCode(), details.getSemester(), group.getType());
        }
    };

    public interface OnDisciplineClickListener {
        void onDisciplineClick(SagresClassDetails details, SagresClassGroup group);
    }
}

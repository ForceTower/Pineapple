package com.forcetower.uefs.view.class_details;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;

import butterknife.ButterKnife;

/**
 * Created by João Paulo on 30/12/2017.
 */

public class DisciplineGradesFragment extends Fragment {

    public DisciplineGradesFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_details_grades, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}

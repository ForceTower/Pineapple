package com.forcetower.uefs.view.experimental.good_barrel.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.vm.GradesViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import timber.log.Timber;

import static com.forcetower.uefs.util.ValueUtils.toDoubleMod;

/**
 * Created by João Paulo on 16/03/2018.
 */

public class BarrelFragment extends Fragment implements Injectable {
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference disciplinesReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barrel, container, false);
        ButterKnife.bind(this, view);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        disciplinesReference = firebaseDatabase.getReference("disciplines");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GradesViewModel gradesViewModel = ViewModelProviders.of(this, viewModelFactory).get(GradesViewModel.class);
        gradesViewModel.requestAllDisciplineGrades().observe(this, this::onDisciplineGradesReceived);
    }

    private void onDisciplineGradesReceived(List<Discipline> disciplines) {
        if (disciplines == null) return;

        disciplines = removeDuplicates(disciplines);
        if (firebaseUser == null) {
            Timber.d("User is disconnected");
            return;
        }

        postDisciplines(disciplines);
    }

    private void postDisciplines(List<Discipline> disciplines) {
        for (Discipline discipline : disciplines) {
            String finalScore = discipline.getGrade().getFinalScore();
            if (finalScore != null && !finalScore.equalsIgnoreCase("Não divulgada")) {
                if (finalScore.trim().isEmpty()) finalScore = "0,0";

                DatabaseReference codeRef = disciplinesReference.child(discipline.getCode());
                String grade = finalScore;
                codeRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Double value = mutableData.child("overall_mean").getValue(Double.class);
                        String postSemester = mutableData.child("users").child(firebaseUser.getUid()).child("semester").getValue(String.class);

                        if (postSemester == null) {
                            Timber.d("Post semester is null");
                            mutableData.child("users").child(firebaseUser.getUid()).child("semester").setValue(discipline.getSemester());

                            if (value == null) {
                                Timber.d("Value is null");
                                mutableData.child("overall_mean").setValue(toDoubleMod(grade));
                            }

                            return Transaction.success(mutableData);
                        }


                        if (postSemester.equalsIgnoreCase(discipline.getSemester())) {
                            Timber.d("Already posted");
                            return Transaction.success(mutableData);
                        }

                        mutableData.child("users").child(firebaseUser.getUid()).child("semester").setValue(discipline.getSemester());
                        Double other = toDoubleMod(grade);
                        if (value == null) {
                            value = other;
                        }
                        mutableData.child("overall_mean").setValue((value + other)/2);
                        Timber.d("Success");
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
            }
        }
    }

    private List<Discipline> removeDuplicates(List<Discipline> disciplines) {
        List<Discipline> clean = new ArrayList<>();

        for (Discipline discipline: disciplines) {
            if(clean.contains(discipline)) {
                Discipline other = clean.get(clean.indexOf(discipline));
                double o = toDoubleMod(other.getGrade().getFinalScore());
                double d = toDoubleMod(discipline.getGrade().getFinalScore());

                if (o < d) {
                    clean.remove(other);
                    clean.add(discipline);
                    Timber.d("Removed duplicated: %s", discipline.getCode());
                }
            } else {
                clean.add(discipline);
            }
        }

        return clean;
    }
}

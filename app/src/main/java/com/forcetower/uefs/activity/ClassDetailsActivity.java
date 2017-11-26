package com.forcetower.uefs.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.base.UEFSBaseActivity;
import com.forcetower.uefs.adapters.ui.GradesAdapter;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDay;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;

public class ClassDetailsActivity extends UEFSBaseActivity {
    private static SagresClassDay sagresClass;

    public static void startActivity(Context context, SagresClassDay classDay) {
        sagresClass = classDay;
        Intent intent = new Intent(context, ClassDetailsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Utils.isLollipop()) {
            toolbar.setElevation(10);
        }

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.class_details);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView className = findViewById(R.id.tv_class_name);
        className.setText(getString(R.string.class_details_cod_name, sagresClass.getClassCode(), sagresClass.getClassName()));

        fillWithGrades();
    }

    private void fillWithGrades() {
        RecyclerView gradesRecyclerView = findViewById(R.id.rv_grades);
        RelativeLayout gradesRelativeLayout = findViewById(R.id.rl_grades);

        if (SagresProfile.getCurrentProfile() == null || SagresProfile.getCurrentProfile().getGrades() == null) {
            gradesRecyclerView.setVisibility(View.GONE);
            gradesRelativeLayout.setVisibility(View.GONE);
        }

        SagresGrade grade = SagresProfile.getCurrentProfile().getGrades().get(sagresClass.getClassCode());

        if (Utils.isLollipop()) {
            gradesRelativeLayout.setNestedScrollingEnabled(false);
            gradesRelativeLayout.setElevation(4);
        }

        GradesAdapter gradesAdapter = new GradesAdapter(this, grade);

        gradesRelativeLayout.setBackgroundResource(android.R.color.white);
        gradesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        gradesRecyclerView.setAdapter(gradesAdapter);
        gradesRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        gradesRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

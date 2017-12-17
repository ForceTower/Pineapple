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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.base.UEFSBaseActivity;
import com.forcetower.uefs.adapters.ui.GradesAdapter;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDay;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDetails;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassGroup;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.managers.SagresProfileManager;

public class ClassDetailsActivity extends UEFSBaseActivity {
    private static final String CLASS_CODE_KEY = "class_code";
    private static final String SEMESTER_KEY = "semester";
    private static final String GROUP_KEY = "group";

    private SagresClassDetails details;
    private SagresClassGroup detailsGroup;
    //View References card one
    private TextView classCredits;
    private TextView classPeriod;
    private TextView classTeacher;
    private TextView classDepartment;
    private TextView classMissLimit;
    //View References Card Two
    private ImageView classAlertImage;
    private TextView classesMissed;
    private TextView classMissRemain;
    private ProgressBar classesMissedProgress;
    //View References Card Three
    private TextView classPrevious;
    private TextView classNext;

    public static void startActivity(Context context, String classCode, String semester, String group) {
        Intent intent = new Intent(context, ClassDetailsActivity.class);
        intent.putExtra(CLASS_CODE_KEY, classCode);
        intent.putExtra(SEMESTER_KEY, semester);
        intent.putExtra(GROUP_KEY, group);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        if (!makeSureEverythingIsOkayOrFinish()) return;

        findDesiredClass();
        if (details == null) {
            Toast.makeText(this, R.string.class_not_found_dev_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        classCredits = findViewById(R.id.tv_class_credits);
        classPeriod = findViewById(R.id.tv_class_period);
        classTeacher = findViewById(R.id.tv_class_teacher);
        classDepartment = findViewById(R.id.tv_class_department);
        classMissLimit = findViewById(R.id.tv_class_miss_limit);

        classAlertImage = findViewById(R.id.iv_icon_class_missed);
        classesMissed = findViewById(R.id.tv_classes_missed);
        classesMissedProgress = findViewById(R.id.pb_classes_missed);
        classMissRemain = findViewById(R.id.tv_miss_remain);

        classPrevious = findViewById(R.id.tv_last_class);
        classNext = findViewById(R.id.tv_next_class);

        classCredits.setText(getString(R.string.class_details_credits, details.getCredits()));
        classPeriod.setText(getString(R.string.class_details_class_period, details.isDraft() ? "???" : detailsGroup.getClassPeriod()));
        classTeacher.setText(getString(R.string.class_details_teacher, details.isDraft() ? "???" : detailsGroup.getTeacher()));
        classDepartment.setText(details.isDraft() ? getString(R.string.class_details_department, "???") : detailsGroup.getDepartment());
        classMissLimit.setText(getString(R.string.class_details_miss_limit, details.isDraft() ? "???" : detailsGroup.getMissLimit()));

        classesMissed.setText(getString(R.string.class_details_classes_missed, details.getMissedClasses()));
        int missed = 0;
        int maxAllowed = 1;
        boolean visible = true;
        try {
            missed = Integer.parseInt(details.getMissedClasses());
            maxAllowed = Integer.parseInt(detailsGroup.getMissLimit());
        } catch (Exception ignored){
            ignored.printStackTrace();
            visible = false;
        }

        if (!visible) classesMissedProgress.setVisibility(View.INVISIBLE);
        else classesMissedProgress.setProgress((missed*100)/maxAllowed);
        classMissRemain.setText(getString(R.string.class_details_miss_remain, visible ? ((maxAllowed - missed) + "") : "???"));

        classPrevious.setText(getString(R.string.class_details_previous_class, details.getLastClass()));
        classPrevious.setText(getString(R.string.class_details_next_class, details.getLastClass()));

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


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean makeSureEverythingIsOkayOrFinish() {
        if (SagresProfile.getCurrentProfile() == null) {
            boolean loaded = SagresProfileManager.getInstance().loadCurrentProfile();
            if (!loaded) {
                Toast.makeText(this, R.string.this_is_and_error, Toast.LENGTH_SHORT).show();
                finish();
                return false;
            }
        }
        return true;
    }

    private void findDesiredClass() {
        if (details != null)
            return;
        if (getIntent() == null) {
            details = null;
            return;
        }
        if (getIntent().getExtras() == null) {
            details = null;
            return;
        }

        Bundle bundle = getIntent().getExtras();
        String semester = bundle.getString(SEMESTER_KEY);
        String classCode = bundle.getString(CLASS_CODE_KEY);
        String group = bundle.getString(GROUP_KEY);

        if (semester == null || classCode == null)

        details = SagresProfile.getCurrentProfile().getClassDetailsWithParams(classCode, semester);
        findDesiredGroup(group);
    }

    private void findDesiredGroup(String group) {
        if (details == null)
            return;

        if (details.getGroups() == null)
            return;

        if (group == null && details.getGroups().size() > 0)
            detailsGroup = details.getGroups().get(0);

        for (SagresClassGroup classGroup : details.getGroups()) {
            if (classGroup.getType().equalsIgnoreCase(group)) {
                this.detailsGroup = classGroup;
                return;
            }
        }
    }
}

package com.forcetower.uefs.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.base.UEFSBaseActivity;
import com.forcetower.uefs.adapters.ui.GradesAdapter;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDetails;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassGroup;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.managers.SagresProfileManager;
import com.forcetower.uefs.sagres_sdk.parsers.SagresFullClassParser;

import java.util.List;

import static com.forcetower.uefs.Constants.APP_TAG;

public class ClassDetailsActivity extends UEFSBaseActivity {
    private static final String CLASS_CODE_KEY = "class_code";
    private static final String SEMESTER_KEY = "semester";
    private static final String GROUP_KEY = "group";

    private SagresClassDetails details;
    private SagresClassGroup detailsGroup;
    //View References card details
    private TextView classCredits;
    private TextView classPeriod;
    private TextView classTeacher;
    private TextView classDepartment;
    private TextView classMissLimit;
    //View References card faults
    private ImageView classAlertImage;
    private TextView classesMissed;
    private TextView classMissRemain;
    private ProgressBar classesMissedProgress;
    //View References card prev and next
    private TextView classPrevious;
    private TextView classNext;
    //View References card identification
    private TextView className;
    private TextView classGroup;
    //View References card grades
    private RecyclerView classGrades;

    private String semester;
    private String classCode;
    private String group;
    private boolean refreshing = false;

    private ProgressBar loadDetailsProgressBar;

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

        findViewById(R.id.draft_card).setOnClickListener(view -> refreshClass());
        if (details != null && detailsGroup != null && !detailsGroup.isDraft()) {
            findViewById(R.id.classes_pn_card).setOnClickListener(view -> DisciplineClassesActivity.startActivity(this, detailsGroup.getClasses()));
            findViewById(R.id.tv_show_all_classes).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.tv_show_all_classes).setVisibility(View.GONE);
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
        loadDetailsProgressBar = findViewById(R.id.progress_bar_load_details);

        className = findViewById(R.id.tv_class_name);
        classGroup = findViewById(R.id.tv_class_group);

        classGrades = findViewById(R.id.rv_discipline_grades);

        if (detailsGroup == null) {
            detailsGroup = new SagresClassGroup(null, null, null, null, null, null);
            System.out.println("It's null.. it needs something to hold it back");
        }
        if (!detailsGroup.isDraft()) findViewById(R.id.draft_card).setVisibility(View.GONE);

        classCredits.setText(getString(R.string.class_details_credits, detailsGroup.isDraft() ? details.getCredits() : detailsGroup.getCredits()));
        classPeriod.setText(getString(R.string.class_details_class_period, detailsGroup.isDraft() ? "???" : detailsGroup.getClassPeriod()));
        classTeacher.setText(getString(R.string.class_details_teacher, detailsGroup.isDraft() ? "???" : detailsGroup.getTeacher()));
        classDepartment.setText(detailsGroup.isDraft() ? getString(R.string.class_details_department, "???") : detailsGroup.getDepartment());
        classMissLimit.setText(getString(R.string.class_details_miss_limit, detailsGroup.isDraft() ? "???" : detailsGroup.getMissLimit()));
        classesMissed.setText(getString(R.string.class_details_classes_missed, details.getMissedClasses()));

        String fullName = details.getCode() + " - " + details.getName();
        className.setText(fullName);
        if (detailsGroup != null) classGroup.setText(detailsGroup.getType());
        else classGroup.setText("???");

        int missed = 0;
        int maxAllowed = 1;
        boolean visible = true;
        try {
            missed = Integer.parseInt(details.getMissedClasses());
            if (detailsGroup != null && detailsGroup.getMissLimit() != null && !detailsGroup.getMissLimit().trim().isEmpty()) maxAllowed = Integer.parseInt(detailsGroup.getMissLimit());
            else maxAllowed = Integer.parseInt(details.getCredits())/4;

            int remains = maxAllowed - missed;
            if (remains > (maxAllowed / 4) + 1 && remains <= (maxAllowed / 2)) {
                classAlertImage.setImageResource(R.drawable.ic_tag_faces_neutral_black_24dp);
            } else if (remains <= (maxAllowed / 4) + 1 && remains >= 0)
                classAlertImage.setImageResource(R.drawable.ic_tag_faces_sad_black_24dp);
            else if (remains < 0)
                classAlertImage.setImageResource(R.drawable.ic_tag_faces_dead_black_24dp);

            classMissLimit.setText(getString(R.string.class_details_miss_limit, detailsGroup.isDraft() ? maxAllowed+"" : detailsGroup.getMissLimit()));
        } catch (Exception ignored){
            ignored.printStackTrace();
            visible = false;
        }

        int remains = maxAllowed - missed;

        if (!visible) classesMissedProgress.setVisibility(View.INVISIBLE);
        else classesMissedProgress.setProgress((missed*100)/maxAllowed);

        if (remains > 0) classMissRemain.setText(getString(R.string.class_details_miss_remain, visible ? (remains + "") : "???"));
        else if (remains == 0) classMissRemain.setText(getString(R.string.class_details_can_not_miss_anymore));
        else classMissRemain.setText(getString(R.string.class_details_failed_by_lack));

        classPrevious.setText(getString(R.string.class_details_previous_class, details.getLastClass()));
        classNext.setText(getString(R.string.class_details_next_class, details.getNextClass()));


        SagresGrade grade = null;
        if (details != null) grade = SagresProfile.getCurrentProfile().getGradesOfClass(details.getCode(), details.getSemester());
        classGrades.setLayoutManager(new LinearLayoutManager(this));
        classGrades.setAdapter(new GradesAdapter(this, grade));
        classGrades.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

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
        } else if (item.getItemId() == R.id.menu_refresh) {
            refreshClass();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshClass() {
        if (refreshing) return;
        Log.i(APP_TAG, "Refresh");
        Utils.fadeIn(loadDetailsProgressBar, this);

        Runnable runnable = () -> {
            refreshing = true;
            List<SagresClassDetails> classDetails = SagresFullClassParser.loginConnectAndGetClassesDetails(semester, classCode, group, false);
            SagresProfile.getCurrentProfile().updateClassDetails(classDetails);
            Log.i(APP_TAG, "Finished... Start Interface Update");
            updateViewWithNewInfo();
            refreshing = false;
        };

        SagresPortalSDK.getExecutor().execute(runnable);
    }

    private void updateViewWithNewInfo() {
        if(isFinishing()) {
            Log.i(APP_TAG, "Is finishing, it will not recreate");
            return;
        }

        //SagresClassDetails details = SagresProfile.getCurrentProfile().getClassDetailsWithParams(classCode, semester);

        runOnUiThread(() -> {
            loadDetailsProgressBar.setVisibility(View.GONE);
            recreate();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.class_details, menu);
        return true;
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
        semester = bundle.getString(SEMESTER_KEY);
        classCode = bundle.getString(CLASS_CODE_KEY);
        group = bundle.getString(GROUP_KEY);

        if (semester == null || classCode == null) {
            return;
        }

        details = SagresProfile.getCurrentProfile().getClassDetailsWithParams(classCode, semester);
        findDesiredGroup(group);
    }

    private void findDesiredGroup(String group) {
        if (details == null)
            return;

        if (details.getGroups() == null)
            return;

        if (group == null && details.getGroups().size() > 0) {
            detailsGroup = details.getGroups().get(0);
            return;
        }

        if (details.getGroups().size() == 1) {
            detailsGroup = details.getGroups().get(0);
            return;
        }

        for (SagresClassGroup classGroup : details.getGroups()) {
            if (classGroup.getType() == null && details.getGroups().size() == 1) {
                this.detailsGroup = classGroup;
                return;
            }

            if (classGroup.getType().equalsIgnoreCase(group)) {
                this.detailsGroup = classGroup;
                return;
            }
        }
    }
}

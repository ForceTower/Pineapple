package com.forcetower.uefs.view.class_details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.forcetower.uefs.R;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassItem;
import com.forcetower.uefs.view.UEFSBaseActivity;
import com.forcetower.uefs.view.adapters.ClassesAdapter;

import java.util.List;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 22/12/2017.
 */
public class DisciplineClassesActivity extends UEFSBaseActivity {
    public static void startActivity(Context context, List<SagresClassItem> items) {
        Intent intent = new Intent(context, DisciplineClassesActivity.class);
        classes = items;
        context.startActivity(intent);
    }

    private RecyclerView rv_classes;
    private static List<SagresClassItem> classes;

    public DisciplineClassesActivity() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details_classes);
        rv_classes = findViewById(R.id.rv_classes);
        Log.i(APP_TAG, "No way create " + classes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Utils.isLollipop()) {
            toolbar.setElevation(10);
        }

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.classes);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (classes != null) setupClasses();
        else finish();
    }

    private void setupClasses() {
        Log.i(APP_TAG, "No way 2");
        ClassesAdapter adapter = new ClassesAdapter(this, classes);
        rv_classes.removeAllViews();
        rv_classes.setLayoutManager(new LinearLayoutManager(this));
        rv_classes.setAdapter(adapter);
        rv_classes.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(APP_TAG, "No way resume " + classes);
        if (classes == null) finish();
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

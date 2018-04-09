package com.forcetower.uefs.view.about;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.CreditsMention;
import com.forcetower.uefs.util.MockUtils;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.about.adapters.CreditsAdapter;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.util.List;

import butterknife.BindView;
import dagger.android.AndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

import static com.forcetower.uefs.util.NetworkUtils.openLink;
import static com.forcetower.uefs.util.WordUtils.validString;

public class AboutActivity extends UBaseActivity implements HasSupportFragmentInjector {
    @BindView(R.id.version_info)
    TextView versionInfo;
    @BindView(R.id.rv_credits)
    RecyclerView rvCredits;
    @BindView(R.id.cv_about_me)
    CardView cvAboutMe;
    @BindView(R.id.cv_enjoy)
    CardView cvEnjoy;

    @SuppressWarnings("FieldCanBeLocal")
    private CreditsAdapter creditsAdapter;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_about, savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (VersionUtils.isLollipop()) toolbar.setElevation(10);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.about_app_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        String version = "0.0u";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException ignored) {}
        versionInfo.setText(getString(R.string.creator, version));

        setupCreditsRecycler();
        cvAboutMe.setOnClickListener(view -> openLink(this, "https://facebook.com/ForceTower"));
        cvEnjoy.setOnClickListener(view -> openLink(this, "https://facebook.com/ForceTower"));
    }

    private void setupCreditsRecycler() {
        List<CreditsMention> mentions = MockUtils.getCredits();

        creditsAdapter = new CreditsAdapter(mentions);
        creditsAdapter.setOnMentionClickListener(mention -> {
            if (validString(mention.getLink())) openLink(this, mention.getLink());
        });
        rvCredits.setLayoutManager(new LinearLayoutManager(this));
        rvCredits.setAdapter(creditsAdapter);
        rvCredits.setNestedScrollingEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void thirdParty(View view) {
        new LibsBuilder()
                .withActivityStyle(Libs.ActivityStyle.LIGHT)
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .start(this);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return null;
    }
}

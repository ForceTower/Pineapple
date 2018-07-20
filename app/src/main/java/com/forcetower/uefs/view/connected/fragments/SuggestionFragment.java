package com.forcetower.uefs.view.connected.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentSuggestionBinding;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.connected.ActivityController;

import timber.log.Timber;

public class SuggestionFragment extends Fragment {
    public static final String MESSAGE_CAUSE = "exception_message";
    public static final String STACK_TRACE = "stack_trace";

    private ActivityController controller;
    private FragmentSuggestionBinding binding;

    public static SuggestionFragment createFragment(String message, String stackTrace) {
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_CAUSE, message);
        bundle.putString(STACK_TRACE, stackTrace);

        SuggestionFragment fragment = new SuggestionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static SuggestionFragment createFragment() {
        return new SuggestionFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            controller = (ActivityController) context;
        } catch (ClassCastException e) {
            Timber.e("%s must implement ActivityController", context.getClass().getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_suggestion, container, false);

        if (controller.getTabLayout() != null) controller.getTabLayout().setVisibility(View.GONE);
        controller.changeTitle(R.string.title_suggestion);

        if (getArguments() != null && getArguments().getString(STACK_TRACE) != null) {
            String message = getArguments().getString(MESSAGE_CAUSE);
            String stack   = getArguments().getString(STACK_TRACE);
            String finalMessage = "- Não altere abaixo -\nCausa: " + message + "\n\n" + stack;
            binding.etSuggestion.setText(finalMessage);
            binding.etSuggestion.setEnabled(false);
        }


        binding.btnSubmit.setOnClickListener(v -> {
            String body = binding.etSuggestion.getText().toString();
            if (body.trim().isEmpty()) {
                Toast.makeText(requireContext(), R.string.empty_suggestion_field, Toast.LENGTH_SHORT).show();
            } else {
                composeEmail(body);

                if (VersionUtils.isLollipop()) setExitTransition(new Fade());
                requireActivity().onBackPressed();
            }
        });

        return binding.getRoot();
    }

    public void composeEmail(String body) {
        try {
            PackageInfo pInfo = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            int code = pInfo.versionCode;
            body = body.concat("\n\nVersion Name: " + version);
            body = body.concat("\nVersion Code: " + code);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","forcetowerandroid@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[UNES]App_Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

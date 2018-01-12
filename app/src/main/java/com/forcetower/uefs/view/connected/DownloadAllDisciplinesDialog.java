package com.forcetower.uefs.view.connected;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.forcetower.uefs.R;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDetails;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.parsers.SagresFullClassParser;

import java.util.List;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 11/01/2018.
 */

public class DownloadAllDisciplinesDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.download_all)
                .setMessage(R.string.dialog_download_all_classes_conf)
                .setPositiveButton(R.string.sure, (dialog, which) -> SagresPortalSDK.getExecutor().execute(()-> {
                    List<SagresClassDetails> allDetails = SagresFullClassParser.loginConnectAndGetClassesDetails(null, null, null, false);
                    if (SagresProfile.getCurrentProfile() != null) {
                        SagresProfile.getCurrentProfile().updateClassDetails(allDetails);
                    } else {
                        Log.e(APP_TAG, "You just got cucked");
                    }
                }))
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dismiss();
                })
                .create();
    }
}

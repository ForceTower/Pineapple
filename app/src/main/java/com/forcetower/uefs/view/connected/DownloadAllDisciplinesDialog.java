package com.forcetower.uefs.view.connected;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.forcetower.uefs.R;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.parsers.SagresFullClassParser;

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
                    SagresFullClassParser.loginConnectAndGetClassesDetails(null, null, null, false);
                }))
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dismiss();
                })
                .create();
    }
}

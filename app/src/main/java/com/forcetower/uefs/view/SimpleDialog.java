package com.forcetower.uefs.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;

public class SimpleDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private static final String EXTRA_ID = "id";
    private static final String EXTRA_MESSAGE = "message";
    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_BUTTONS = "buttons";
    private static final String DIALOG_TAG = "SimpleDialog";

    private int dialogId;
    private FragmentDialogInterface listener;

    public static SimpleDialog newDialog(int id, String title, String message, int[] buttonTexts) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_ID, id);
        bundle.putString(EXTRA_TITLE, title);
        bundle.putString(EXTRA_MESSAGE, message);
        bundle.putIntArray(EXTRA_BUTTONS, buttonTexts);

        SimpleDialog dialog = new SimpleDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            String title = getArguments().getString(EXTRA_TITLE);
            String message = getArguments().getString(EXTRA_MESSAGE);
            int[] buttons = getArguments().getIntArray(EXTRA_BUTTONS);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(message);

            if (buttons != null) {
                switch (buttons.length) {
                    case 3:
                        alertDialogBuilder.setNeutralButton(buttons[2], this);
                    case 2:
                        alertDialogBuilder.setNegativeButton(buttons[1], this);
                    case 1:
                        alertDialogBuilder.setPositiveButton(buttons[0], this);
                }
            }

            return alertDialogBuilder.create();
        } else {
            throw new RuntimeException("Somebody touch my spaghetti!");
        }
    }

    public void setListener(FragmentDialogInterface listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        listener.onClick(dialogId, which);
    }

    public void openDialog(FragmentManager supportFragmentManager) {
        if (supportFragmentManager.findFragmentByTag(DIALOG_TAG) == null) {
            show(supportFragmentManager, DIALOG_TAG);
        }
    }

    public interface FragmentDialogInterface {
        void onClick(int id, int which);
    }
}
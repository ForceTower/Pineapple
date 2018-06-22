package com.forcetower.uefs.view.connected.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentInsertReminderBinding;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.base.TodoItemViewModel;

import java.util.Calendar;

import javax.inject.Inject;

import static com.forcetower.uefs.util.WordUtils.stringify;
import static com.forcetower.uefs.util.WordUtils.validString;

/**
 * Created by João Paulo on 22/06/2018.
 */
public class CreateReminderFragment extends BottomSheetDialogFragment implements Injectable {
    @Inject
    UEFSViewModelFactory viewModelFactory;

    private FragmentInsertReminderBinding binding;
    private TodoItemViewModel viewModel;

    private int date, mont, year;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_insert_reminder, container, false);
        binding.btnSave.setOnClickListener(v -> saveReminder());
        binding.etDateLimit.setOnClickListener(v -> openDatePicker());
        binding.etDateLimit.setOnLongClickListener(v -> clearDateLimit());

        Calendar calendar = Calendar.getInstance();
        date = calendar.get(Calendar.DAY_OF_MONTH);
        mont = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TodoItemViewModel.class);
    }

    private void openDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, sYear, sMonth, sDay) -> {
            this.year = sYear;
            this.mont = sMonth;
            this.date = sDay;
            sMonth++;
            binding.etDateLimit.setText(getString(R.string.date_format, stringify(sDay), stringify(sMonth), sYear));
            binding.etDateLimit.clearFocus();
        }, year, mont, date);

        datePickerDialog.setTitle(R.string.select_date);
        datePickerDialog.show();
    }

    private boolean clearDateLimit() {
        binding.etDateLimit.setText("");
        return true;
    }

    private void saveReminder() {
        boolean error = false;
        String title = binding.etTitle.getText().toString().trim();
        String message = binding.etMessage.getText().toString().trim();
        String date = binding.etDateLimit.getText().toString().trim();
        boolean hasLimit = validString(date);

        if (!validString(message)) {
            binding.etMessage.setError(getString(R.string.field_is_mandatory));
            binding.etMessage.requestFocus();
            error = true;
        }

        if (!validString(title)) {
            binding.etTitle.setError(getString(R.string.reminder_title_too_short));
            binding.etTitle.requestFocus();
            error = true;
        }

        if (error) return;

        viewModel.createTodoItem(title, message, date, hasLimit);
        dismiss();
    }
}

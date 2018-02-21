package com.forcetower.uefs.view.class_details;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.forcetower.uefs.R;
import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.database.entities.ATodoItem;
import com.forcetower.uefs.view_models.TodoItemCollectionViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 30/12/2017.
 */

public class CreateTodoItemDialog extends DialogFragment {
    @BindView(R.id.et_task_title)
    EditText etTitle;
    @BindView(R.id.et_text_date)
    EditText etDate;
    @BindView(R.id.cb_has_date_limit)
    AppCompatCheckBox cHasLimit;
    @BindView(R.id.save_btn)
    Button bSave;
    @BindView(R.id.cancel_btn)
    Button bCancel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    TodoItemCollectionViewModel viewModel;

    private Calendar calendar;
    private String discipline;

    public CreateTodoItemDialog() { super(); }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_create_todo_item, container, false);
        ButterKnife.bind(this, view);
        ((UEFSApplication)getContext().getApplicationContext()).getApplicationComponent().inject(this);

        calendar = Calendar.getInstance();
        updateDateText(calendar);

        etDate.setOnClickListener(this::dateClick);
        bSave.setOnClickListener(this::saveTodoItem);
        bCancel.setOnClickListener((ignored)->dismiss());
        cHasLimit.setOnClickListener(this::limitClicked);

        if (getArguments() != null) {
            discipline = getArguments().getString("discipline", null);
        }

        setCancelable(true);
        return view;
    }

    private void limitClicked(View view) {
        etDate.setEnabled(cHasLimit.isChecked());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TodoItemCollectionViewModel.class);
    }

    private void saveTodoItem(View view) {
        String title = etTitle.getText().toString();
        String date = etDate.getText().toString();
        boolean timeLim = cHasLimit.isChecked();

        ATodoItem item = new ATodoItem(discipline, title, date, timeLim);
        viewModel.addTodoItem(item);
        Log.i(APP_TAG, "Inserted item");
        dismiss();
    }

    private void dateClick(View view) {
        new DatePickerDialog(getContext(), date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateText(Calendar calendar) {
        String format = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        etDate.setText(sdf.format(calendar.getTime()));
    }

    DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateText(calendar);
    };
}

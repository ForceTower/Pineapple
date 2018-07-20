package com.forcetower.uefs.view.connected.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ItemScheduleClassBinding;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.view.connected.LocationClickListener;

import static com.forcetower.uefs.util.WordUtils.validString;

/**
 * Created by João Paulo on 07/03/2018.
 */

class DayClassAdapter extends ListAdapter<DisciplineClassLocation, DayClassAdapter.DisciplineHolder> {
    private boolean style;
    private LocationClickListener onClickListener;

    DayClassAdapter(boolean style) {
        super(new DiffUtil.ItemCallback<DisciplineClassLocation>() {
            @Override
            public boolean areItemsTheSame(DisciplineClassLocation oldItem, DisciplineClassLocation newItem) {
                return oldItem.getUid() == newItem.getUid();
            }

            @Override
            public boolean areContentsTheSame(DisciplineClassLocation oldItem, DisciplineClassLocation newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.style = style;
    }

    @NonNull
    @Override
    public DisciplineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemScheduleClassBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_schedule_class, parent, false);
        return new DisciplineHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DisciplineHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public void setOnClickListener(LocationClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    class DisciplineHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final ItemScheduleClassBinding binding;

        DisciplineHolder(ItemScheduleClassBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> onClick());
        }

        private void onClick() {
            if (onClickListener != null) {
                int position = getAdapterPosition();
                onClickListener.onDisciplineGroupClicked(getItem(position));
            }
        }

        public void bind(DisciplineClassLocation discipline) {
            Context context = binding.getRoot().getContext();
            String location = "";
            //if (validString(discipline.getCampus())) location = discipline.getCampus().trim();

            if (validString(discipline.getModulo())) {
                if (location.isEmpty()) location = discipline.getModulo().trim();
                else location = location + " - " + discipline.getModulo().trim();
            }

            if (validString(discipline.getRoom())) {
                if (location.isEmpty()) location = discipline.getRoom().trim();
                else location = location + " - " + discipline.getRoom().trim();
            }

            binding.tvClassName.setText(discipline.getClassName().trim());
            binding.tvClassLocation.setText(location);

            if (!style) binding.tvClassTime.setText(context.getString(R.string.discipline_start_end_time, discipline.getStartTime(), discipline.getEndTime()));
            else binding.tvClassTime.setText(context.getString(R.string.discipline_code_group, discipline.getClassCode(), discipline.getClassGroup()));
        }
    }
}

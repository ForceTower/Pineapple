package com.forcetower.uefs.view.connected.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.view.connected.LocationClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.forcetower.uefs.util.WordUtils.validString;

/**
 * Created by João Paulo on 07/03/2018.
 */

class DayClassAdapter extends RecyclerView.Adapter<DayClassAdapter.DisciplineHolder> {
    private Context context;
    private List<DisciplineClassLocation> disciplines;
    private boolean style;
    private LocationClickListener onClickListener;

    DayClassAdapter(@NonNull Context context, @NonNull List<DisciplineClassLocation> disciplines, boolean style) {
        this.context = context;
        this.disciplines = disciplines;
        this.style = style;
    }

    @NonNull
    @Override
    public DisciplineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_class, parent, false);
        return new DisciplineHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisciplineHolder holder, int position) {
        holder.bind(disciplines.get(position));
    }

    @Override
    public int getItemCount() {
        return disciplines.size();
    }

    public void setDisciplines(List<DisciplineClassLocation> locations) {
        this.disciplines.clear();
        this.disciplines.addAll(locations);
        notifyDataSetChanged();
    }

    public void setOnClickListener(LocationClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    class DisciplineHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_class_name)
        TextView tvClassName;
        @BindView(R.id.tv_class_time)
        TextView tvClassTime;
        @BindView(R.id.tv_class_location)
        TextView tvClassLocation;

        DisciplineHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> onClick());
        }

        private void onClick() {
            if (onClickListener != null) {
                int position = getAdapterPosition();
                onClickListener.onDisciplineGroupClicked(disciplines.get(position));
            }
        }

        public void bind(DisciplineClassLocation discipline) {
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

            tvClassName.setText(discipline.getClassName().trim());
            tvClassLocation.setText(location);

            if (!style) tvClassTime.setText(context.getString(R.string.discipline_start_end_time, discipline.getStartTime(), discipline.getEndTime()));
            else tvClassTime.setText(context.getString(R.string.discipline_code_group, discipline.getClassCode(), discipline.getClassGroup()));
        }
    }
}

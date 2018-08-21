package com.forcetower.uefs.view.connected.adapters;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ItemDisciplinesDisciplineBinding;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.view.connected.DisciplineClickListener;

import java.util.List;

/**
 * Created by João Paulo on 07/03/2018.
 */

class DisciplinesAdapter extends RecyclerView.Adapter<DisciplinesAdapter.DisciplineHolder> {
    private List<Discipline> disciplines;
    private Context context;
    private DisciplineClickListener clickListener;

    DisciplinesAdapter(Context context, List<Discipline> disciplines) {
        this.disciplines = disciplines;
        this.context = context;
    }

    @NonNull
    @Override
    public DisciplineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDisciplinesDisciplineBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_disciplines_discipline, parent, false);
        return new DisciplineHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DisciplineHolder holder, int position) {
        holder.bind(disciplines.get(position));
    }

    public void setDisciplines(List<Discipline> disciplines) {
        this.disciplines.clear();
        this.disciplines.addAll(disciplines);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return disciplines.size();
    }

    public void setClickListener(DisciplineClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class DisciplineHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ItemDisciplinesDisciplineBinding binding;

        DisciplineHolder(ItemDisciplinesDisciplineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(Discipline discipline) {
            binding.tvClassName.setText(discipline.getName().trim());
            binding.tvAdditionalInfo.setText(context.getString(R.string.class_additional_info_format,
                    discipline.getCode(),
                    discipline.getCredits(),
                    discipline.getMissedClasses()));
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Discipline discipline = disciplines.get(position);
            if (clickListener != null) {
                clickListener.onDisciplineClick(discipline);
            }
        }
    }
}

package com.forcetower.uefs.view.connected.adapters;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ItemGradeDisciplineBinding;
import com.forcetower.uefs.db.entity.Discipline;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class DisciplineGradesAdapter extends ListAdapter<Discipline, DisciplineGradesAdapter.DisciplineHolder> {
    private final RecyclerView.RecycledViewPool viewPool;

    public DisciplineGradesAdapter() {
        super(new DiffUtil.ItemCallback<Discipline>() {
            @Override
            public boolean areItemsTheSame(Discipline oldItem, Discipline newItem) {
                return oldItem.getUid() == newItem.getUid();
            }

            @Override
            public boolean areContentsTheSame(Discipline oldItem, Discipline newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.viewPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public DisciplineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGradeDisciplineBinding binding = DataBindingUtil.inflate( LayoutInflater.from(parent.getContext()), R.layout.item_grade_discipline, parent, false);
        binding.recyclerView.setRecycledViewPool(viewPool);
        return new DisciplineHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DisciplineHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class DisciplineHolder extends RecyclerView.ViewHolder {
        private final ItemGradeDisciplineBinding binding;
        private final GradesAdapter adapter;

        DisciplineHolder(ItemGradeDisciplineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
            adapter = new GradesAdapter();
            binding.recyclerView.setAdapter(adapter);
        }

        public void bind(Discipline discipline) {
            binding.setDiscipline(discipline);
            if (discipline.getGrade() != null) {
                binding.meanText.setText(discipline.getGrade().getFinalScore());
            } else {
                binding.meanText.setText("--");
            }

            adapter.setItems(discipline.getSections(), discipline.getGrade());
        }
    }
}

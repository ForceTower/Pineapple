package com.forcetower.uefs.view.connected.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.view.connected.DisciplineClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_disciplines_discipline, parent, false);
        return new DisciplineHolder(view);
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
        @BindView(R.id.tv_class_name)
        TextView tvClassName;
        @BindView(R.id.tv_additional_info)
        TextView tvAdditionalInfo;
        @BindView(R.id.iv_draft_item)
        ImageView ivDraftImage;

        DisciplineHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(Discipline discipline) {
            tvClassName.setText(discipline.getName().trim());
            tvAdditionalInfo.setText(context.getString(R.string.class_additional_info_format,
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

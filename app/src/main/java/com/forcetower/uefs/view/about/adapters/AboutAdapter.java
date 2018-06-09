package com.forcetower.uefs.view.about.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.CardAboutItemBinding;
import com.forcetower.uefs.db_service.entity.AboutField;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by João Paulo on 09/06/2018.
 */
public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.AboutHolder> {
    private final List<AboutField> about;
    private AboutClickListener listener;

    public AboutAdapter() {
        about = new ArrayList<>();
    }

    @NonNull
    @Override
    public AboutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardAboutItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.card_about_item, parent, false);
        return new AboutHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutHolder holder, int position) {
        holder.bind(about.get(position));
    }

    @Override
    public int getItemCount() {
        return about.size();
    }

    public void setAbout(List<AboutField> about) {
        this.about.clear();
        this.about.addAll(about);
        Timber.d("-------- SIZE IS %d NOW ---------", this.about.size());
        notifyDataSetChanged();
    }

    public void setListener(AboutClickListener listener) {
        this.listener = listener;
    }

    class AboutHolder extends RecyclerView.ViewHolder {
        private CardAboutItemBinding binding;

        AboutHolder(CardAboutItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(v -> onClick());
        }

        private void onClick() {
            int position = getAdapterPosition();
            if (listener != null) listener.onAboutClicked(about.get(position));
        }

        void bind(AboutField about) {
            binding.setAbout(about);
            binding.executePendingBindings();
        }
    }

    public interface AboutClickListener {
        void onAboutClicked(AboutField about);
    }
}

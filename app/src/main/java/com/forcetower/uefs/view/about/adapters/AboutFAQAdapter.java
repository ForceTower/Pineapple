package com.forcetower.uefs.view.about.adapters;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ItemAboutFaqItemBinding;
import com.forcetower.uefs.db_service.entity.QuestionAnswer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 18/04/2018.
 */
public class AboutFAQAdapter extends RecyclerView.Adapter<AboutFAQAdapter.FAQItemHolder> {
    private final List<QuestionAnswer> items;

    public AboutFAQAdapter(List<QuestionAnswer> items) {
        this.items = new ArrayList<>();
        setItems(items);
    }

    public void setItems(List<QuestionAnswer> items) {
        this.items.clear();
        for (QuestionAnswer qa : items) if (qa.isActive()) this.items.add(qa);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FAQItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       ItemAboutFaqItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_about_faq_item, parent, false);
        return new FAQItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQItemHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class FAQItemHolder extends RecyclerView.ViewHolder {
        ItemAboutFaqItemBinding binding;
        FAQItemHolder(ItemAboutFaqItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(QuestionAnswer question) {
            binding.setFaq(question);
            binding.executePendingBindings();
        }
    }
}

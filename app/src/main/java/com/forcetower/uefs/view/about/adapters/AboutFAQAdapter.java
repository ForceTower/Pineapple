package com.forcetower.uefs.view.about.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.QuestionAnswer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FAQItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_about_faq_item, parent, false);
        return new FAQItemHolder(view);
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
        @BindView(R.id.tv_question)
        TextView tvQuestion;
        @BindView(R.id.tv_answer)
        TextView tvAnswer;

        FAQItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(QuestionAnswer question) {
            tvQuestion.setText(question.getQuestion());
            tvAnswer.setText(question.getAnswer());
        }
    }
}

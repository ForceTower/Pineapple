package com.forcetower.uefs.view.about.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db_service.entity.CreditsMention;
import com.forcetower.uefs.db_service.helper.CreditAndMentions;
import com.forcetower.uefs.view.about.CreditClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.MentionHolder> {
    private final List<CreditAndMentions> mentions;
    private final RecyclerView.RecycledViewPool pool;
    private CreditClickListener onMentionClickListener;

    public CreditsAdapter() {
        this.pool = new RecyclerView.RecycledViewPool();
        this.mentions = new ArrayList<>();
    }

    public void setMentions(List<CreditAndMentions> mentions) {
        if (mentions == null) return;

        this.mentions.clear();
        this.mentions.addAll(mentions);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MentionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_credit_mention, parent, false);
        MentionHolder holder = new MentionHolder(view);
        holder.rvMentions.setRecycledViewPool(pool);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MentionHolder holder, int position) {
        holder.bind(mentions.get(position));
    }

    @Override
    public int getItemCount() {
        return mentions.size();
    }

    public void setOnMentionClickListener(CreditClickListener onMentionClickListener) {
        this.onMentionClickListener = onMentionClickListener;
    }

    class MentionHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_category_name)
        TextView tvCategoryName;
        @BindView(R.id.rv_mentions)
        RecyclerView rvMentions;

        private MentionsAdapter adapter;

        MentionHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            adapter = new MentionsAdapter(new ArrayList<>());
            adapter.setOnMentionClickListener(onMentionClickListener);
            rvMentions.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            rvMentions.setAdapter(adapter);
        }

        public void bind(CreditAndMentions mention) {
            tvCategoryName.setText(mention.getCategory());
            adapter.setParticipants(mention.getParticipants());
        }
    }
}

package com.forcetower.uefs.view.about.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db_service.entity.Mention;
import com.forcetower.uefs.view.about.CreditClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.forcetower.uefs.util.WordUtils.validString;

public class MentionsAdapter extends RecyclerView.Adapter<MentionsAdapter.MentionHolder> {
    private final List<Mention> participants;
    private CreditClickListener onMentionClickListener;

    public MentionsAdapter(ArrayList<Mention> participants) {
        this.participants = new ArrayList<>();
        setParticipants(participants);
    }

    @NonNull
    @Override
    public MentionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_credit_mention_participant, parent, false);
        return new MentionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MentionHolder holder, int position) {
        holder.bind(participants.get(position));
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public void setParticipants(List<Mention> participants) {
        this.participants.clear();
        this.participants.addAll(participants);
        notifyDataSetChanged();
    }

    public void setOnMentionClickListener(CreditClickListener onMentionClickListener) {
        this.onMentionClickListener = onMentionClickListener;
    }

    class MentionHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_link)
        TextView tvLink;

        MentionHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(view -> onClicked());
        }

        public void bind(Mention mention) {
            tvName.setText(mention.getName());
            if (validString(mention.getLink()))
                tvLink.setText(mention.getLink());
            else
                tvLink.setVisibility(View.GONE);
        }

        private void onClicked() {
            if (onMentionClickListener != null) {
                int position = getAdapterPosition();
                onMentionClickListener.onMentionClicked(participants.get(position));
            }
        }
    }
}

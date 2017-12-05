package com.forcetower.uefs.adapters.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.sagres_sdk.domain.SagresAccess;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by João Paulo on 29/11/2017.
 */

public class NavigationDrawerAdapter extends RecyclerView.Adapter {
    private List<NavDrawerItem> items;
    private final int HEADER = 1, ITEM = 2;
    private final int normalColor, normalBackground, checkedColor, checkedBackground;
    private OnNavDrawerClickListener clickListener;
    private DrawerCallback callback;
    private Context context;

    public NavigationDrawerAdapter(Context context, DrawerCallback callback, List<NavDrawerItem> items) {
        normalColor       = ContextCompat.getColor(context, R.color.primaryText);
        normalBackground  = R.drawable.selectable_background;
        checkedColor      = ContextCompat.getColor(context, R.color.colorPrimary);
        checkedBackground = R.color.nav_drawer_selected_bg;
        this.callback = callback;
        this.context = context;
        setItems(items);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_drawer_header, parent, false);
            return new HeaderHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_drawer_list_item, parent, false);
            return new ItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == HEADER)
            onBindHeaderHolder((HeaderHolder)holder);
        else
            onBindItemHolder((ItemHolder)holder, position);
    }

    private void onBindHeaderHolder(HeaderHolder holder) {
        if (SagresProfile.getCurrentProfile() != null) {
            SagresProfile profile = SagresProfile.getCurrentProfile();
            SagresAccess access = SagresAccess.getCurrentAccess();
            holder.titleTextView.setVisibility(View.VISIBLE);
            holder.titleTextView.setText(profile.getStudentName());
            holder.profileImageView.setVisibility(View.VISIBLE);
            holder.profileImageView.setImageResource(R.drawable.logo_uefs);
            holder.backgroundImageView.setVisibility(View.VISIBLE);
            holder.backgroundImageView.setImageResource(R.drawable.capa);
            holder.subtitleTextView.setVisibility(View.VISIBLE);

            boolean showScore = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_score", false);
            if (!showScore)
                holder.subtitleTextView.setText(access.getUsername());
            else
                holder.subtitleTextView.setText(context.getString(R.string.score_is, profile.getScore()));
        }
    }

    private void onBindItemHolder(ItemHolder holder, int position) {
        NavDrawerItem item = getItem(position);

        holder.title.setText(item.getTitle());

        boolean selected = callback.getSelectedPosition() == getCorrectPosition(position);
        holder.title.setTextColor(selected ? checkedColor : normalColor);
        holder.itemView.setBackgroundResource(selected ? checkedBackground : normalBackground);

        if (item.getIcon() > 0) {
            holder.icon.setImageResource(item.getIcon());
            holder.icon.setColorFilter(selected ? checkedColor : normalColor, PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<NavDrawerItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setClickListener(OnNavDrawerClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isHeader()) {
            return HEADER;
        }
        return ITEM;
    }

    public NavDrawerItem getItem(int position) {
        if (position < 0 || position >= items.size())
            return null;
        return items.get(position);
    }

    public int getCorrectPosition(int position) {
        return position - 1;
    }

    private class HeaderHolder extends RecyclerView.ViewHolder {
        ImageView backgroundImageView;
        CircleImageView profileImageView;
        TextView titleTextView;
        TextView subtitleTextView;

        public HeaderHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            backgroundImageView = itemView.findViewById(R.id.bg_image_view);
            profileImageView = itemView.findViewById(R.id.profile_image_view);
            subtitleTextView = itemView.findViewById(R.id.subtitle_text_view);
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icon;
        TextView title;

        public ItemHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(android.R.id.icon);
            title = itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                int position = getAdapterPosition();
                NavDrawerItem item = getItem(position);
                clickListener.onDrawerItemClicked(view, this, item, position);
            }
        }
    }

    public interface OnNavDrawerClickListener {
        void onDrawerItemClicked(View view, ItemHolder vh, NavDrawerItem item, int position);
    }

    public interface NavDrawerItemClickListener {
        void onClick();
    }

    public interface DrawerCallback {
        int getSelectedPosition();
    }
}

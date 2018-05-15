package com.forcetower.uefs.view.connected.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.CalendarItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 02/12/2017.
 */
public class CalendarAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<CalendarAdapter.ItemHolder>{
    private Context context;
    private List<CalendarItem> items;

    public CalendarAdapter(Context context, List<CalendarItem> calendar) {
        this.context = context;
        this.items = new ArrayList<>();
        setCalendar(calendar);
    }

    public void setCalendar(List<CalendarItem> calendar) {
        this.items.clear();
        this.items.addAll(calendar);
        notifyDataSetChanged();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(context).inflate(R.layout.item_calendar, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        CalendarItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ItemHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_event)
        TextView tvEvent;

        ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(CalendarItem item) {
            tvDate.setText(item.getDay());
            tvEvent.setText(item.getMessage());
        }
    }
}
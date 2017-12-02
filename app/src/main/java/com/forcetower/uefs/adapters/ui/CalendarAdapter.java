package com.forcetower.uefs.adapters.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.sagres_sdk.domain.SagresCalendarItem;

import java.util.List;

/**
 * Created by João Paulo on 02/12/2017.
 */
public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<SagresCalendarItem> items;

    public CalendarAdapter(Context context, List<SagresCalendarItem> calendar) {
        this.context = context;
        setCalendar(calendar);
    }

    public void setCalendar(List<SagresCalendarItem> calendar) {
        this.items = calendar;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(context).inflate(R.layout.calendar_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SagresCalendarItem item = items.get(position);
        ItemHolder itemHolder = (ItemHolder)holder;

        itemHolder.tv_date.setText(item.getDay());
        itemHolder.tv_event.setText(item.getMessage());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView tv_date;
        TextView tv_event;

        ItemHolder(View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_event = itemView.findViewById(R.id.tv_event);
        }
    }
}

package com.sp.smarttaskmanagerv2;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> eventList;
    private final OnItemClickListener listener;
    private Set<Long> selectedEvents = new HashSet<>();

    public EventAdapter(List<Event> eventList, OnItemClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.tvTitle.setText(event.getTitle());

        // Format Date
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.getDefault());
        String formattedDate = sdf.format(new Date(event.getStartTime()));

        holder.tvDate.setText(formattedDate);

        // Highlight selected events
        holder.itemView.setBackgroundColor(selectedEvents.contains(event.getId()) ?
                ContextCompat.getColor(holder.itemView.getContext(), R.color.selected_event_background) :
                Color.TRANSPARENT);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (selectedEvents.contains(event.getId())) {
                selectedEvents.remove(event.getId());
            } else {
                selectedEvents.add(event.getId());
            }
            notifyItemChanged(position);
            listener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public Set<Long> getSelectedEvents() {
        return selectedEvents;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDate = itemView.findViewById(R.id.tvEventDate);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
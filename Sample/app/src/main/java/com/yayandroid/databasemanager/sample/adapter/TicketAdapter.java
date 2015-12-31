package com.yayandroid.databasemanager.sample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yayandroid.databasemanager.sample.R;
import com.yayandroid.databasemanager.sample.model.Ticket;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Yahya Bayramoglu on 31/12/15.
 */
public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    private ArrayList<Ticket> tickets;
    private LayoutInflater inflater;
    private TicketClickListener listener;
    private boolean isReadOnly = false;

    public interface TicketClickListener {
        void ticketClick(Ticket ticket);

        void deleteClick(int position);
    }

    public TicketAdapter(Context context, TicketClickListener listener) {
        this.tickets = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void addAll(Collection<? extends Ticket> list) {
        tickets.addAll(list);
        notifyDataSetChanged();
    }

    public void add(Ticket ticket) {
        tickets.add(ticket);
        notifyItemInserted(tickets.size() - 1);
    }

    public Ticket remove(int position) {
        Ticket removedTicket = tickets.remove(position);
        notifyItemRemoved(position);
        return removedTicket;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.recycler_item_ticket, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.ticketText.setText(tickets.get(position).getId());
        if (isReadOnly) {
            holder.deleteText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (tickets == null)
            return 0;
        return tickets.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView ticketText, deleteText;

        public ViewHolder(View itemView) {
            super(itemView);
            this.ticketText = (TextView) itemView.findViewById(R.id.ticketText);
            this.deleteText = (TextView) itemView.findViewById(R.id.deleteText);

            ticketText.setOnClickListener(this);
            deleteText.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                switch (v.getId()) {
                    case R.id.deleteText: {
                        listener.deleteClick(position);
                        break;
                    }
                    case R.id.ticketText: {
                        listener.ticketClick(tickets.get(position));
                        break;
                    }
                }
            }
        }
    }

}
package com.yayandroid.databasemanager.sample.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yayandroid.databasemanager.listener.QueryListener;
import com.yayandroid.databasemanager.model.Query;
import com.yayandroid.databasemanager.sample.Constants;
import com.yayandroid.databasemanager.sample.R;
import com.yayandroid.databasemanager.sample.model.Ticket;
import com.yayandroid.databasemanager.sample.adapter.TicketAdapter;

import java.util.ArrayList;

/**
 * Created by Yahya Bayramoglu on 30/12/15.
 */
public class DBListActivity extends BaseActivity {

    private TicketAdapter adapter;
    private String databaseTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dblist);

        adapter = new TicketAdapter(this, adapterListener);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            databaseTag = extras.getString(Constants.INTENT_KEY_TAG);

            if (extras.containsKey(Constants.INTENT_KEY_LIST)) {
                ArrayList<Ticket> list = extras.getParcelableArrayList(Constants.INTENT_KEY_LIST);
                if (list != null) {
                    adapter.setReadOnly(true);
                    adapter.addAll(list);
                }
            }
        }

        if (!TextUtils.isEmpty(databaseTag)) {
            adapter.setReadOnly(getDatabaseManager().isReadOnly(databaseTag));
            getTickets();
        }

        if (adapter.isReadOnly()) {
            Button addButton = (Button) findViewById(R.id.addButton);
            addButton.setText(getString(R.string.read_only));
            addButton.setEnabled(false);
        }
    }

    private void getTickets() {
        displayProgress();
        getDatabaseManager().select(new Query(databaseTag)
                .set(Constants.SELECT_TICKETS)
                .setListener(ticketsListener));
    }

    public void addClick(View v) {
        displayProgress();

        Ticket newTicket = Ticket.createRandom(adapter.getItemCount() + 1);
        adapter.add(newTicket);

        Query insertQuery = new Query(databaseTag)
                .insert(Constants.TABLE_TICKET, 2) // .set(Constants.INSERT_TICKET);
                .withArgs(newTicket.getId(), newTicket.getDate())
                .setListener(new QueryListener() {
                    @Override
                    public void onComplete(Query query) {
                        dismissProgress();
                    }
                });

        getDatabaseManager().insert(insertQuery);
    }

    private void removeTicket(int position) {
        dismissProgress();

        Ticket removedTicket = adapter.remove(position);

        Query deleteQuery = new Query(databaseTag)
                .set(Constants.DELETE_TICKET)
                .withArgs(removedTicket.getId())
                .setListener(new QueryListener() {
                    @Override
                    public void onComplete(Query query) {
                        dismissProgress();
                    }
                });

        getDatabaseManager().delete(deleteQuery);
    }

    private TicketAdapter.TicketClickListener adapterListener = new TicketAdapter.TicketClickListener() {
        @Override
        public void ticketClick(Ticket ticket) {
            Toast.makeText(getApplicationContext(), ticket.getDate(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void deleteClick(int position) {
            removeTicket(position);
        }
    };

    private QueryListener ticketsListener = new QueryListener<Ticket>(true) {

        @Override
        public boolean manualInjection(Ticket object, Cursor cursor) {
            if (getUseReflection()) {
                return false;
            } else {
                object.setId(cursor.getString(0));
                object.setDate(cursor.getString(1));
                return true;
            }
        }

        @Override
        public void onListReceived(ArrayList<Ticket> result) {
            adapter.addAll(result);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void noDataFound() {
            Toast.makeText(getApplicationContext(), R.string.toast_no_ticket, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(Query query) {
            dismissProgress();
        }
    };

}
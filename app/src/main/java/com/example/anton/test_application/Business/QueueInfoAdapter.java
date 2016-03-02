package com.example.anton.test_application.Business;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anton.test_application.R;

import java.util.List;

/**
 * Created by L on 3/2/2016.
 */
public class QueueInfoAdapter extends RecyclerView.Adapter<QueueInfoAdapter.ViewHolder> {
    private List<QueueInfo> qInfo;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        CardView cv;
        TextView title;
        TextView number;

        ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_view);
            title = (TextView)itemView.findViewById(R.id.txtTitle);
            number = (TextView)itemView.findViewById(R.id.txtNoOfCustomers);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public QueueInfoAdapter(List<QueueInfo> queue) {
        this.qInfo = queue;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.queuedashboard_activity_cardview, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.title.setText(qInfo.get(position).title);
        holder.number.setText(qInfo.get(position).noOfCustomers+"");


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return qInfo.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
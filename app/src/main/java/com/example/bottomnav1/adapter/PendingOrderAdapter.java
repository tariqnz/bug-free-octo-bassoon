package com.example.bottomnav1.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomnav1.R;
import com.example.bottomnav1.MyDatabase;
import com.example.bottomnav1.databaseTables.PendingOrdersList;
import com.example.bottomnav1.fragments.OrderDetFragment;
import com.example.bottomnav1.fragments.OrderListFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class PendingOrderAdapter extends RecyclerView.Adapter<PendingOrderAdapter.PendingViewHolder> {
    Context mContext;
    List<PendingOrdersList> mpendingOrdersLists;
    public PendingOrderAdapter(Context context, List<PendingOrdersList> pendingOrdersLists){
        mContext = context;
        mpendingOrdersLists = pendingOrdersLists;
    }
    @NonNull
    @Override
    public PendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.pending_orders_list_items,parent,false);
        return new PendingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingViewHolder holder, int position) {
        holder.cName.setText(mpendingOrdersLists.get(position).getName());
        holder.cFName.setText(mpendingOrdersLists.get(position).getFather_name());
        holder.cMobile.setText(mpendingOrdersLists.get(position).getMobile());
        holder.status.setText(mpendingOrdersLists.get(position).getStatus());
        holder.remaining.setText(String.valueOf(mpendingOrdersLists.get(position).getRemaining()));
    }

    @Override
    public int getItemCount() {return mpendingOrdersLists != null ? mpendingOrdersLists.size() : 0;}
    public void filterList(ArrayList<PendingOrdersList> filteredList){
        mpendingOrdersLists = filteredList;
        notifyDataSetChanged();
    }
    public class PendingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView cName;
        private final TextView cFName;
        private final TextView cMobile;
        private final TextView status;
        private final TextView remaining;

        public PendingViewHolder(@NonNull View itemView) {
            super(itemView);
            cName = (TextView) itemView.findViewById(R.id.customerName);
            cFName = (TextView) itemView.findViewById(R.id.customerFatherp);
            cMobile = (TextView) itemView.findViewById(R.id.mobileText);
            status = (TextView) itemView.findViewById(R.id.orderStatus);
            remaining = (TextView) itemView.findViewById(R.id.remainingText);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @OptIn(markerClass = UnstableApi.class)
        @Override
        public void onClick(View view) {
            int currentPosition = getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) {
                Timber.tag("AdapterClick").w("Clicked at NO_POSITION");
                return;
            }

            // Assuming mOrderList or mpendingOrdersLists holds your data
            // For OrderAdapter:
            // OrderList clickedOrder = mOrderList.get(currentPosition);        // For PendingOrderAdapter:
            // PendingOrdersList clickedOrder = mpendingOrdersLists.get(currentPosition);

            // --- IMPORTANT: Log the ID being sent ---
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            OrderDetFragment orderDetFragment = new OrderDetFragment();
            Bundle bundle = new Bundle();

            if (mpendingOrdersLists != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                PendingOrdersList pendingOrder = mpendingOrdersLists.get(getAdapterPosition());
                // Using "orderId" as the key for clarity.
                // The value comes from getId(), which is assumed to return the order ID.
                bundle.putLong("orderId", pendingOrder.getId());
                orderDetFragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, orderDetFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public final void updateData(List<PendingOrdersList> newPendingOrderList) {
        this.mpendingOrdersLists = new ArrayList<>(newPendingOrderList != null ? newPendingOrderList : Collections.emptyList());
        notifyDataSetChanged();
    }
}

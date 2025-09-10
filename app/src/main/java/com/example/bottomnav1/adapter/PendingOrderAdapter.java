package com.example.bottomnav1.adapter;

import static com.example.bottomnav1.fragments.AddCustomerFragment.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomnav1.MyDatabase;
import com.example.bottomnav1.R;
import com.example.bottomnav1.databaseTables.OrderList;
import com.example.bottomnav1.databaseTables.PendingOrdersList;
import com.example.bottomnav1.databaseTables.TopCustomers;
import com.example.bottomnav1.fragments.CustomerDetFragment;
import com.example.bottomnav1.fragments.OrderDetFragment;
import com.example.bottomnav1.fragments.OrderFragment;
import com.example.bottomnav1.fragments.OrderListFragment;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        //PendingViewHolder pendingViewHolder = new PendingViewHolder(v);
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
    public int getItemCount() {return mpendingOrdersLists != null ? mpendingOrdersLists.size() : 0;}  // Make sure this is correct
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
        private final ImageButton popupBtn;
        public PendingViewHolder(@NonNull View itemView) {
            super(itemView);
            cName = (TextView) itemView.findViewById(R.id.customerName);
            cFName = (TextView) itemView.findViewById(R.id.customerFatherp);
            cMobile = (TextView) itemView.findViewById(R.id.mobileText);
            status = (TextView) itemView.findViewById(R.id.orderStatus);
            remaining = (TextView) itemView.findViewById(R.id.remainingText);
            popupBtn = (ImageButton) itemView.findViewById(R.id.order_det_btn);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            popupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(mContext, popupBtn);
                    popupMenu.getMenuInflater().inflate(R.menu.order_popup_menu, popupMenu.getMenu());
                    final AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int mitem = menuItem.getItemId();
                            if (mitem == R.id.viewDet) {
                                OrderDetFragment orderDetFragment = new OrderDetFragment();
                                Bundle bundle = new Bundle();
                                bundle.putLong("orderId", mpendingOrdersLists.get(getAdapterPosition()).getId());
                                orderDetFragment.setArguments(bundle);
                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, orderDetFragment).addToBackStack(null).commit();
                                return true;
                            } else if (mitem == R.id.updateStatus) {
                                PopupMenu popupMenu1 = new PopupMenu(mContext, popupBtn);
                                popupMenu1.getMenuInflater().inflate(R.menu.status_popup_menu, popupMenu1.getMenu());
                                popupMenu1.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        int mitem1 = menuItem.getItemId();
                                        if (mitem1 == R.id.ready) {
                                            String r = "Ready";
                                            MyDatabase.INSTANCE.orderDao().updateStatus(mpendingOrdersLists.get(getAdapterPosition()).getId(), r);
                                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrderListFragment()).addToBackStack(null).commit();
                                            return true;
                                        } else if (mitem1 == R.id.delivered) {
                                            String d = "Delivered";
                                            MyDatabase.INSTANCE.orderDao().updateStatus(mpendingOrdersLists.get(getAdapterPosition()).getId(), d);
                                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrderListFragment()).addToBackStack(null).commit();
                                            return true;
                                        } else if (mitem1 == R.id.cancel) {
                                            String c = "Cancel";
                                            MyDatabase.INSTANCE.orderDao().updateStatus(mpendingOrdersLists.get(getAdapterPosition()).getId(), c);
                                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrderListFragment()).addToBackStack(null).commit();
                                            return true;
                                        } else if (mitem1 == R.id.in_progress) {
                                            String ip = "In Progress";
                                            MyDatabase.INSTANCE.orderDao().updateStatus(mpendingOrdersLists.get(getAdapterPosition()).getId(), ip);
                                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrderListFragment()).addToBackStack(null).commit();
                                            return true;
                                        }
                                        return false;
                                    }
                                });
                            }
                            return false;
                        }
                    });
                }
            });
        }

        @OptIn(markerClass = UnstableApi.class)
        @Override
        public void onClick(View view) {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            OrderDetFragment orderDetFragment = new OrderDetFragment();
            Bundle bundle = new Bundle();
            // Ensure mTopCustomerList is not null and position is valid
            if (mpendingOrdersLists != null && getAdapterPosition() != RecyclerView.NO_POSITION) {

                int currentPosition = getAdapterPosition(); // Get it once
                PendingOrdersList pendingOrdersList = mpendingOrdersLists.get(currentPosition);
                long pendingId = pendingOrdersList.getId();

                bundle.putLong("orderId", mpendingOrdersLists.get(getAdapterPosition()).getId());
                //customerDetFragment.setArguments(bundle);
                //activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, customerDetFragment).addToBackStack(null).commit();
                orderDetFragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, orderDetFragment)
                        .addToBackStack(null)
                        .commit();
                Log.d(TAG, "PendingOrderAdapter orderId: " + mpendingOrdersLists.get(getAdapterPosition()).getId());
            }
        }
    }
    // Optional: Method to update data in the adapter
    @SuppressLint("NotifyDataSetChanged")
    public final void updateData(List<PendingOrdersList> newPendingOrderList) {
        this.mpendingOrdersLists = new ArrayList<>(newPendingOrderList != null ? newPendingOrderList : Collections.emptyList());
        notifyDataSetChanged();
    }
}

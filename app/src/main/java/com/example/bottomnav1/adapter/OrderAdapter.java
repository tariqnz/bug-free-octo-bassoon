package com.example.bottomnav1.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log; // Import Log
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast; // Import Toast

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomnav1.MyDatabase;
import com.example.bottomnav1.R;
import com.example.bottomnav1.databaseTables.OrderList;
import com.example.bottomnav1.fragments.OrderDetFragment;
import com.example.bottomnav1.fragments.OrderListFragment;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{
    Context mContext;
    List<OrderList> mOrderList;
    private static final String TAG = "OrderAdapter"; // For logging

    public OrderAdapter(@NonNull Context context, List<OrderList> orders) {
        mContext = context;
        mOrderList = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.order_items_list,parent,false);
        //OrderViewHolder orderViewHolder = new OrderViewHolder(v);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        if (mOrderList == null || position < 0 || position >= mOrderList.size()) {
            Timber.tag(TAG).e("Invalid position or mOrderList is null/empty at position: %s", position);
            return;
        }
        OrderList currentOrder = mOrderList.get(position);
        if (currentOrder == null) {
            Timber.tag(TAG).e("OrderList object is null at position: %s", position);
            return;
        }
        //int orderId = Math.toIntExact(morderList.get(position).getId());
        holder.cName.setText(mOrderList.get(position).getName());
        holder.cFName.setText(mOrderList.get(position).getFather_name());
        holder.cMobile.setText(mOrderList.get(position).getMobile());
        holder.status.setText(mOrderList.get(position).getStatus());
        holder.remaining.setText(String.valueOf(mOrderList.get(position).getRemaining()));
    }

    @Override
    public int getItemCount() {return mOrderList != null ? mOrderList.size() : 0;}
    public void filterList(ArrayList<OrderList> filteredList){
        mOrderList = filteredList;
        notifyDataSetChanged();
    }
    // This method is correctly defined for removing item from the list and notifying adapter
    public void removeItem(int position) {
        if (mOrderList != null && position >= 0 && position < mOrderList.size()) {
            mOrderList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mOrderList.size()); // Important to update subsequent items
        } else {
            Timber.tag(TAG).w("Attempted to remove item at invalid position: %s", position);
        }
    }
    /*public void removeItem(int position) {
        if (mOrderList != null && position >= 0 && position < mOrderList.size()) {
            mOrderList.remove(position);
            notifyItemRemoved(position);
        }
    }*/
    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView cName;
        private final TextView cFName;
        private final TextView cMobile;
        private final TextView status;
        private final TextView remaining;
        private final ImageButton popupBtn;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            cName = (TextView) itemView.findViewById(R.id.customerName);
            cFName = (TextView) itemView.findViewById(R.id.customerFather);
            cMobile = (TextView) itemView.findViewById(R.id.mobileText);
            status = (TextView) itemView.findViewById(R.id.orderStatus);
            remaining = (TextView) itemView.findViewById(R.id.remainingText);
            popupBtn = (ImageButton) itemView.findViewById(R.id.orderlist_det_btn);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            popupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mContext == null) {
                        Timber.tag(TAG).e("mContext is null in popupBtn.onClick, cannot show PopupMenu.");
                        Toast.makeText(view.getContext(), "Error: Cannot show menu", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    PopupMenu popupMenu = new PopupMenu(mContext,popupBtn);
                    popupMenu.getMenuInflater().inflate(R.menu.order_popup_menu,popupMenu.getMenu());
                    final AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int mitem = menuItem.getItemId();
                            final int currentPosition = getAdapterPosition(); // Get position once

                            if (currentPosition == RecyclerView.NO_POSITION) {
                                Timber.tag(TAG).e("Invalid adapter position (NO_POSITION). Cannot proceed.");
                                Toast.makeText(mContext != null ? mContext : activity, "Error: Please try again.", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            if (mOrderList == null || currentPosition >= mOrderList.size() || mOrderList.get(currentPosition) == null) {
                                Timber.tag(TAG).e("mOrderList is null, or position is out of bounds, or order at position is null.");
                                Toast.makeText(mContext != null ? mContext : activity, "Error: Data inconsistency.", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            if(mitem == R.id.viewDet){
                                OrderDetFragment orderDetFragment = new OrderDetFragment();
                                Bundle bundle = new Bundle();
                                bundle.putLong("id",mOrderList.get(getAdapterPosition()).getId());
                                orderDetFragment.setArguments(bundle);
                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,orderDetFragment).addToBackStack(null).commit();
                                return true;
                            }else if(mitem == R.id.updateStatus){
                                PopupMenu popupMenu1 = new PopupMenu(mContext,popupBtn);
                                popupMenu1.getMenuInflater().inflate(R.menu.status_popup_menu,popupMenu1.getMenu());
                                popupMenu1.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        int mitem1 = menuItem.getItemId();
                                        if(mitem1 == R.id.ready){
                                            String r ="Ready";
                                            MyDatabase.INSTANCE.orderDao().updateStatus(mOrderList.get(getAdapterPosition()).getId(),r);
                                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new OrderListFragment()).addToBackStack(null).commit();
                                            return true;
                                        } else if (mitem1==R.id.delivered) {
                                            String d ="Delivered";
                                            MyDatabase.INSTANCE.orderDao().updateStatus(mOrderList.get(getAdapterPosition()).getId(),d);
                                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new OrderListFragment()).addToBackStack(null).commit();
                                            return true;
                                        } else if (mitem1==R.id.cancel) {
                                            String c = "Cancel";
                                            MyDatabase.INSTANCE.orderDao().updateStatus(mOrderList.get(getAdapterPosition()).getId(),c);
                                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new OrderListFragment()).addToBackStack(null).commit();
                                            return true;
                                        } else if (mitem1==R.id.in_progress) {
                                            String ip = "In Progress";
                                            MyDatabase.INSTANCE.orderDao().updateStatus(mOrderList.get(getAdapterPosition()).getId(),ip);
                                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new OrderListFragment()).addToBackStack(null).commit();
                                            return true;
                                        }
                                        return false;
                                    }
                                });
                                popupMenu1.show();
                                return true;
                            }else if(mitem == R.id.delete){
                                // --- Show AlertDialog for delete confirmation ---
                                new AlertDialog.Builder(activity) // Use activity context for dialog
                                        .setTitle("Confirm Delete")
                                        .setMessage("Do you want to delete this order?")
                                        .setIcon(android.R.drawable.ic_menu_delete) // Common delete icon
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // User clicked "Yes", proceed with delete
                                                if (MyDatabase.INSTANCE != null && MyDatabase.INSTANCE.orderDao() != null) {
                                                    OrderList orderToDelete = mOrderList.get(currentPosition);
                                                    MyDatabase.INSTANCE.orderDao().deleteOrder(orderToDelete.getId());

                                                    // Option 1: Remove from current list and notify adapter (Recommended)
                                                    removeItem(currentPosition); // Use your existing removeItem method
                                                    Toast.makeText(activity, "Order deleted", Toast.LENGTH_SHORT).show();

                                                    // Option 2: Reload the OrderListFragment (Simpler but full reload)
                                                    // This will re-fetch data from the database.
                                                    // Toast.makeText(activity, "Order deleted. Refreshing list...", Toast.LENGTH_SHORT).show();
                                                    // activity.getSupportFragmentManager().beginTransaction()
                                                    //         .replace(R.id.fragment_container, new OrderListFragment())
                                                    //         .commit();

                                                } else {
                                                    Timber.tag(TAG).e("Database or OrderDao is null, cannot delete order.");
                                                    Toast.makeText(activity, "Error: Could not delete order.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // User clicked "No", just dismiss the dialog
                                                dialog.dismiss();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert) // Optional: add an icon
                                        .show();
                                return true; // Indicate that the menu item click was handled
                                /*int position = getAdapterPosition();
                                if (.position != RecyclerView.NO_POSITION) {
                                    MyDatabase.INSTANCE.orderDao().deleteOrder(mOrderList.get(position).getId());
                                    removeItem(position);
                                    mOrderList.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
                                }
                                return true;*/
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
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
            if (mOrderList != null && getAdapterPosition() != RecyclerView.NO_POSITION) {

                int currentPosition = getAdapterPosition(); // Get it once
                OrderList orderList = mOrderList.get(currentPosition);
                long orderId = orderList.getId();

                bundle.putLong("id", mOrderList.get(getAdapterPosition()).getId());
                orderDetFragment.setArguments(bundle);

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, orderDetFragment)
                        .addToBackStack(null)
                        .commit();
                //int morderId = Math.toIntExact(mOrderList.get(getAdapterPosition()).getId());
                Timber.tag(TAG).d("orderAdapter id %s", mOrderList.get(getAdapterPosition()).getId());
            }
        }
    }
    // Optional: Method to update data in the adapter
    @SuppressLint("NotifyDataSetChanged")
    public final void updateData(List<OrderList> newOrderList) {
        this.mOrderList = new ArrayList<>(newOrderList != null ? newOrderList : Collections.emptyList());
        notifyDataSetChanged();
        //mOrderList = newOrderList;
        //notifyDataSetChanged(); // Or use DiffUtil for better performance
    }
}

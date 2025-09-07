package com.example.bottomnav1;

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
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomnav1.databaseTables.MyDatabase;
import com.example.bottomnav1.databaseTables.OrderList;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{
    Context mContext;
    List<OrderList> mOrderList;
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

    public void removeItem(int position) {
        if (mOrderList != null && position >= 0 && position < mOrderList.size()) {
            mOrderList.remove(position);
            notifyItemRemoved(position);
        }
    }
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
                    PopupMenu popupMenu = new PopupMenu(mContext,popupBtn);
                    popupMenu.getMenuInflater().inflate(R.menu.order_popup_menu,popupMenu.getMenu());
                    final AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int mitem = menuItem.getItemId();
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
                                int position = getAdapterPosition();
                                if (position != RecyclerView.NO_POSITION) {
                                    MyDatabase.INSTANCE.orderDao().deleteOrder(mOrderList.get(position).getId());
                                    removeItem(position);
                                }
                                return true;
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
                //customerDetFragment.setArguments(bundle);
                //activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, customerDetFragment).addToBackStack(null).commit();
                orderDetFragment.setArguments(bundle);

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, orderDetFragment)
                        .addToBackStack(null)
                        .commit();
                //int orderId = Math.toIntExact(mOrderList.get(getAdapterPosition()).getId());
                //Log.d(TAG, "onCreate: currentCustomerId received: from cDF " + orderId);
            }
            //AppCompatActivity activity = (AppCompatActivity) view.getContext();
            //Bundle bundle = new Bundle();
            //bundle.putLong("id",mOrderList.get(getAdapterPosition()).getId());
            //OrderDetFragment orderDetFragment = new OrderDetFragment();
            //orderDetFragment.setArguments(bundle);
            //activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, orderDetFragment).addToBackStack(null).commit();
        }
    }
    // Optional: Method to update data in the adapter
    @SuppressLint("NotifyDataSetChanged")
    public final void updateData(List<OrderList> newOrderList) {
        mOrderList = newOrderList;
        notifyDataSetChanged(); // Or use DiffUtil for better performance
    }
}

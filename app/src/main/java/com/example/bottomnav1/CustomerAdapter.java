package com.example.bottomnav1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomnav1.databaseTables.Customer;
import com.example.bottomnav1.databaseTables.Measurement;
import com.example.bottomnav1.databaseTables.MyDatabase;
import com.example.bottomnav1.databaseTables.Order;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    List<Customer> mCustomerList;
    Context mContext; // This might be null

    public CustomerAdapter(Context context, List<Customer> customerList) {
        this.mContext = context; // If 'context' passed in is null, mContext will be null
        this.mCustomerList = customerList;
    }


    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v= LayoutInflater.from(mContext).inflate(R.layout.customer_list_items,parent,false);
        //return new CustomerViewHolder(v);
        return new CustomerViewHolder(v, "0");
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onBindViewHolder(@NonNull final CustomerViewHolder holder, int position) {
        Customer currentCustomer = mCustomerList.get(position);
        holder.CName.setText(currentCustomer.getName());
        holder.CFname.setText(currentCustomer.getFather_name());
        holder.CMobile.setText(currentCustomer.getMobile());

        // Correctly fetch all orders for the customer and sum the remaining amounts
        List<Order> orders = MyDatabase.INSTANCE.orderDao().getOrdersByCustomerId(currentCustomer.getId());
        int totalRemaining = 0;
        if (orders != null && !orders.isEmpty()) {
            for (Order order : orders) {
                totalRemaining += order.getRemaining();
            }
        }
        holder.remaining_amount.setText(String.valueOf(totalRemaining));
    }

    @Override
    public int getItemCount() {
        return mCustomerList == null ? 0 : mCustomerList.size();
        //return mCustomer.size();
    }

    public void filterList(ArrayList<Customer> filteredList){
        mCustomerList = filteredList;
        notifyDataSetChanged();
    }
    public class CustomerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView CName;
        private final TextView CFname;
        //private TextView CAddress;
        private final TextView CMobile;
        private final TextView remaining_amount;
        private final ImageButton popupBt;

        public CustomerViewHolder(@NonNull View itemView, String remainingAmount) {
            super(itemView);
            CName = (TextView) itemView.findViewById(R.id.customerName);
            CFname = (TextView) itemView.findViewById(R.id.customerFather);
            //Caddress = (TextView) itemView.findViewById(R.id.customerAddress);
            CMobile = (TextView) itemView.findViewById(R.id.mobileText);
            popupBt = (ImageButton) itemView.findViewById(R.id.custoer_det_btn);
            remaining_amount = (TextView) itemView.findViewById(R.id.remaining_amountTxt);
            //int remaining_amount = MyDatabase.getInstance(PendingOrdersList).get
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            popupBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext,popupBt);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
                    final AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @OptIn(markerClass = UnstableApi.class)
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int ItemId = item.getItemId();

                            int currentPosition = getAdapterPosition(); // Get it once
                            //Log.d("PopupMenuClick", "Menu Item Clicked: " + item.getTitle() + ", ItemID: " + ItemId);
                            //Log.d("PopupMenuClick", "Adapter Position: " + currentPosition);
                            /*if (currentPosition == RecyclerView.NO_POSITION) {
                                Log.e("PopupMenuClick", "Invalid adapter position. Cannot proceed.");
                                Toast.makeText(mContext, "Error: Please try again.", Toast.LENGTH_SHORT).show();
                                return false;
                            }*/

                            if(ItemId == R.id.viewdet){
                                //Log.d("PopupMenuClick", "SUCCESS: Entering viewDet block!");
                                //Log.d("PopupMenuClick", "View Details selected for position: " + currentPosition);
                                Customer customer = mCustomerList.get(currentPosition);
                                long customerId = customer.getId();
                                //Log.d("PopupMenuClick", "Passing Customer ID to CustomerDetFragment: " + customerId);


                                /*CustomerDetFragment customerDetFragment = new CustomerDetFragment();
                                Bundle bundle = new Bundle();
                                bundle.putLong("id", customerId);
                                customerDetFragment.setArguments(bundle);

                                AppCompatActivity activity = (AppCompatActivity) v.getContext(); // Assuming v is the view from popupBt.onClick
                                activity.getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container,customerDetFragment)
                                        .addToBackStack("Customers")
                                        .commit();*/
                                CustomerDetFragment customerDetFragment = new CustomerDetFragment();
                                Bundle bundle = new Bundle();
                                bundle.putLong("id",customerId);
                                customerDetFragment.setArguments(bundle);
                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,customerDetFragment).addToBackStack("Customers").commit();

                                return true;
                            }else if(ItemId == R.id.takeOrder){
                                NewOrderFragment newOrderFragment = new NewOrderFragment();
                                Bundle bundle = new Bundle();
                                bundle.putLong("id",mCustomerList.get(getAdapterPosition()).getId());
                                newOrderFragment.setArguments(bundle);
                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,newOrderFragment).addToBackStack(null).commit();
                                return true;
                            } else if (ItemId == R.id.delete) {
                                MyDatabase.INSTANCE.customerDao().deleteCustomer(mCustomerList.get(getAdapterPosition()).getId());
                                //mCustomer.remove(getAdapterPosition());
                                //notifyItemRemoved(getAdapterPosition());
                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CustomerFragment()).addToBackStack(null).commit();
                                return true;
                            }else {
                                return false;
                            }
                        }
                    });
                    popupMenu.show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            Bundle bundle = new Bundle();
            bundle.putLong("id",mCustomerList.get(getAdapterPosition()).getId());
            if(currentFragment instanceof CustomerFragment){
                CustomerDetFragment customerDetFragment = new CustomerDetFragment();
                customerDetFragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,customerDetFragment).addToBackStack("Customers").commit();
            }else {
                Measurement measurement = MyDatabase.INSTANCE.measurementDao().getCustomerMeasurements(mCustomerList.get(getAdapterPosition()).getId());
                if(measurement==null){
                    Toast.makeText(mContext,"First Enter The Measurements",Toast.LENGTH_SHORT).show();
                    MeasurementFragment measurementFragment = new MeasurementFragment();
                    measurementFragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,measurementFragment).addToBackStack("newOrder").commit();
                }else {
                    NewOrderFragment newOrderFragment = new NewOrderFragment();
                    newOrderFragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,newOrderFragment).addToBackStack(null).commit();
                }
            }
        }
    }

}

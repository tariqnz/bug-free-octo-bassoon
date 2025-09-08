package com.example.bottomnav1.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bottomnav1.MyDatabase;
import com.example.bottomnav1.R;
import com.example.bottomnav1.adapter.OrderAdapter;
import com.example.bottomnav1.databaseTables.OrderList;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrderAdapter mAdapter;
    Button orderDet;
    EditText searchBar;
    private TextView emptyViewText;
    public boolean mViewCreated = false;

    public OrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        mViewCreated = true;

        requireActivity().setTitle(R.string.Orders);

        // Initialize Views
        searchBar = view.findViewById(R.id.search_bar_orders);
        recyclerView = view.findViewById(R.id.list);
        emptyViewText = view.findViewById(R.id.empty_view_text);
        FloatingActionButton fab = view.findViewById(R.id.selectCustomerFab);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new OrderAdapter(requireContext(), new ArrayList<>()); // Start with an empty list
        recyclerView.setAdapter(mAdapter);

        // Setup Listeners
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        fab.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new OrderFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void filter(String text) {
        if (getContext() == null) return;
        ArrayList<OrderList> filteredList = new ArrayList<>();
        // It's better to get the full list once and then filter, rather than hitting DB on every search
        List<OrderList> allOrders = MyDatabase.getInstance(getContext()).orderDao().getOrders();
        for (OrderList order : allOrders) {
            if (order.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(order);
            }
        }
        mAdapter.filterList(filteredList);
    }

    private void loadOrders() {
        if (getContext() == null || mAdapter == null) {
            return; // Avoid context-related crashes or uninitialized adapter
        }

        List<OrderList> orders = MyDatabase.INSTANCE.orderDao().getOrders();
        mAdapter.updateData(orders); // Use the adapter's method to update the data

        if (orders == null || orders.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyViewText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyViewText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // This is called after onCreateView and ensures the view hierarchy is created.
        loadOrders(); // Load initial data here

        // Padding logic remains the same
        BottomAppBar bottomAppBar = requireActivity().findViewById(R.id.bottomAppBar);
        if (bottomAppBar != null) {
            int bottomAppBarHeight = bottomAppBar.getHeight();
            view.setPadding(0, 0, 0, bottomAppBarHeight);
        }
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the data every time the fragment comes into view
        loadOrders();
    }
}

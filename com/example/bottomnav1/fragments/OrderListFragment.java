package com.example.bottomnav1.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottomnav1.MyDatabase;
import com.example.bottomnav1.R;
import com.example.bottomnav1.adapter.OrderAdapter;
import com.example.bottomnav1.databaseTables.OrderList;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderListFragment extends Fragment implements OrderAdapter.OnOrderDeletedListener {

    private RecyclerView recyclerView;
    private OrderAdapter mAdapter;
    private EditText searchBar;
    private TextView emptyViewText;

    // The master list that holds the single source of truth for all orders from the DB.
    private final List<OrderList> masterOrderList = new ArrayList<>();

    public OrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        requireActivity().setTitle(R.string.Orders);

        // Initialize Views
        searchBar = view.findViewById(R.id.search_bar_orders);
        recyclerView = view.findViewById(R.id.list);
        emptyViewText = view.findViewById(R.id.empty_view_text);
        FloatingActionButton fab = view.findViewById(R.id.selectCustomerFab);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new OrderAdapter(requireContext(), new ArrayList<>());
        mAdapter.setOnOrderDeletedListener(this); // Set the fragment as the listener for deletions
        recyclerView.setAdapter(mAdapter);

        // Setup Listeners
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                // When text changes, apply the filter to the master list
                applyFilter();
            }
        });

        fab.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new OrderFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Adjust padding for bottom navigation bar
        BottomAppBar bottomAppBar = requireActivity().findViewById(R.id.bottomAppBar);
        if (bottomAppBar != null) {
            ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(0, 0, 0, systemBars.bottom);
                return insets;
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Every time the fragment is resumed, reload data from the database to ensure it's fresh.
        loadOrders();
    }

    /**
     * This is the only method that should fetch data from the database.
     * It populates the master list and then triggers a filter application to update the UI.
     */
    private void loadOrders() {
        if (getContext() == null) return;
        masterOrderList.clear();
        masterOrderList.addAll(MyDatabase.INSTANCE.orderDao().getOrders());
        applyFilter(); // After loading, apply the current filter to refresh the displayed list
    }

    /**
     * This method filters the in-memory masterOrderList based on the search query
     * and updates the adapter with the result. It does NOT query the database.
     */
    private void applyFilter() {
        if (mAdapter == null || masterOrderList == null) return;

        String query = searchBar.getText().toString().toLowerCase().trim();
        ArrayList<OrderList> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredList.addAll(masterOrderList);
        } else {
            for (OrderList order : masterOrderList) {
                // Add any other fields you want to search by here
                if (order.getName().toLowerCase().contains(query) ||
                    order.getFather_name().toLowerCase().contains(query) ||
                    order.getMobile().contains(query)) {
                    filteredList.add(order);
                }
            }
        }
        mAdapter.updateData(filteredList);

        // Update the visibility of the "empty" message based on the adapter's item count
        if (mAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyViewText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyViewText.setVisibility(View.GONE);
        }
    }

    /**
     * This is the callback method from the OrderAdapter. It's called after an order
     * has been successfully deleted from the database.
     * @param order The order that was deleted.
     */
    @Override
    public void onOrderDeleted(OrderList order) {
        // Remove the deleted order from the master list.
        // Using an iterator is safer for removal to avoid ConcurrentModificationException,
        // though a simple for loop with break would also work here.
        for (int i = 0; i < masterOrderList.size(); i++) {
            if (Objects.equals(masterOrderList.get(i).getId(), order.getId())) {
                masterOrderList.remove(i);
                break;
            }
        }
        // After updating the master list, re-apply the filter to update the UI.
        applyFilter();
    }
}

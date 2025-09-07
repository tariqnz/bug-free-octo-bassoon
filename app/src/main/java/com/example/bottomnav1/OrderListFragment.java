package com.example.bottomnav1;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.example.bottomnav1.databaseTables.MyDatabase;
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
        searchBar = view.findViewById(R.id.search_bar_orders);


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.selectCustomerFab);
        // ... (your existing fab OnClickListener code) ...

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        emptyViewText = view.findViewById(R.id.empty_view_text); // Initialize the Tex

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
            private void filter(String text) {
                ArrayList<OrderList> filteredList = new ArrayList<>();
                for (OrderList order : MyDatabase.getInstance(getContext()).orderDao().getOrders()) {
                    if (order.getName().toLowerCase().contains(text.toLowerCase())) {
                        filteredList.add(order);
                    }
                }
                mAdapter.filterList(filteredList);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrderFragment()).addToBackStack("").commit();
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),RecyclerView.VERTICAL));
        mAdapter = new OrderAdapter(requireContext(), MyDatabase.getInstance(getContext()).orderDao().getOrders());
        //recyclerView.setAdapter(mAdapter);
        List<OrderList> orders = MyDatabase.INSTANCE.orderDao().getOrders();
        if (orders == null || orders.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyViewText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setAdapter(mAdapter);
            recyclerView.setVisibility(View.VISIBLE);
            emptyViewText.setVisibility(View.GONE);
        }
        return view;
    }
    // its make view up from bottom nav
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomAppBar bottomAppBar = requireActivity().findViewById(R.id.bottomAppBar);
        if (bottomAppBar != null) {
            int bottomAppBarHeight = bottomAppBar.getHeight();
            view.setPadding(0, 0, 0, bottomAppBarHeight);
        }
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int bottomInset = insets.getSystemGestureInsets().bottom;
            v.setPadding(0, 0, 0, bottomInset);
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, systemBars.bottom); // Only bottom padding
            return WindowInsetsCompat.CONSUMED;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            List<OrderList> orders = MyDatabase.INSTANCE.orderDao().getOrders();
            mAdapter.updateData(orders);

            if (orders == null || orders.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyViewText.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyViewText.setVisibility(View.GONE);
            }
        }
    }
}

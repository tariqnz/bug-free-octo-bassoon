// INSTRUCTIONS:
// 1. You will need to add your package declaration at the top of this file.
//    e.g., package com.example.yourapp;
// 2. You will also need to ensure all necessary imports are included for your project.
// 3. Replace the entire content of your existing NewOrderFragment.java with the code from this file.

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

// NOTE: You must import your own project's classes, for example:
// import com.your.package.R;
// import com.your.package.database.MyDatabase;
// import com.your.package.database.Order;
// import com.your.package.ui.OrderListFragment;

public class Corrected_NewOrderFragment extends Fragment {
    private Spinner mCollerTypeSpinner, mSidePocketSpinner;
    private boolean mPetHasChanged = false;

    private EditText quantity, price, total, advance, remaining;
    private CheckBox frontPocket, doubleStich, shalwarPocket;
    private int mCollar, mPocket;
    private RadioGroup bottom;
    private EditText collectionDateEditText; // Renamed for clarity
    private long collectionDateTimestamp; // Renamed for clarity

    public Corrected_NewOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_new_order, container, false);
        assert getArguments() != null;
        final long customerId = getArguments().getLong("id");
        final long orderId = getArguments().getLong("orderId");

        // Initialize Views
        initializeViews(view);

        // Setup Listeners
        mCollerTypeSpinner.setOnTouchListener(mTouchListener);
        mSidePocketSpinner.setOnTouchListener(mTouchListener);
        setupTextWatchers();

        Button pickDate = view.findViewById(R.id.picDateBtn);
        pickDate.setOnClickListener(v -> showDatePicker());

        Button confirm = view.findViewById(R.id.confirmOrderBtn);
        confirm.setOnClickListener(v -> saveOrder(orderId, customerId));

        // Load data for new or existing order
        if (orderId == 0) {
            setupNewOrder();
        } else {
            setupEditOrder(orderId);
        }

        return view;
    }

    private void initializeViews(View view) {
        mCollerTypeSpinner = view.findViewById(R.id.spinner_collarType);
        mSidePocketSpinner = view.findViewById(R.id.spinner_sidePocket);
        quantity = view.findViewById(R.id.quantityEditText);
        price = view.findViewById(R.id.priceEditText);
        total = view.findViewById(R.id.totalEditText);
        advance = view.findViewById(R.id.advanceEditText);
        remaining = view.findViewById(R.id.remainingEditText);
        frontPocket = view.findViewById(R.id.frontPocketCheckBox);
        bottom = view.findViewById(R.id.bottmTypeRadioBtn);
        doubleStich = view.findViewById(R.id.doubleStichCheck);
        shalwarPocket = view.findViewById(R.id.shalwarPocketCheck);
        collectionDateEditText = view.findViewById(R.id.collectionDated);

        CollerTypeSpinner();
        SidePocketSpinner();
    }

    private void setupNewOrder() {
        requireActivity().setTitle(R.string.new_order);
        final Calendar c = Calendar.getInstance();
        collectionDateTimestamp = c.getTimeInMillis();
        SimpleDateFormat defaultSdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        collectionDateEditText.setText(defaultSdf.format(c.getTime()));
    }

    private void setupEditOrder(long orderId) {
        requireActivity().setTitle(R.string.edit_order);
        Order order = MyDatabase.INSTANCE.orderDao().getOrderById(orderId);
        if (order == null) {
            Toast.makeText(getContext(), "Error: Could not load order details.", Toast.LENGTH_SHORT).show();
            if (getFragmentManager() != null) getFragmentManager().popBackStack();
            return;
        }
        quantity.setText(String.valueOf(order.getQuantity()));
        price.setText(String.valueOf(order.getPrice()));
        total.setText(String.valueOf(order.getTotal()));
        advance.setText(String.valueOf(order.getAdvance()));
        remaining.setText(String.valueOf(order.getRemaining()));

        collectionDateTimestamp = order.getCollectionDate();
        String collectDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(collectionDateTimestamp);
        collectionDateEditText.setText(collectDate);

        frontPocket.setChecked(order.isFrontPocket());
        mCollar = order.getCollar();
        mCollerTypeSpinner.setSelection(mCollar);
        mPocket = order.getSidePocket();
        mSidePocketSpinner.setSelection(mPocket);
        bottom.check(order.IsBottom());
        doubleStich.setChecked(order.isDoubleStich());
        shalwarPocket.setChecked(order.isShalwarPocket());
    }

    private void saveOrder(long orderId, long customerId) {
        String qtyStr = quantity.getText().toString();
        String prcStr = price.getText().toString();
        if (TextUtils.isEmpty(qtyStr) || TextUtils.isEmpty(prcStr)) {
            Toast.makeText(getContext(), "Please fill quantity and price.", Toast.LENGTH_SHORT).show();
            return;
        }

        Order order;
        if (orderId == 0) {
            order = new Order();
            // Set the receivedDate to the current moment for new orders
            order.setDated(Calendar.getInstance().getTimeInMillis());
        } else {
            order = MyDatabase.INSTANCE.orderDao().getOrderById(orderId);
            if (order == null) {
                Toast.makeText(getContext(), "Error: Order not found. Cannot update.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        order.setCustomerId(customerId);
        order.setQuantity(Integer.parseInt(qtyStr));
        order.setPrice(Integer.parseInt(prcStr));
        order.setTotal(Integer.parseInt(total.getText().toString()));
        order.setAdvance(TextUtils.isEmpty(advance.getText().toString()) ? 0 : Integer.parseInt(advance.getText().toString()));
        order.setRemaining(Integer.parseInt(remaining.getText().toString()));
        order.setCollar(mCollar);
        order.setFrontPocket(frontPocket.isChecked());
        order.setSidePocket(mPocket);
        order.setBottom(bottom.getCheckedRadioButtonId());
        order.setDoubleStich(doubleStich.isChecked());
        order.setShalwarPocket(shalwarPocket.isChecked());
        order.setStatus(requireContext().getResources().getString(R.string.in_progress));
        // Set the collectionDate from our timestamp variable
        order.setCollectionDate(collectionDateTimestamp);

        MyDatabase.INSTANCE.orderDao().newOrder(order);
        Toast.makeText(getActivity(), (orderId == 0) ? "New Order added" : "Order updated", Toast.LENGTH_SHORT).show();
        showPrintDialog();
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            collectionDateTimestamp = calendar.getTimeInMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            collectionDateEditText.setText(sdf.format(calendar.getTime()));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { calculateTotals(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        quantity.addTextChangedListener(textWatcher);
        price.addTextChangedListener(textWatcher);
        advance.addTextChangedListener(textWatcher);
    }

    private void calculateTotals() {
        int q = 0, p = 0, a = 0;
        try {
            if (!TextUtils.isEmpty(quantity.getText().toString())) q = Integer.parseInt(quantity.getText().toString());
            if (!TextUtils.isEmpty(price.getText().toString())) p = Integer.parseInt(price.getText().toString());
            if (!TextUtils.isEmpty(advance.getText().toString())) a = Integer.parseInt(advance.getText().toString());
        } catch (NumberFormatException e) {
            // Handle cases where user enters non-numeric input if necessary
        }
        int t = q * p;
        int r = t - a;
        total.setText(String.valueOf(t));
        remaining.setText(String.valueOf(r));
    }

    private void showPrintDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle("Print Invoice")
            .setMessage("Do you want to print invoice?")
            .setPositiveButton("Yes", (dialog, which) -> {
                Toast.makeText(getContext(), "Printing invoice...", Toast.LENGTH_SHORT).show();
                navigateToOrderList();
            })
            .setNegativeButton("No", (dialog, which) -> navigateToOrderList())
            .setIcon(R.drawable.ic_action_print)
            .show();
    }

    private void navigateToOrderList() {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OrderListFragment())
                .addToBackStack(null)
                .commit();
        }
    }

    private void SidePocketSpinner() {
        ArrayAdapter sidePocketAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.array_side_pocket_options, android.R.layout.simple_spinner_item);
        sidePocketAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSidePocketSpinner.setAdapter(sidePocketAdapter);
        mSidePocketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { mPocket = position; }
            @Override public void onNothingSelected(AdapterView<?> parent) { mPocket = 0; }
        });
    }

    private void CollerTypeSpinner() {
        ArrayAdapter collarTypeAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.array_collar_type_options, android.R.layout.simple_spinner_item);
        collarTypeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mCollerTypeSpinner.setAdapter(collarTypeAdapter);
        mCollerTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { mCollar = position; }
            @Override public void onNothingSelected(AdapterView<?> parent) { mCollar = 0; }
        });
    }

    private final View.OnTouchListener mTouchListener = (v, event) -> {
        mPetHasChanged = true;
        return false;
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), systemBars.bottom);
            return insets;
        });
    }
}

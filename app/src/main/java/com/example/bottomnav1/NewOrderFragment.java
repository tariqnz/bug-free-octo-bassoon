package com.example.bottomnav1;

import android.app.AlertDialog;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.bottomnav1.databaseTables.MyDatabase;
import com.example.bottomnav1.databaseTables.Order;
import com.google.android.material.bottomappbar.BottomAppBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewOrderFragment extends Fragment {
    private Spinner mCollerTypeSpinner, mSidePocketSpinner;
    private boolean mPetHasChanged = false;

    private EditText quantity, price, total, advance, remaining;
    private CheckBox frontPocket, doubleStich, shalwarPocket;
    private int mCollar, mPocket;
    private RadioGroup bottom;
    private EditText dated;
    private Button pickDate;
    private DatePickerDialog datePickerDialog;
    private long collectionDated;

    public NewOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_new_order, container, false);
        assert getArguments() != null;
        final long customerId = getArguments().getLong("id");
        final Bundle bundle = getArguments();
        final long orderId = bundle.getLong("orderId");

        mCollerTypeSpinner = (Spinner) view.findViewById(R.id.spinner_collarType);
        mSidePocketSpinner = (Spinner) view.findViewById(R.id.spinner_sidePocket);

        mCollerTypeSpinner.setOnTouchListener(mTouchListener);
        mSidePocketSpinner.setOnTouchListener(mTouchListener);

        CollerTypeSpinner();
        SidePocketSpinner();

        quantity = view.findViewById(R.id.quantityEditText);
        price = view.findViewById(R.id.priceEditText);
        total = view.findViewById(R.id.totalEditText);
        advance = view.findViewById(R.id.advanceEditText);
        remaining = view.findViewById(R.id.remainingEditText);

        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        advance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateRemaining();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        frontPocket = view.findViewById(R.id.frontPocketCheckBox);
        bottom = view.findViewById(R.id.bottmTypeRadioBtn);
        doubleStich = view.findViewById(R.id.doubleStichCheck);
        shalwarPocket = view.findViewById(R.id.shalwarPocketCheck);
        dated = (EditText) view.findViewById(R.id.collectionDated);
        pickDate = (Button) view.findViewById(R.id.picDateBtn);

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int currentYear = c.get(Calendar.YEAR);
                int currentMonth = c.get(Calendar.MONTH);
                int currentDay = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        collectionDated = calendar.getTimeInMillis();

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dated.setText(sdf.format(calendar.getTime()));
                    }
                }, currentYear, currentMonth, currentDay);
                datePickerDialog.show();
            }
        });

        Button confirm = view.findViewById(R.id.confirmOrderBtn);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrder(orderId, customerId);
            }
        });

        if (orderId == 0) {
            requireActivity().setTitle(R.string.new_order);
            // Set current date as default collection date
            final Calendar c = Calendar.getInstance();
            collectionDated = c.getTimeInMillis();
            // Format it for display
            SimpleDateFormat defaultSdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dated.setText(defaultSdf.format(c.getTime()));
        } else {
            requireActivity().setTitle(R.string.edit_order);
            Order order = MyDatabase.INSTANCE.orderDao().getOrderById(orderId);
            if (order == null) {
                Toast.makeText(getContext(), "Error: Could not load order details.", Toast.LENGTH_SHORT).show();
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
                return view;
            }

            quantity.setText(String.valueOf(order.getQuantity()));
            price.setText(String.valueOf(order.getPrice()));
            total.setText(String.valueOf(order.getTotal()));
            advance.setText(String.valueOf(order.getAdvance()));
            remaining.setText(String.valueOf(order.getRemaining()));

            collectionDated = order.getCollectionDate();
            String collectDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(collectionDated);
            dated.setText(collectDate);
            frontPocket.setChecked(order.isFrontPocket());
            mCollar = order.getCollar();
            mCollerTypeSpinner.setSelection(mCollar);
            mPocket = order.getSidePocket();
            mSidePocketSpinner.setSelection(mPocket);
            bottom.check(order.IsBottom());
            doubleStich.setChecked(order.isDoubleStich());
            shalwarPocket.setChecked(order.isShalwarPocket());
        }

        return view;
    }

    private void saveOrder(long orderId, long customerId) {
        String qtyStr = quantity.getText().toString();
        String prcStr = price.getText().toString();
        String totalStr = total.getText().toString();
        String advStr = advance.getText().toString();
        String remStr = remaining.getText().toString();

        if (TextUtils.isEmpty(qtyStr) || TextUtils.isEmpty(prcStr) || TextUtils.isEmpty(totalStr) || TextUtils.isEmpty(remStr)) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean fPocket = frontPocket.isChecked();
        int botmType = bottom.getCheckedRadioButtonId();
        boolean doubleSt = doubleStich.isChecked();
        boolean shalwarP = shalwarPocket.isChecked();

        // Create a new Order object to hold the current data from the UI.
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setQuantity(Integer.parseInt(qtyStr));
        order.setPrice(Integer.parseInt(prcStr));
        order.setTotal(Integer.parseInt(totalStr));
        order.setAdvance(TextUtils.isEmpty(advStr) ? 0 : Integer.parseInt(advStr));
        order.setRemaining(Integer.parseInt(remStr));
        order.setCollar(mCollar);
        order.setFrontPocket(fPocket);
        order.setSidePocket(mPocket);
        order.setBottom(botmType);
        order.setDoubleStich(doubleSt);
        order.setShalwarPocket(shalwarP);
        order.setStatus(requireContext().getResources().getString(R.string.in_progress));
        order.setCollectionDate(collectionDated);

        if (orderId == 0) {
            // This is a new order. Set its creation date and insert it.
            order.setDated(Calendar.getInstance().getTimeInMillis());
            MyDatabase.INSTANCE.orderDao().insertOrder(order);
            Toast.makeText(getActivity(), "New Order added successfully", Toast.LENGTH_SHORT).show();
        } else {
            // This is an existing order. We need to fetch the original order
            // to preserve its creation date, then set the ID and update it.
            Order existingOrder = MyDatabase.INSTANCE.orderDao().getOrderById(orderId);
            if (existingOrder == null) {
                Toast.makeText(getContext(), "Error: Order not found. Cannot update.", Toast.LENGTH_SHORT).show();
                return;
            }
            order.setId(orderId);
            order.setDated(existingOrder.getDated()); // Preserve the original creation date.
            MyDatabase.INSTANCE.orderDao().updateOrder(order);
            Toast.makeText(getActivity(), "Order updated successfully", Toast.LENGTH_SHORT).show();
        }

        showPrintDialog();
    }

    private void showPrintDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Print Invoice")
                .setMessage("Do you want to print invoice?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Printing invoice...", Toast.LENGTH_SHORT).show();
                        navigateToOrderList();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        navigateToOrderList();
                    }
                })
                .setIcon(R.drawable.ic_action_print)
                .show();
    }

    private void navigateToOrderList() {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new OrderListFragment())
                    .addToBackStack(null) // Use null or a specific name
                    .commit();
        }
    }


    private void calculateTotal() {
        String prStr = price.getText().toString();
        String qtStr = quantity.getText().toString();

        if (TextUtils.isEmpty(prStr) || TextUtils.isEmpty(qtStr)) {
            total.setText("0");
        } else {
            try {
                int q = Integer.parseInt(qtStr);
                int p = Integer.parseInt(prStr);
                String tot = String.valueOf(p * q);
                total.setText(tot);
            } catch (NumberFormatException e) {
                total.setText("0");
            }
        }
        calculateRemaining();
    }

    private void calculateRemaining() {
        String totalAmtStr = total.getText().toString();
        String advStr = advance.getText().toString();

        if (TextUtils.isEmpty(totalAmtStr)) {
            remaining.setText("");
            return;
        }

        try {
            int totalA = Integer.parseInt(totalAmtStr);
            int advanceA = TextUtils.isEmpty(advStr) ? 0 : Integer.parseInt(advStr);
            remaining.setText(String.valueOf(totalA - advanceA));
        } catch (NumberFormatException e) {
            remaining.setText(totalAmtStr);
        }
    }

    protected void SidePocketSpinner() {
        ArrayAdapter sidePocketAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.array_side_pocket_options, android.R.layout.simple_spinner_item);
        sidePocketAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSidePocketSpinner.setAdapter(sidePocketAdapter);
        mSidePocketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPocket = position; // Assuming position corresponds to the desired integer value
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mPocket = 0;
            }
        });
    }

    private void CollerTypeSpinner() {
        ArrayAdapter collarTypeAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.array_collar_type_options, android.R.layout.simple_spinner_item);
        collarTypeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mCollerTypeSpinner.setAdapter(collarTypeAdapter);
        mCollerTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCollar = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCollar = 0;
            }
        });
    }

    private final View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetHasChanged = true;
            return false;
        }
    };
    //to up from the bottom nav
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
}

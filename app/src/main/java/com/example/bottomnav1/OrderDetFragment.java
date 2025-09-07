package com.example.bottomnav1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.media3.common.util.UnstableApi;

import com.example.bottomnav1.databaseTables.Customer;
import com.example.bottomnav1.databaseTables.Measurement;
import com.example.bottomnav1.databaseTables.MyDatabase;
import com.example.bottomnav1.databaseTables.Order;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import timber.log.Timber;

import static android.content.ContentValues.TAG;


public class OrderDetFragment extends Fragment {
    TextView contact,address,quantity, price,total,advance,remaining,collorType,frontPocket,
            sidePocket,bottomType,doubleStich,shalwarPocket,kameezL,armL,teeraL,neck,chest,waist
            ,ghera,shoulder,shalwarL,paincha,cafSize,orderDate,collectionDate;
    Long orderId,customerId;
    Button mditOrder;
    private PrintJob currentPrintJob;
    private String titleToSet = null;

    public OrderDetFragment() {
        // Required empty public constructor
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_det, container, false);

        setHasOptionsMenu(true);
        assert getArguments() != null;
        orderId = getArguments().getLong("id");
        Log.d(TAG, "OrderDetFragment showing orDerId " + orderId);

        contact = (TextView) view.findViewById(R.id.customerContactE);
        address = (TextView) view.findViewById(R.id.customerAddressE);
        quantity = (TextView) view.findViewById(R.id.quantity);
        price = (TextView) view.findViewById(R.id.price);
        total = (TextView) view.findViewById(R.id.totalAmount);
        advance = (TextView) view.findViewById(R.id.advance);
        remaining = (TextView) view.findViewById(R.id.recievable);
        orderDate = (TextView) view.findViewById(R.id.orderDated);
        collectionDate = (TextView) view.findViewById(R.id.collectionDated);
        collorType = (TextView) view.findViewById(R.id.collorType);
        frontPocket = (TextView) view.findViewById(R.id.frontPocket);
        sidePocket = (TextView) view.findViewById(R.id.sidePockets);
        bottomType = (TextView) view.findViewById(R.id.bottomType);
        doubleStich = (TextView) view.findViewById(R.id.doubleStich);
        shalwarPocket = (TextView) view.findViewById(R.id.shalwarPocket);
        kameezL = (TextView) view.findViewById(R.id.kameezl);
        armL = (TextView) view.findViewById(R.id.armL);
        teeraL = (TextView) view.findViewById(R.id.teeraL);
        neck = (TextView) view.findViewById(R.id.neckL);
        chest = (TextView) view.findViewById(R.id.chestL);
        waist = (TextView) view.findViewById(R.id.waistL);
        ghera = (TextView) view.findViewById(R.id.gheraL);
        shoulder = (TextView) view.findViewById(R.id.shoulderL);
        shalwarL = (TextView) view.findViewById(R.id.shalwarL);
        paincha = (TextView) view.findViewById(R.id.painchaL);
        cafSize = (TextView) view.findViewById(R.id.cafL);


        Order order = MyDatabase.INSTANCE.orderDao().getOrderById(orderId);

        // Check if the order exists. If not, it may have been deleted.
        if (order == null) {
            Toast.makeText(getContext(), "Order not found. It may have been deleted.", Toast.LENGTH_LONG).show();
            // Go back to the previous screen
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
            // Return an empty view to prevent the fragment from continuing to load
            return new View(getContext());
        }

        // Now it's safe to use the order object
        Log.e("OrderDetFragment", " orderID: " + order.getId());

        quantity.setText(String.valueOf(order.getQuantity()));

        price.setText(String.valueOf(order.getPrice()));
        total.setText(String.valueOf(order.getTotal()));
        advance.setText(String.valueOf(order.getAdvance()));
        remaining.setText(String.valueOf(order.getRemaining()));
        String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(order.getDated());
        String colDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(order.getCollectionDate());
        orderDate.setText(currentDate);
        collectionDate.setText(colDate);
        if(order.getCollar() == 0){
            collorType.setText(R.string.collar); //"Collar"
        }else if(order.getCollar() == 1){
            collorType.setText(R.string.circle_ban);  //"Gole Ban"
        }else if(order.getCollar() == 2){
            collorType.setText(R.string.choras_ban);  //"Chores Ban"
        }if(order.isFrontPocket()){
            frontPocket.setText("Yes");
        }else {
            frontPocket.setText("No");
        }
        if(order.getSidePocket()==0){
            sidePocket.setText("Non");
        } else if (order.getSidePocket()==1) {
            sidePocket.setText("Single");
        } else if (order.getSidePocket()==2) {
            sidePocket.setText("Double Pocket");
        }
        if(order.getBottom()==R.id.radioCircle){
            bottomType.setText("Circular");
        }else {
            bottomType.setText("Square");
        }
        if(order.isDoubleStich()){
            doubleStich.setText("Yes");
        }else {
            doubleStich.setText("No");
        }
        if(order.isShalwarPocket()){
            shalwarPocket.setText("Yes");
        }else {
            shalwarPocket.setText("No");
        }
        customerId = order.getCustomerId();
        Customer customer = MyDatabase.INSTANCE.customerDao().getCustomer(customerId);
        requireActivity().setTitle(customer.getName());

        contact.setText(customer.getMobile());
        address.setText(customer.getAddress());
        Measurement measurement = MyDatabase.INSTANCE.measurementDao().getCustomerMeasurements(customerId);
        kameezL.setText(String.valueOf(measurement.getKameezLength()));
        armL.setText(String.valueOf(measurement.getArmLength()));
        teeraL.setText(String.valueOf(measurement.getTeera()));
        neck.setText(String.valueOf(measurement.getNeck()));
        chest.setText(String.valueOf(measurement.getChest()));
        waist.setText(String.valueOf(measurement.getWaist()));
        ghera.setText(String.valueOf(measurement.getGhera()));
        shoulder.setText(String.valueOf(measurement.getShoulder()));
        shalwarL.setText(String.valueOf(measurement.getShalwarLenght()));
        paincha.setText(String.valueOf(measurement.getPaincha()));
        cafSize.setText(String.valueOf(measurement.getCafSize()));
        //return inflater.inflate(R.layout.fragment_order_det, container, false);
        return view;
    }
    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int mItem = item.getItemId();
        if(mItem==R.id.edit_order_btn) {
            NewOrderFragment newOrderFragment = new NewOrderFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("id", customerId);
            bundle.putLong("orderId", orderId);
            newOrderFragment.setArguments(bundle);
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, newOrderFragment).addToBackStack(null).commit();
        }
        return super.onOptionsItemSelected(item);
    }*/

    /*@Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_order_menu,menu);
        //super.onCreateOptionsMenu(menu, inflater);
        return;
    }*/
    //extra menu with phone and printer
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.measurement_det_menu, menu); // Inflate your menu
        return;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.edit_order_btn) {
            NewOrderFragment newOrderFragment = new NewOrderFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("id", customerId);
            bundle.putLong("orderId", orderId);
            newOrderFragment.setArguments(bundle);
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, newOrderFragment).addToBackStack(null).commit();
            return true;
        }else if (id == R.id.action_call_customer) {
            // Handle the phone icon click
            // For example, get the customer's phone number and initiate a call
            Toast.makeText(getContext(), "Call clicked", Toast.LENGTH_SHORT).show();

            Customer customer = MyDatabase.INSTANCE.customerDao().getCustomer(customerId);
            String phoneNumber = customer.getMobile();
            makePhoneCall(phoneNumber); // Implement this method
            return true;
        }else if (id == R.id.action_print_recept) {
            // TODO: Handle call logic
            Toast.makeText(getContext(), "Print clicked", Toast.LENGTH_SHORT).show(); // "Call clicked" was a typo here

            /*// 1. Declare and 2. Generate/Populate invoiceText
            //String invoiceText = generateInvoiceText(); // Call a new method to create the invoice string
            String invoiceText = generateInvoicePdfn();
            if (invoiceText == null || invoiceText.isEmpty()) {
                Toast.makeText(getContext(), "Could not generate invoice data.", Toast.LENGTH_LONG).show();
                return true; // Or handle error appropriately
            }*/

            String pdfPath = generateInvoicePdfFile();
            if (pdfPath == null) {
                Toast.makeText(getContext(), "Could not generate PDF invoice.", Toast.LENGTH_LONG).show();
                return true;
            }
            // --- Printing Logic ---
            /*Intent intent = new Intent(getContext(), InvoiceFragment.class);
            intent.putExtra("invoiceText", invoiceText); // Pass the formatted invoice string
            startActivity(intent);*/
            // OPTION B: If InvoiceFragment is meant to replace the current fragment in the existing Activity
            // This is more common for Fragment-to-Fragment navigation.
            InvoiceFragment invoiceFragment = new InvoiceFragment();
            Bundle bundle = new Bundle();
            //bundle.putString("invoiceText", invoiceText);
            bundle.putString("invoiceText",pdfPath);
            invoiceFragment.setArguments(bundle);

            //sending the invoice to the printer or save as pdf
            /*
            PrintManager printManager = (PrintManager) requireActivity().getSystemService(Context.PRINT_SERVICE);
            PrintDocumentAdapter adapter = new InvoicePrintAdapter(this, invoiceText);
            assert printManager != null;
            printManager.print("Invoice", adapter, new PrintAttributes.Builder().build());
            */

            /*//String invoiceText = generateInvoiceText();
            String jobName = "MyDocumentPrintJob"; // Or generate a dynamic name
            TextPrintAdapter adapter = new TextPrintAdapter(requireActivity(), invoiceText,jobName);
            PrintManager printManager = (PrintManager) requireActivity().getSystemService(Context.PRINT_SERVICE);
            printManager.print("Invoice", adapter, new PrintAttributes.Builder().build());*/

            PrintManager printManager = (PrintManager) requireActivity().getSystemService(Context.PRINT_SERVICE);
            PrintDocumentAdapter adapter = new PdfDocumentAdapter(requireContext(), pdfPath);
            printManager.print("Invoice", adapter, new PrintAttributes.Builder().build());

            currentPrintJob = printManager.print("Invoice", adapter, new PrintAttributes.Builder().build());
            new Handler().postDelayed(() -> {
                if (currentPrintJob != null && getActivity() != null) {
                    if (currentPrintJob.isCompleted()) {
                        Toast.makeText(getActivity(), "Invoice printed successfully!", Toast.LENGTH_SHORT).show();
                    } else if (currentPrintJob.isFailed()) {
                        Toast.makeText(getActivity(), "Failed to print invoice.", Toast.LENGTH_SHORT).show();
                    } else if (currentPrintJob.isCancelled()) {
                        Toast.makeText(getActivity(), "Invoice printing was cancelled.", Toast.LENGTH_SHORT).show();
                    }else if (currentPrintJob.isStarted() || currentPrintJob.isQueued()) {
                        Toast.makeText(getActivity(), "Print job is processing.", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 3000); // Delay to give time for user to complete action

            //end sending the invoice to printer

            // Assuming your container ID in the activity is R.id.fragment_container
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, invoiceFragment) // Replace with your actual container ID
                    .addToBackStack(null) // So the user can navigate back
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //used for the call to customer
    @OptIn(markerClass = UnstableApi.class)
    private void makePhoneCall(String phoneNumber) {

        //String phoneNumber = "1234567890"; // Replace with actual logic to get the phone number
        Intent intent = new Intent(Intent.ACTION_DIAL);

        if (phoneNumber != null && !phoneNumber.isEmpty()) {

            Toast.makeText(getContext(), "Calling customer at " + phoneNumber, Toast.LENGTH_LONG).show();
            Log.d("MeasurementDetFragment", "Initiating call to: " + phoneNumber);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);

        } else {
            Toast.makeText(getContext(), "Customer phone number not available.", Toast.LENGTH_SHORT).show();
        }
    }
    //used for the print invoice
    // New method to generate the invoice string content
    @OptIn(markerClass = UnstableApi.class)
    private String generateInvoicePdfFile() {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(612, 792, 1).create(); // Letter size
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(16);
        paint.setColor(Color.BLACK);

        // You have access to orderId and customerId in OrderDetFragment
        if (orderId == null) {
            //Log.e("OrderDetFragment", "Order ID is null, cannot generate invoice.");
            return null;
        }

        Order order = MyDatabase.INSTANCE.orderDao().getOrderById(orderId);
        if (order == null) {
            //Log.e("OrderDetFragment", "Order not found for ID: " + orderId);
            return null;
        }

        Customer customer = MyDatabase.INSTANCE.customerDao().getCustomer(order.getCustomerId());
        if (customer == null) {
            //Log.e("OrderDetFragment", "Customer not found for ID: " + order.getCustomerId());
            return null;
        }
        // Get and format today's date
        String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date());
        //generating invoivce number randomly
        String cusId = String.valueOf(customer.getId());
        String cusName = customer.getName();
        String invoiceId = generateInvoiceId(cusName, cusId);
        //Log.e("OrderDetFragment", "Order not found for ID: " + cusId);
        int x = 40, y = 60, spacing = 30;
        canvas.drawText(getString(R.string.invoiceNo), x, y, paint);
        canvas.drawText(invoiceId, x + 77, y, paint);
        paint.setTextSize(10);
        canvas.drawText(getString(R.string.date),x, y + 15, paint);
        canvas.drawText(currentDate,x + 25, y + 15, paint);

        paint.setTextSize(14);
        // Branding: Logo
        Bitmap logo = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.smlogo);
        // Resize: scale to desired width and height
        int desiredWidth = 80;  // make it smaller or larger
        int desiredHeight = 80;
        Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, desiredWidth, desiredHeight, true);
        canvas.drawBitmap(scaledLogo, 520, 10, paint);
        paint.setTextSize(22);
        canvas.drawText(getString(R.string.smartTailor), x + 160, y + 1 * spacing, paint);
        paint.setTextSize(18);
        canvas.drawText(getString(R.string.customer_information), x, y + 3 * spacing, paint);
        canvas.drawLine(x, 66 + 3 * spacing, 572, 66 + 3 * spacing, paint);
        paint.setTextSize(14);
        canvas.drawText(getString(R.string.customerName), x, y + 4 * spacing, paint);
        canvas.drawText(customer.getName(), x + 210, y + 4 * spacing, paint);

        canvas.drawText(getString(R.string.contact), x, y + 5 * spacing, paint);
        canvas.drawText("+92 " + customer.getMobile(), x + 210, y + 5 * spacing, paint);

        canvas.drawText(getString(R.string.address), x, y + 6 * spacing, paint);
        canvas.drawText(customer.getAddress(), x + 210, y + 6 * spacing, paint);

        paint.setTextSize(18);
        canvas.drawText(getString(R.string.customerSummary), x, y + 7 * spacing, paint);
        canvas.drawLine(x, 66 + 7 * spacing, 572, 66 + 7 * spacing, paint);

        canvas.drawText(getString(R.string.item), x, y + 8 * spacing, paint);
        canvas.drawText(getString(R.string.rqty), x + 200, y + 8 * spacing, paint);
        canvas.drawText(getString(R.string.price), x + 300, y + 8 * spacing, paint);
        canvas.drawLine(x, 76 + 8 * spacing, 572, 76 + 8 * spacing, paint);
        paint.setTextSize(14);

        canvas.drawText(getString(R.string.quantity), x, y + 9 * spacing + 10, paint);
        canvas.drawText(String.valueOf(order.getQuantity()), x + 207, y + 9 * spacing + 10, paint);
        canvas.drawText(getString(R.string.pricePerItem), x, y + 10 * spacing + 10, paint);
        canvas.drawText(String.valueOf(order.getPrice()), x + 300, y + 10 * spacing + 10, paint);
        canvas.drawText(getString(R.string.totalAmount), x, y + 11 * spacing + 10, paint);
        canvas.drawText(String.valueOf(order.getTotal()), x + 300, y + 11 * spacing + 10, paint);
        canvas.drawText(getString(R.string.advancedPaid), x, y + 12 * spacing + 10, paint);
        canvas.drawText(String.valueOf(order.getAdvance()), x + 300, y + 12 * spacing + 10, paint);
        canvas.drawText(getString(R.string.remainingAmount), x, y + 13 * spacing + 10, paint);
        canvas.drawText(String.valueOf(order.getRemaining()), x + 300, y + 13 * spacing + 10, paint);

        // Step 1: Create a date formatter
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        canvas.drawText(getString(R.string.orderDate), x, y + 14 * spacing + 10, paint);
        canvas.drawText(sdf.format(order.getDated()), x + 300, y + 14 * spacing + 10, paint);
        canvas.drawText(getString(R.string.collectionDate), x, y + 15 * spacing + 10, paint);
        canvas.drawText(sdf.format(order.getCollectionDate()), x + 300, y + 15 * spacing + 10, paint);
        canvas.drawLine(x, 86 + 15 * spacing, 572, 86 + 15 * spacing, paint);

        canvas.drawText(getString(R.string.thanksforurOrder), x, y + 17 * spacing, paint);
        //canvas.drawText("Generated by Tariq's App", x, y + 8 * spacing, paint);

        // QR Code (optional)
        //Bitmap qrCode = generateQRCode("upi://pay?pa=yourid@bank&am=" + remainingDue);
        Bitmap qrCode = generateQRCode("upi://pay?pa=yourid@bank&am=" + 5000);
        canvas.drawBitmap(qrCode, 300, 580, paint);

        // Signature Field
        canvas.drawText(getString(R.string.signature), x, 620, paint);
        canvas.drawLine(80 + 28, 620, 200 + 28, 620, paint);

        // Footer
        //canvas.drawText("Thank you for your business!", 10, 360, paint);
        //canvas.drawText("Contact: +92-XXX-XXXXXXX", 10, 380, paint);


        pdfDocument.finishPage(page);

        File file = new File(requireContext().getExternalFilesDir(null), "invoice_output.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        pdfDocument.close();
        return file.getAbsolutePath();
    }
    public Bitmap generateQRCode(String text) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 100, 100);
            BarcodeEncoder encoder = new BarcodeEncoder();
            return encoder.createBitmap(bitMatrix);
        } catch (Exception e) {
            Timber.e(e, "QR generation failed");
            return null;
        }
    }
    // generate random invoice id from name, id and current date
    public static String generateInvoiceId(String name, String customerId) {
        // Normalize inputs
        String namePart = name.replaceAll("\\s+", "").toUpperCase(); // e.g., "TARIQKHAN"
        String idPart = customerId.toUpperCase();                    // e.g., "CUST123"

        // Get current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String datePart = sdf.format(new Date());                    // e.g., "20250903"

        // Add random suffix
        String randomSuffix = String.valueOf(new Random().nextInt(9000) + 1000); // e.g., "4721"

        // Combine all parts
        return namePart + "-" + idPart + "-" + datePart + "-" + randomSuffix;
    }
    //used to up the ui or view layout from bottom nav
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

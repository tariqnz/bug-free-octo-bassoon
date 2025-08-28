package com.example.app;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.media3.common.util.UnstableApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;

// --- Mock/Placeholder classes for compilation ---
class Order {
    public String getId() { return "ORD-123"; }
    public int getCustomerId() { return 1; }
    public int getQuantity() { return 2; }
    public double getPrice() { return 50.0; }
    public double getTotal() { return 100.0; }
    public double getAdvance() { return 20.0; }
    public double getRemaining() { return 80.0; }
    public long getDated() { return System.currentTimeMillis(); }
    public long getCollectionDate() { return System.currentTimeMillis() + 86400000L * 5; }
}
class Customer {
    public String getName() { return "John Doe"; }
    public String getMobile() { return "555-1234"; }
    public String getAddress() { return "123 Main Street, Anytown, USA. This is a very long address designed to test the line wrapping functionality of the PDF generator to ensure that it correctly breaks the line and moves to the next one instead of letting the text run off the page."; }
}
class MyDatabase {
    public static final MyDatabase INSTANCE = new MyDatabase();
    private final OrderDao orderDao = new OrderDao();
    private final CustomerDao customerDao = new CustomerDao();
    public OrderDao orderDao() { return orderDao; }
    public CustomerDao customerDao() { return customerDao; }
}
class OrderDao { public Order getOrderById(String id) { return new Order(); } }
class CustomerDao { public Customer getCustomer(int id) { return new Customer(); } }
// --- End Mock classes ---

public class OrderDetFragment extends Fragment {

    private String orderId; // Assume this is a member variable of the fragment

    @OptIn(markerClass = UnstableApi.class)
    public String generateInvoicePdf(Order order, Customer customer) {
        if (order == null || customer == null) {
            Log.e("OrderDetFragment", "Order or Customer data is null.");
            return null;
        }

        // --- Document Setup ---
        final int PAGE_WIDTH = 595;
        final int PAGE_HEIGHT = 842;
        final int TOP_MARGIN = 40;
        final int BOTTOM_MARGIN = 40;
        final int LEFT_MARGIN = 40;
        final int RIGHT_MARGIN = 40;
        final int LINE_SPACING = 20;

        PdfDocument pdfDocument = new PdfDocument();

        // --- Paint Setup ---
        Paint paint = new Paint();
        paint.setTextSize(12);
        paint.setColor(0xFF000000); // Black

        Paint titlePaint = new Paint();
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(18);
        titlePaint.setColor(0xFF000000); // Black

        // --- Page State Management ---
        // A simple container class to hold mutable state that can be modified by helper methods.
        class PageState {
            PdfDocument.Page page;
            Canvas canvas;
            int y;
            int number = 1;

            PageState() {
                startNewPage();
                this.y = TOP_MARGIN;
            }

            void startNewPage() {
                if (page != null) {
                    pdfDocument.finishPage(page);
                }
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, number++).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                y = TOP_MARGIN;
            }

            void checkPageBreak(int spaceNeeded) {
                if (this.y + spaceNeeded > PAGE_HEIGHT - BOTTOM_MARGIN) {
                    startNewPage();
                }
            }
        }

        PageState state = new PageState();

        // --- Drawing Logic ---

        // Title
        state.checkPageBreak((int) titlePaint.getFontSpacing() * 2);
        state.canvas.drawText("INVOICE", (PAGE_WIDTH - titlePaint.measureText("INVOICE")) / 2, state.y, titlePaint);
        state.y += titlePaint.getFontSpacing() * 2;

        // Customer Details
        state.checkPageBreak(LINE_SPACING * 5); // Approximate space for the details block
        state.canvas.drawText("Order ID: " + order.getId(), LEFT_MARGIN, state.y, paint);
        state.y += LINE_SPACING;
        state.canvas.drawText("Customer Name: " + customer.getName(), LEFT_MARGIN, state.y, paint);
        state.y += LINE_SPACING;
        state.canvas.drawText("Contact: " + customer.getMobile(), LEFT_MARGIN, state.y, paint);
        state.y += LINE_SPACING;

        // Address with Text Wrapping
        if (customer.getAddress() != null && !customer.getAddress().isEmpty()) {
            state.canvas.drawText("Address:", LEFT_MARGIN, state.y, paint);
            state.y += 5; // Small gap before the address block

            TextPaint addressPaint = new TextPaint(paint);
            int textWidth = PAGE_WIDTH - (LEFT_MARGIN + 20) - RIGHT_MARGIN; // Indent address
            StaticLayout addressLayout = new StaticLayout(customer.getAddress(), addressPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

            state.checkPageBreak(addressLayout.getHeight());

            state.canvas.save();
            state.canvas.translate(LEFT_MARGIN + 20, state.y);
            addressLayout.draw(state.canvas);
            state.canvas.restore();
            state.y += addressLayout.getHeight();
        }
        state.y += LINE_SPACING * 1.5; // Extra space after address block

        // Order Summary
        state.checkPageBreak((int) (titlePaint.getFontSpacing() + (LINE_SPACING * 5)));
        state.canvas.drawText("Order Summary", LEFT_MARGIN, state.y, titlePaint);
        state.y += titlePaint.getFontSpacing();
        state.canvas.drawText("Quantity: " + order.getQuantity(), LEFT_MARGIN + 10, state.y, paint);
        state.y += LINE_SPACING;
        state.canvas.drawText("Price per Item: " + order.getPrice(), LEFT_MARGIN + 10, state.y, paint);
        state.y += LINE_SPACING;
        state.canvas.drawText("Total Amount: " + order.getTotal(), LEFT_MARGIN + 10, state.y, paint);
        state.y += LINE_SPACING;
        state.canvas.drawText("Advance Paid: " + order.getAdvance(), LEFT_MARGIN + 10, state.y, paint);
        state.y += LINE_SPACING;
        state.canvas.drawText("Remaining Due: " + order.getRemaining(), LEFT_MARGIN + 10, state.y, paint);
        state.y += LINE_SPACING * 1.5;

        // Dates
        state.checkPageBreak(LINE_SPACING * 2);
        String orderDateStr = order.getDated() != 0L ? DateFormat.getDateInstance(DateFormat.MEDIUM).format(order.getDated()) : "N/A";
        state.canvas.drawText("Order Date: " + orderDateStr, LEFT_MARGIN, state.y, paint);
        state.y += LINE_SPACING;

        String collectionDateStr = order.getCollectionDate() != 0L ? DateFormat.getDateInstance(DateFormat.MEDIUM).format(order.getCollectionDate()) : "N/A";
        state.canvas.drawText("Collection Date: " + collectionDateStr, LEFT_MARGIN, state.y, paint);
        state.y += LINE_SPACING * 2;

        // Footer
        state.checkPageBreak((int) titlePaint.getFontSpacing());
        state.canvas.drawText("Thank you!", (PAGE_WIDTH - titlePaint.measureText("Thank you!")) / 2, state.y, titlePaint);

        // --- Finalize and Save ---
        pdfDocument.finishPage(state.page);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "invoice-" + order.getId() + ".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            Log.e("OrderDetFragment", "Error writing PDF file", e);
            // Close the document even if writing fails
            pdfDocument.close();
            return null;
        }
        pdfDocument.close();
        // Return the absolute path of the created file on success
        return file.getAbsolutePath();
    }
}

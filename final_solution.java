// =======================================================================================
// FINAL SOLUTION AND INSTRUCTIONS
// =======================================================================================
//
// STEP 1: Add the following 'import' statements to the top of your `OrderDetFragment.java` file.
//         Add any that are missing.
// ---------------------------------------------------------------------------------------
// import android.graphics.Canvas;
// import android.graphics.Paint;
// import android.graphics.Typeface;
// import android.graphics.pdf.PdfDocument;
// import android.os.Environment;
// import android.text.Layout;
// import android.text.StaticLayout;
// import android.text.TextPaint;
// import android.util.Log;
// import androidx.annotation.OptIn;
// import androidx.media3.common.util.UnstableApi;
// import java.io.File;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.text.DateFormat;
//
//
// STEP 2: Delete your entire existing `generateInvoicePdf` method from your file.
//
//
// STEP 3: Copy the method below and paste it into your file where the old method was.
// ---------------------------------------------------------------------------------------

@OptIn(markerClass = UnstableApi.class)
public String generateInvoicePdf(String invoiceText) {
    // This is a hyper-defensive version to prevent overflow errors.
    // It keeps your original method signature for compatibility.

    // Data Fetching (as in your original code)
    if (orderId == null) {
        Log.e("OrderDetFragment", "Order ID is null, cannot generate invoice.");
        return null;
    }
    Order order = MyDatabase.INSTANCE.orderDao().getOrderById(orderId);
    Customer customer = MyDatabase.INSTANCE.customerDao().getCustomer(order.getCustomerId());
    if (order == null || customer == null) {
        Log.e("OrderDetFragment", "Failed to retrieve Order or Customer from database.");
        return null;
    }

    // Document Setup
    final int PAGE_WIDTH = 595;
    final int PAGE_HEIGHT = 842;
    final int TOP_MARGIN = 40;
    final int BOTTOM_MARGIN = 40;
    final int LEFT_MARGIN = 40;
    final int RIGHT_MARGIN = 40;

    PdfDocument pdfDocument = new PdfDocument();
    Paint paint = new Paint();
    paint.setTextSize(12);
    Paint titlePaint = new Paint(paint);
    titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    titlePaint.setTextSize(18);

    // This helper class manages the page state and drawing logic.
    class PageState {
        PdfDocument.Page page;
        Canvas canvas;
        int y = TOP_MARGIN;
        int number = 1;

        PageState() { startNewPage(); }

        void startNewPage() {
            if (page != null) pdfDocument.finishPage(page);
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, number++).create();
            page = pdfDocument.startPage(pageInfo);
            canvas = page.getCanvas();
            y = TOP_MARGIN;
        }

        // This method draws any text with wrapping to prevent overflow.
        void drawWrappedText(String text, Paint textPaint, int indent) {
            if (text == null || text.isEmpty()) return;

            int availableWidth = PAGE_WIDTH - (LEFT_MARGIN + indent) - RIGHT_MARGIN;
            TextPaint tp = new TextPaint(textPaint);
            StaticLayout layout = new StaticLayout(text, tp, availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

            if (this.y + layout.getHeight() > PAGE_HEIGHT - BOTTOM_MARGIN) {
                startNewPage();
            }

            canvas.save();
            canvas.translate(LEFT_MARGIN + indent, y);
            layout.draw(canvas);
            canvas.restore();
            y += layout.getHeight() + 5; // Add padding after each element
        }
    }

    PageState state = new PageState();

    // The entire drawing process is wrapped in a try-catch block for safety.
    try {
        state.drawWrappedText("INVOICE", titlePaint, (PAGE_WIDTH / 2) - LEFT_MARGIN - 60); // Approx center
        state.y += 20;

        state.drawWrappedText("Order ID: " + order.getId(), paint, 0);
        state.drawWrappedText("Customer Name: " + customer.getName(), paint, 0);
        state.drawWrappedText("Contact: " + customer.getMobile(), paint, 0);
        state.drawWrappedText("Address:", paint, 0);
        state.drawWrappedText(customer.getAddress(), paint, 20); // Indented address
        state.y += 20;

        state.drawWrappedText("Order Summary", titlePaint, 0);
        state.drawWrappedText("Quantity: " + order.getQuantity(), paint, 10);
        state.drawWrappedText("Price per Item: " + order.getPrice(), paint, 10);
        state.drawWrappedText("Total Amount: " + order.getTotal(), paint, 10);
        state.drawWrappedText("Advance Paid: " + order.getAdvance(), paint, 10);
        state.drawWrappedText("Remaining Due: " + order.getRemaining(), paint, 10);
        state.y += 20;

        String orderDateStr = order.getDated() != 0L ? DateFormat.getDateInstance(DateFormat.MEDIUM).format(order.getDated()) : "N/A";
        state.drawWrappedText("Order Date: " + orderDateStr, paint, 0);
        String collectionDateStr = order.getCollectionDate() != 0L ? DateFormat.getDateInstance(DateFormat.MEDIUM).format(order.getCollectionDate()) : "N/A";
        state.drawWrappedText("Collection Date: " + collectionDateStr, paint, 0);
        state.y += 40;

        state.drawWrappedText("Thank you!", titlePaint, (PAGE_WIDTH / 2) - LEFT_MARGIN - 80); // Approx center

    } catch (Exception e) {
        Log.e("PDF_GEN_FATAL", "A fatal error occurred during PDF drawing.", e);
    } finally {
        // Finalize and save the document.
        pdfDocument.finishPage(state.page);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "invoice-" + order.getId() + ".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            Log.e("OrderDetFragment", "Error writing PDF file", e);
            pdfDocument.close();
            return null; // Return null on failure
        }
        pdfDocument.close();
        return file.getAbsolutePath(); // Return file path on success
    }
}

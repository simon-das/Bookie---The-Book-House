package com.bookie_tbh.bookie;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.bookie_tbh.bookie.ui.OnSwipeTouchListener;

import java.io.File;
import java.io.IOException;

public class PdfViewer extends AppCompatActivity {

    private String pdfUrl;
    private SubsamplingScaleImageView pdfView;
    private TextView pageNumber;
    private int ind = 0, currentPageWidth, currentPageHeight, pageCount;
    private ParcelFileDescriptor fileDescriptor = null;
    private PdfRenderer pdfRenderer = null;
    private PdfRenderer.Page currentPage;
    private Bitmap bitmap;
    private File file;
    private float initialScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

//      initializing the pdf-view and page number text-view
        pdfView = (SubsamplingScaleImageView) findViewById(R.id.pdfview);
        pageNumber = (TextView) findViewById(R.id.page_number);

//      getting intent and pdf url
        Intent intent = getIntent();
        pdfUrl = intent.getStringExtra("pdfUrl");

//      open pdf
        file = new File(pdfUrl);
        try {
            openPDF();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void openPDF() throws IOException {

        fileDescriptor = ParcelFileDescriptor.open(
                file, ParcelFileDescriptor.MODE_READ_ONLY);
        pdfRenderer = new PdfRenderer(fileDescriptor);
        pageCount = pdfRenderer.getPageCount();

//      display first page
        openPage(ind);

//      change page on swipe
        pdfView.setOnTouchListener(new OnSwipeTouchListener(this){
            public void onSwipeRight() {
                if (initialScale == 0)
                    initialScale = pdfView.getScale();
                initialScale += 0.01;
                if (ind > 0 && initialScale >= pdfView.getScale()){
                    currentPage.close();
                    ind -= 1;
                    openPage(ind);
                }
            }
            public void onSwipeLeft() {
                if (initialScale == 0)
                    initialScale = pdfView.getScale();
                initialScale += 0.01;
                if (ind < pageCount - 1 && initialScale >= pdfView.getScale()){
                    currentPage.close();
                    ind += 1;
                    openPage(ind);
                }
            }
        });
    }

    private void openPage(int index){
        pageNumber.setText(ind+1 + "/" + pageCount);
        currentPage = pdfRenderer.openPage(index);
        currentPageWidth = currentPage.getWidth();
        currentPageHeight = currentPage.getHeight();
        bitmap = Bitmap.createBitmap(
                getResources().getDisplayMetrics().densityDpi * currentPageWidth / 72,
                getResources().getDisplayMetrics().densityDpi * currentPageHeight / 72,
                Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null,
                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        pdfView.setImage(ImageSource.bitmap(bitmap));
        initialScale = pdfView.getScale();
    }

    @Override
    protected void onDestroy() {
        try {
            currentPage.close();
            pdfRenderer.close();
            fileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
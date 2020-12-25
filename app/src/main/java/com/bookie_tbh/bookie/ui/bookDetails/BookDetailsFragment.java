package com.bookie_tbh.bookie.ui.bookDetails;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.bookie_tbh.bookie.R;
import com.bookie_tbh.bookie.ReadOnline;
import com.bookie_tbh.bookie.SignInDialog;
import com.bookie_tbh.bookie.ui.ratingReview.RatingReview;
import com.bookie_tbh.bookie.ui.ratingReview.RatingReviewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class BookDetailsFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private String bookName, writerName, image, pdf;
    private TextView bookName_textView, writerName_textView;
    private ImageView bookImage;

//  for reading online
    private Button readOnlineButton;

//  for downloading
    private Button downloadButton;
    private File filePdf, fileImage, filePdfTemp, fileImageTemp;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference tokenReference;
    private String access_token, pdfExtension, imageExtension;
    private DbxRequestConfig config;
    private DbxClientV2 client;
    private OutputStream imageOutputStream, pdfOutputStream;
    private Context context;


//  for rating_review portion
    private RecyclerView ratingReviewRecyclerView;
    private RatingReviewAdapter ratingReviewAdapter;
    private List<RatingReview> ratingReviewList;
    private RatingBar ratingBarBooks, ratingBarUser;
    private TextView rating;
    private EditText review;
    private Button ratingReviewButton;
    private float totalRating, averageRating;
    private int totalRatingCount;
    private String segmentName, bookKey;

//  database reference variables
    private DatabaseReference ratingReviewRef;

    public BookDetailsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_book_details, container, false);

//      set book name
        bookName = getArguments().getString("bookName");
        bookName_textView = (TextView) root.findViewById(R.id.book_name__book_details);
        bookName_textView.setText(bookName);

//      set writer name
        writerName = getArguments().getString("writerName");
        writerName_textView = (TextView) root.findViewById(R.id.writer_name__book_details);
        writerName_textView.setText(writerName);

//      set book image
        image = getArguments().getString("image");
        bookImage = (ImageView) root.findViewById(R.id.book_image_view__book_details);
        RequestOptions options = new RequestOptions().placeholder(R.drawable.book_loading).timeout(50000);
        Glide.with(this).load(image).apply(options).into(bookImage);


//      check internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

//      get pdf download link
        pdf = getArguments().getString("pdf");

//      initializing the read online button
        readOnlineButton = (Button) root.findViewById(R.id.read_online_button_book_details);
//      set on click listener to read online button
        readOnlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (activeNetwork != null){
                    if (pdf.equals("No online version")){
                        Toast.makeText(getContext(), pdf + " available", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent = new Intent(getContext(), ReadOnline.class);
                        intent.putExtra("pdfUrl", pdf);
                        getContext().startActivity(intent);
                    }

                }else
                    Toast.makeText(getContext(), "No internet check connection", Toast.LENGTH_SHORT).show();

            }
        });

//      database instance
        firebaseDatabase = FirebaseDatabase.getInstance();
//      token reference
        tokenReference = firebaseDatabase.getReference("access_token");

//      get segment name and book key
        segmentName = getArguments().getString("segment");
        bookKey = getArguments().getString("bookKey");

//      image files for downloading
        imageExtension = ".png";
        fileImageTemp = new File(getContext().getFilesDir(), bookName + imageExtension + "temp");
        fileImage = new File(getContext().getFilesDir(), bookName + imageExtension);

//      pdf files for downloading
        pdfExtension = ".pdf";
        filePdfTemp = new File(getContext().getFilesDir(), bookName + pdfExtension + "temp");
        filePdf = new File(getContext().getFilesDir(), bookName + pdfExtension);

//      initializing the download button
        downloadButton = (Button) root.findViewById(R.id.download_button_book_details);
//      download button click event
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, should we show an explanation?
                // No explanation; request the permission
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                if (activeNetwork != null)
                    startDownload();
                else
                    Toast.makeText(getContext(), "No internet check connection", Toast.LENGTH_SHORT).show();
            }

            }
        });




//      initializing rating and review variables
        ratingBarBooks = (RatingBar) root.findViewById(R.id.rating_bar_books);
        rating = (TextView) root.findViewById(R.id.rating);
        ratingBarUser = (RatingBar) root.findViewById(R.id.rating_bar_for_user);
        review = (EditText) root.findViewById(R.id.review_editText);
        ratingReviewButton = (Button) root.findViewById(R.id.rating_review_button);

//      setting up the rating_review recycler view
        ratingReviewRecyclerView = (RecyclerView) root.findViewById(R.id.rating_review_recycler_view);
        ratingReviewRecyclerView.setHasFixedSize(true);
        ratingReviewRecyclerView.setNestedScrollingEnabled(false);
        ratingReviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

//      testing with dummy values
        ratingReviewList = new ArrayList<>();

//      initializing total rating and total rating count
        totalRating = 0;
        totalRatingCount = 0;

//      database reference initializing for rating and review
        ratingReviewRef = firebaseDatabase.getReference("Rating_Review/" + segmentName + "/" + bookKey);

//      fetching ratings and reviews
        ratingReviewRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    RatingReview ratingReview = child.getValue(RatingReview.class);
                    if (ratingReview.getRating() != 0){
                        totalRating += ratingReview.getRating();
                        totalRatingCount += 1;
                    }
                    ratingReviewList.add(ratingReview);
                }
//              calculate rating for this book
                averageRating = totalRating/totalRatingCount;

//              set the rating for this book
                ratingBarBooks.setRating(Float.parseFloat(String.format("%.1f", averageRating)));
                if (String.valueOf(averageRating).equals("NaN"))
                    rating.setText("0.0");
                else
                    rating.setText(String.format("%.1f", averageRating));

//              set adapter
                ratingReviewAdapter = new RatingReviewAdapter(ratingReviewList, getContext());
                ratingReviewRecyclerView.setAdapter(ratingReviewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



//      rating review button click event
        ratingReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null){
                    if (ratingBarUser.getRating() == 0 && review.getText().toString().isEmpty()){
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                        alertBuilder.setMessage("Please rate this book and write a review. Do at least one or both to post.")
                                .setPositiveButton("Ok", dialogClickListener).show();
                    }else{
                        DatabaseReference userNameRef = firebaseDatabase.getReference("Rating_Review/" + segmentName + "/" + bookKey + "/"
                                + currentUser.getUid() + "/userName");
                        DatabaseReference providerNameRef = firebaseDatabase.getReference("Rating_Review/" + segmentName + "/" + bookKey + "/"
                                + currentUser.getUid() + "/providerName");
                        DatabaseReference ratingRef = firebaseDatabase.getReference("Rating_Review/" + segmentName + "/" + bookKey + "/"
                                + currentUser.getUid() + "/rating");
                        DatabaseReference reviewRef = firebaseDatabase.getReference("Rating_Review/" + segmentName + "/" + bookKey + "/"
                                + currentUser.getUid() + "/review");

                        userNameRef.setValue(currentUser.getDisplayName());
                        providerNameRef.setValue(currentUser.getIdToken(false).getResult().getSignInProvider());
                        ratingRef.setValue(ratingBarUser.getRating());
                        reviewRef.setValue(review.getText().toString());

    //                  refresh the fragment
                        refreshFragment();
                    }
                }else{
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    signInDialog();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                    alertBuilder.setMessage("Please sign-in to give rating and review. Want to sign-in now ?")
                            .setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            }
        });

        return root;
    }

    //  sign-in dialog
    public void signInDialog(){
        SignInDialog signInDialog = new SignInDialog();
        signInDialog.show(getActivity().getSupportFragmentManager(), "Sign-in Dialog");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    startDownload();
                } else {
                    // permission denied
                    Toast.makeText(getContext(), "Please allow permission to download", Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }
    }


    private void refreshFragment(){
//      using bundle to pass info from one fragment to another fragment
        Bundle bundle = new Bundle();
        bundle.putString("bookName", bookName);
        bundle.putString("writerName", writerName);
        bundle.putString("image", image);
        bundle.putString("pdf", pdf);
        bundle.putString("segment", segmentName);
        bundle.putString("bookKey", bookKey);

//      changing fragment
        BookDetailsFragment fragment = new BookDetailsFragment();
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void startDownload(){
//      check if the book exists or not
        if (!filePdf.exists() || !fileImage.exists()){
//          checking already downloading or not
            downloadButton.setEnabled(false);
            Toast.makeText(getContext(), bookName + " is downloading", Toast.LENGTH_SHORT).show();
            tokenReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    access_token = (String) dataSnapshot.getValue();
//                  create dbx client
                    config = DbxRequestConfig.newBuilder("Secure").build();
                    client = new DbxClientV2(config, access_token);
                    try {
                        imageOutputStream = new FileOutputStream(fileImageTemp);
                        pdfOutputStream = new FileOutputStream(filePdfTemp);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                client.files().download("/" + segmentName + "/" + bookName + "/" + bookName + imageExtension).download(imageOutputStream);
                                client.files().download("/" + segmentName + "/" + bookName + "/" + bookName + pdfExtension).download(pdfOutputStream);
                                if (context != null && !filePdf.exists()){
                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, bookName + " is downloaded", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                fileImageTemp.renameTo(fileImage);
                                filePdfTemp.renameTo(filePdf);
                            } catch (DbxException | IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else {
//          book exists msg
            Toast.makeText(getContext(), "Book already exists", Toast.LENGTH_SHORT).show();
        }
    }
}

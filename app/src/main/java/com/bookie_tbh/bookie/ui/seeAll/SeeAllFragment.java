package com.bookie_tbh.bookie.ui.seeAll;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookie_tbh.bookie.R;
import com.bookie_tbh.bookie.ui.book.Book;
import com.bookie_tbh.bookie.ui.book.BookAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SeeAllFragment extends Fragment {

    private RecyclerView seeAllRecyclerView;
    private List<Book> bookItems;
    private BookAdapter bookAdapter;
    private String segmentName;
    private List<Pair<String, String>> bookKeys;

//    database reference variables
    private FirebaseDatabase database;
    private DatabaseReference booksRef;

    public SeeAllFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.fragment_see_all, container, false);

//        initializing the see-all recycler-view
        seeAllRecyclerView = (RecyclerView) root.findViewById(R.id.see_all_recycler_view);
        seeAllRecyclerView.setHasFixedSize(true);
        seeAllRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));


//      list initializing
        bookItems = new ArrayList<>();
        bookKeys = new ArrayList<>();

//      getting the segment name
        segmentName = getArguments().getString("segmentName");

//      database reference initializing
        database = FirebaseDatabase.getInstance();
        booksRef = database.getReference("Books/" + segmentName);

//      fetching books
        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Book book = child.getValue(Book.class);
                    Pair<String, String> bookKey = new Pair<String, String>(segmentName, child.getKey());
                    bookItems.add(book);
                    bookKeys.add(bookKey);
                }
//              shuffle the books
                long seed = System.nanoTime();
                Collections.shuffle(bookItems, new Random(seed));
                Collections.shuffle(bookKeys, new Random(seed));

        //      set adapter
                bookAdapter = new BookAdapter(bookItems, getContext(), "SeeAllFragment", bookKeys);
                seeAllRecyclerView.setAdapter(bookAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    };

}

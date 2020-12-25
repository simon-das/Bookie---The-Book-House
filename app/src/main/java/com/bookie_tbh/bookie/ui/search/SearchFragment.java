package com.bookie_tbh.bookie.ui.search;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

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
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView searchRecyclerView;
    private List<Book> bookItems;
    private BookAdapter bookAdapter;
    private List<Pair<String, String>> bookKeys;

    //    database reference variables
    private FirebaseDatabase database;
    private DatabaseReference booksRef;

//  search view
    private SearchView searchView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);

//      initializing the search recycler-view
        searchRecyclerView = (RecyclerView) root.findViewById(R.id.search_recycler_view);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

//      list initializing
        bookItems = new ArrayList<>();
        bookKeys = new ArrayList<>();

//      database reference initializing
        database = FirebaseDatabase.getInstance();
        booksRef = database.getReference("Books");

//      initializing search view
        searchView = (SearchView) root.findViewById(R.id.search_view);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

//              list initializing
                bookItems = new ArrayList<>();
                bookKeys = new ArrayList<>();

        //      fetching books
                booksRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot segment : dataSnapshot.getChildren()){
                            for(DataSnapshot child : segment.getChildren()){
                                Book book = child.getValue(Book.class);
                                Pair<String, String> bookKey = new Pair<String, String>(segment.getKey(), child.getKey());
                                if (book.getBookName().toLowerCase().contains(newText.toLowerCase())){
                                    bookItems.add(book);
                                    bookKeys.add(bookKey);
                                }
                            }
                        }

//                      set adapter
                        bookAdapter = new BookAdapter(bookItems, getContext(), "SeeAllFragment", bookKeys);
                        searchRecyclerView.setAdapter(bookAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                return false;
            }
        });



        return root;
    }
}

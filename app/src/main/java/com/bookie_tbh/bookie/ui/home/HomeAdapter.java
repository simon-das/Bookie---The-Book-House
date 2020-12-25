package com.bookie_tbh.bookie.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookie_tbh.bookie.R;
import com.bookie_tbh.bookie.ui.book.Book;
import com.bookie_tbh.bookie.ui.book.BookAdapter;
import com.bookie_tbh.bookie.ui.seeAll.SeeAllFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private List<HomeContent> homeItems;
    private List<Book> books;
    private Context context;
    private RecyclerView bookRecyclerView;
    private BookAdapter bookAdapter;
    private List<Pair<String, String>> bookKeys;

//    database reference variables
    private FirebaseDatabase database;
    private DatabaseReference booksRef;


    public HomeAdapter(List<HomeContent> homeItems, Context context) {
        this.homeItems = homeItems;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.home_content, parent, false);
        HomeViewHolder holder = new HomeViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeViewHolder holder, int position) {
        final HomeContent homeItem = homeItems.get(position);

//      set segment name
        holder.segmentName.setText(homeItem.getSegmentName());

        //      see all button click event
        holder.seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("segmentName", homeItem.getSegmentName());
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                SeeAllFragment fragment = new SeeAllFragment();
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).addToBackStack(null).commit();
            }
        });

//      set recycler view
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

//      initializing list
        books = new ArrayList<>();
        bookKeys = new ArrayList<>();

//      database reference initializing
        database = FirebaseDatabase.getInstance();
        booksRef = database.getReference("Books/" + homeItem.getSegmentName());

//      fetching books
        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Book book = child.getValue(Book.class);
                    Pair<String, String> bookKey = new Pair<String, String>(homeItem.getSegmentName(), child.getKey());;
                    books.add(book);
                    bookKeys.add(bookKey);
                }
//              shuffle the books
                long seed = System.nanoTime();
                Collections.shuffle(books, new Random(seed));
                Collections.shuffle(bookKeys, new Random(seed));

                bookAdapter = new BookAdapter(books, context, "HomeFragment", bookKeys);
                holder.recyclerView.setAdapter(bookAdapter);
                books = new ArrayList<>();
                bookKeys = new ArrayList<>();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return homeItems.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder{

        public TextView segmentName, seeAll;
        public RecyclerView recyclerView;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            segmentName = (TextView) itemView.findViewById(R.id.segmentName);
            seeAll = (TextView) itemView.findViewById(R.id.see_all);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.book_suggestions_recycler_view);
        }
    }

}

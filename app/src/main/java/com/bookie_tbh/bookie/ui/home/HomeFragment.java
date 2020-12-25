package com.bookie_tbh.bookie.ui.home;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookie_tbh.bookie.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

//  private HomeViewModel homeViewModel;
    private RecyclerView homeRecyclerView;
    private List<HomeContent> homeContents;
    private HomeAdapter homeAdapter;
    private FirebaseDatabase database;
    private DatabaseReference segmentRef;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

//      initializing the home recycler view
        homeRecyclerView = (RecyclerView) root.findViewById(R.id.home_recycler_view);
        homeRecyclerView.setHasFixedSize(true);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//      list initializing
        homeContents = new ArrayList<>();

//      check internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null)
            Toast.makeText(getContext(), "No internet check connection", Toast.LENGTH_SHORT).show();

//      setting up the database and books reference
        database = FirebaseDatabase.getInstance();
        segmentRef = database.getReference("Books");

        segmentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot segmentChild : dataSnapshot.getChildren()){
                    String homeContent = segmentChild.getKey();
                    homeContents.add(new HomeContent(homeContent));
                }
//              shuffle the segments
                Collections.shuffle(homeContents);

                homeAdapter = new HomeAdapter(homeContents, getContext());
                homeRecyclerView.setAdapter(homeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("error", String.valueOf(databaseError.toException()));
            }
        });

        return root;
    }

}

package com.bookie_tbh.bookie.ui.downloads;

import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookie_tbh.bookie.R;
import com.bookie_tbh.bookie.ui.book.Book;
import com.bookie_tbh.bookie.ui.book.BookAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DownloadsFragment extends Fragment {

    private RecyclerView downlaodsRecyclerView;
    private List<Book> downloadsBookItems;
    private BookAdapter downloadsBookAdapter;
    private List<Pair<String, String>> bookKeys;

    private String downloadsPath, extension;
    private File downloadsDirectory, specificDirectory;
    private File[] downloadedFiles, specificFiles;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_downloads, container, false);

//      initializing the downloads recycler view
        downlaodsRecyclerView = (RecyclerView) root.findViewById(R.id.downloads_recycler_view);
        downlaodsRecyclerView.setHasFixedSize(true);
        downlaodsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

//      list initializing
        downloadsBookItems = new ArrayList<>();
        bookKeys = new ArrayList<>();

        downloadsPath = getContext().getFilesDir().getAbsolutePath();
        downloadsDirectory = new File(downloadsPath);

        downloadedFiles = downloadsDirectory.listFiles();

        for(File file : downloadedFiles){
            extension = MimeTypeMap.getFileExtensionFromUrl(String.valueOf(Uri.fromFile(file)));
            if(extension.equals("pdf")){
                int lastIndex = file.getPath().lastIndexOf('.');
                String imagePath = file.getPath().substring(0, lastIndex) + ".png";
                downloadsBookItems.add(new Book(file.getName(), "", imagePath, file.getPath()));
            }
        }



//      shuffle the books
        long seed = System.nanoTime();
        Collections.shuffle(downloadsBookItems, new Random(seed));
        Collections.shuffle(bookKeys, new Random(seed));

//      set adapter
        downloadsBookAdapter = new BookAdapter(downloadsBookItems, getContext(), "DownloadsFragment", bookKeys);
        downlaodsRecyclerView.setAdapter(downloadsBookAdapter);

        return root;
    }
}

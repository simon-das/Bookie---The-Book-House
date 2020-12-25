package com.bookie_tbh.bookie.ui.book;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bookie_tbh.bookie.PdfViewer;
import com.bookie_tbh.bookie.R;
import com.bookie_tbh.bookie.ui.bookDetails.BookDetailsFragment;

import java.io.File;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> books;
    private Context context;
    private String fragmentName, bookUrl, segmentName, bookKey;
    private List<Pair<String, String>> bookKeys;

    public BookAdapter(List<Book> books, Context context, String fragmentName, List<Pair<String, String>> bookKeys) {
        this.books = books;
        this.context = context;
        this.fragmentName = fragmentName;
        this.bookKeys = bookKeys;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.book_item, parent, false);
        BookViewHolder holder = new BookViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final BookViewHolder holder, final int position) {
//      getting the book
        final Book book = books.get(position);

        RequestOptions options = new RequestOptions().placeholder(R.drawable.book_loading).timeout(50000);

//      click event on the book card-view
//      for downloads fragment
        if(fragmentName.equals("DownloadsFragment")){
//          set book name
            int lastIndex = book.getBookName().lastIndexOf('.');
            final String bookName = book.getBookName().substring(0, lastIndex);
            holder.bookName.setText(bookName);
//          set image
            File file = new File(book.getImage());
            Uri imageUri = Uri.fromFile(file);
            Glide.with(context).load(imageUri).timeout(50000).placeholder(R.drawable.book_loading)
                    .into(holder.bookImageView);

//          open pdf by clicking the book card
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PdfViewer.class);
                    intent.putExtra("pdfUrl", book.getPdf());
                    context.startActivity(intent);
                }
            });
        }
//     for other fragments
        else{

//          get book key and segment name
            segmentName = bookKeys.get(position).first;
            bookKey = bookKeys.get(position).second;

//          set book name
            holder.bookName.setText(book.getBookName());

//          set book image
            Glide.with(context).load(book.getImage())
                    .apply(options).into(holder.bookImageView);

//          card-view click event
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//              using bundle to pass info from one fragment to another fragment
                Bundle bundle = new Bundle();
                bundle.putString("bookName", book.getBookName());
                bundle.putString("writerName", book.getWriterName());
                bundle.putString("image", book.getImage());
                bundle.putString("pdf", book.getPdf());
                bundle.putString("segment", bookKeys.get(position).first);
                bundle.putString("bookKey", bookKeys.get(position).second);

//              changing fragment
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                BookDetailsFragment fragment = new BookDetailsFragment();
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).addToBackStack(null).commit();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder{

        public ImageView bookImageView;
        public TextView bookName;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImageView = (ImageView) itemView.findViewById(R.id.book_image_view__book_item);
            bookName = (TextView) itemView.findViewById(R.id.book_name__book_item);
        }
    }

}

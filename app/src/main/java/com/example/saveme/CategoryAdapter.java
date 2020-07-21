package com.example.saveme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Category> categories;

    CategoryAdapter(ArrayList<Category> items) {
        categories = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View gridItem = inflater.inflate(R.layout.category_item, parent, false);
        return new CategoryItemHolder(gridItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

class CategoryItemHolder extends RecyclerView.ViewHolder {
    ImageView image;
    TextView title;
    TextView desc;

    public CategoryItemHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.category_img);
        title = itemView.findViewById(R.id.category_title);
        desc = itemView.findViewById(R.id.category_desc);
    }
}

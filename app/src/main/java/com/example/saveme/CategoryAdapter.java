package com.example.saveme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Category> categories;

    CategoryAdapter(List<Category> items) {
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
        Category catItem = categories.get(position);
        CategoryItemHolder catHolder = ((CategoryItemHolder) holder);
        // Picasso.get().load(catItem.image).into(catHolder.image); todo check
        catHolder.title.setText(catItem.title);
        catHolder.desc.setText(catItem.description);
    }

    @Override
    public int getItemCount() {
        return categories.size();
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

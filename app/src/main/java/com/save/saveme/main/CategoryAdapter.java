package com.save.saveme.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.save.saveme.R;

import java.util.ArrayList;

/**
 * a class for the category adapter
 */
public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Category> categories;
    private CategoryClickListener categoryClickListener;
    private CategoryLongClickListener categoryLongClickListener;

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        Category catItem = categories.get(position);
        CategoryItemHolder catHolder = ((CategoryItemHolder) holder);
        catHolder.title.setText(catItem.title);
        catHolder.image.setImageResource(catItem.getImage());

        // set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryClickListener != null) {
                    categoryClickListener.onCategoryClicked(position);
                }
            }
        });

        // set long click listener
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (categoryLongClickListener != null) {
                    categoryLongClickListener.onCategoryLongClicked(position);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     * set a category click listener
     *
     * @param categoryClickListener - the listener
     */
    public void setCategoryClickListener(CategoryClickListener categoryClickListener) {
        this.categoryClickListener = categoryClickListener;
    }

    /**
     * set a long click listener
     *
     * @param categoryLongClickListener - the listener
     */
    public void setCategoryLongClickListener(CategoryLongClickListener categoryLongClickListener) {
        this.categoryLongClickListener = categoryLongClickListener;
    }

    /**
     * deletes a category from adapter
     *
     * @param category - the adapter
     */
    public void deleteCategory(Category category) {
        categories.remove(category);
    }
}

/**
 * a class for a category holder
 */
class CategoryItemHolder extends RecyclerView.ViewHolder {
    ImageView image;
    TextView title;

    public CategoryItemHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.category_img);
        title = itemView.findViewById(R.id.category_title);
    }
}

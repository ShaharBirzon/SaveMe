package com.example.saveme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class DocumentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Document> documents;

    private DocumentClickListener documentClickListener;
    private DocumentLongClickListener documentLongClickListener;

    DocumentAdapter(ArrayList<Document> items) {
        documents = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View gridItem = inflater.inflate(R.layout.document_item, parent, false);
        return new DocumentItemHolder(gridItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        Document docItem = documents.get(position);
        DocumentItemHolder docHolder = ((DocumentItemHolder) holder);
        docHolder.title.setText(docItem.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (documentClickListener != null) {
                    documentClickListener.onDocumentClicked(position);
                }
            }
        });

        // set long click listener
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (documentLongClickListener != null) {
                    documentLongClickListener.onDocumentLongClicked(position);
                    return true;
                }
                return false; //todo check
            }
        });
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }


    public void setDocumentClickListener(DocumentClickListener documentClickListener) {
        this.documentClickListener = documentClickListener;
    }

    public void setDocumentLongClickListener(DocumentLongClickListener documentLongClickListener) {
        this.documentLongClickListener = documentLongClickListener;
    }

    //delete a document
    public void deleteDocument(Document document) {
        documents.remove(document);
    }
}

class DocumentItemHolder extends RecyclerView.ViewHolder {
    TextView title;
    // todo check if need to add more

    public DocumentItemHolder(@NonNull View itemView) {
        super(itemView);
        //todo check what needs to hold
        title = itemView.findViewById(R.id.document_title);
    }
}

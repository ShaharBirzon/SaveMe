package com.save.saveme.category;

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
 * adapter for the document list
 */
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
        docHolder.expirationDate.setText(docItem.getExpirationDate());
        if (docItem.getHasAlarm()){
            docHolder.alarmImg.setImageResource(R.drawable.ic_alarm_on);
        }
        else {
            docHolder.alarmImg.setImageResource(R.drawable.ic_alarm_off);
        }
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
                return false;
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

    /**
     * delete a document from adapter
     * @param document - the document to delte
     */
    public void deleteDocument(Document document) {
        documents.remove(document);
    }
}

/**
 * the document holder class for adapter
 */
class DocumentItemHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView expirationDate;
    ImageView alarmImg;

    public DocumentItemHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.document_title);
        expirationDate = itemView.findViewById(R.id.tv_expiry_date);
        alarmImg = itemView.findViewById(R.id.iv_alarm);
    }
}

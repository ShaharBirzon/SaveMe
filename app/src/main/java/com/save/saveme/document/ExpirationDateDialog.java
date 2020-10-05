package com.save.saveme.document;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.save.saveme.R;

/**
 * a dialog for the expiration date. This dialog present the found expiration dates from
 * the document
 */
public class ExpirationDateDialog extends DialogFragment {
    private static final String TAG = "ExpirationDateDialog";
    private String[] possibleDates; //categories titles not used for spinner

    public ExpirationDateDialog() {
    }

    public ExpirationDateDialog(String[] possibleDates) {
        this.possibleDates = possibleDates;
    }


    public OnExpirationDateInputListener mOnInputListener;

    //widgets
    private Button actionOkButton, actionCancelButton;
    private Spinner titleSpinner;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expiration_date_dialog_fragment, container, false);
        actionCancelButton = view.findViewById(R.id.btn_action_cancel_icon_selection);
        actionOkButton = view.findViewById(R.id.btn_action_ok);
        titleSpinner = view.findViewById(R.id.spinner_title);
        setCategoryTitle();

        actionCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });

        actionOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capturing input");
                String title = titleSpinner.getSelectedItem().toString();
                mOnInputListener.sendInput(title);
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInputListener = (OnExpirationDateInputListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    /**
     * Handles the event where the user chooses a category name
     */
    private void setCategoryTitle() {
        final ArrayAdapter<String> titlesAdapter = new ArrayAdapter<>(ExpirationDateDialog.this.getActivity(), android.R.layout.simple_list_item_1, possibleDates);
        titlesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        titleSpinner.setAdapter(titlesAdapter);
        titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                titleSpinner.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    public interface OnExpirationDateInputListener {
        void sendInput(String date);
    }


}

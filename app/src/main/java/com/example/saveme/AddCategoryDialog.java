package com.example.saveme;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;

public class AddCategoryDialog extends DialogFragment {
    private static final String TAG = "AddCategoryFragment";
    public AddCategoryDialog(){
    }


    public interface OnInputListener{
        void sendInput(String title, String description);
    }
    public OnInputListener mOnInputListener;

    //widgets
    private TextInputLayout titleInput;
    private TextInputLayout descriptionInput;
    private Button actionOkButton, actionCancelButton;
    private Spinner titleSpinner;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_category_dialog_fragment, container, false);
        actionCancelButton = view.findViewById(R.id.btn_action_cancel);
        actionOkButton = view.findViewById(R.id.btn_action_ok);
        titleInput = view.findViewById(R.id.et_title);
        titleInput.setVisibility(View.INVISIBLE);
        titleSpinner = view.findViewById(R.id.spinner_title);
        setCategoryTitle();
        descriptionInput = view.findViewById(R.id.et_description);

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

                String title = titleInput.getEditText().getText().toString();
                if (title == null || title.equals("")){
                    title = titleSpinner.getSelectedItem().toString();
                }
                String description = descriptionInput.getEditText().getText().toString();

                //TODO  add image
                mOnInputListener.sendInput(title, description);

                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnInputListener = (OnInputListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }

    /**
     * Handles the event where the user chooses a neighborhood
     */
    private void setCategoryTitle() {
        final ArrayAdapter<String> titlesAdapter = new ArrayAdapter<>(AddCategoryDialog.this.getActivity(), android.R.layout.simple_list_item_1, getActivity().getResources().getStringArray(R.array.categories));
        titlesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        titleSpinner.setAdapter(titlesAdapter);
        titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                titleSpinner.setSelection(position);
                String title = titlesAdapter.getItem(position);
                if (title.equals("Other")){
                    titleInput.setVisibility(View.VISIBLE);
                    ((RelativeLayout.LayoutParams) descriptionInput.getLayoutParams()).addRule(RelativeLayout.BELOW, R.id.et_title);
                }
                else {
                    titleInput.setVisibility(View.INVISIBLE);
                    ((RelativeLayout.LayoutParams) descriptionInput.getLayoutParams()).addRule(RelativeLayout.BELOW, R.id.spinner_title);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }
}

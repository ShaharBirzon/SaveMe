package com.save.saveme.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.save.saveme.R;
import com.google.android.material.textfield.TextInputLayout;

import static android.app.Activity.RESULT_OK;

public class AddCategoryDialog extends DialogFragment {
    private static final String TAG = "AddCategoryFragment";
    private static final int CATEGORY_ICON_REQUEST_CODE = 111;
    public static final int DEFAULT_ICON = R.drawable.buy;
    private String[] categoriesTitles; //categories titles not used for spinner
    private boolean isCategoryTitleValid = false;

    public AddCategoryDialog() {
    }

    public AddCategoryDialog(String[] categoriesTitles) {
        this.categoriesTitles = categoriesTitles;
    }


    public interface OnInputListener {
        void sendInput(String title, int image);
    }

    public OnInputListener mOnInputListener;

    //widgets
    private TextInputLayout titleInput;
    private Button actionOkButton, actionCancelButton, chooseIconButton;
    private Spinner titleSpinner;
    private ImageView iconPrevView;

    private int iconImageValue = DEFAULT_ICON; //initialized to default icon


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_category_dialog_fragment, container, false);
        actionCancelButton = view.findViewById(R.id.btn_action_cancel_icon_selection);
        actionOkButton = view.findViewById(R.id.btn_action_ok);
        chooseIconButton = view.findViewById(R.id.btn_choose_icon);
        iconPrevView = view.findViewById(R.id.iv_icon_img_prev);
        titleInput = view.findViewById(R.id.et_title);
        titleInput.setVisibility(View.INVISIBLE);
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
                if(userInputValid()){
                    addNewCategory();
                }
                else {
                    //todo change message
                    Toast.makeText(getContext(), "invalid input", Toast.LENGTH_LONG).show();
                }
            }
        });

        chooseIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: chooseIconButton");
                ChooseIconFragment chooseIconFragment = new ChooseIconFragment();
                chooseIconFragment.setTargetFragment(AddCategoryDialog.this, 111);
                chooseIconFragment.show(getFragmentManager(), "dialog");
            }
        });

        return view;
    }

    private boolean userInputValid() {
        return isCategoryTitleValid();
    }

    private boolean isCategoryTitleValid() {
        String title = titleInput.getEditText().getText().toString();
        boolean isTitleChosen=false;
        if (title.equals("")) {
            title = titleSpinner.getSelectedItem().toString();
            if (!title.equals("Choose Name…") && !title.equals("Other")){
                isTitleChosen =true;
            }
        }
        return isCategoryTitleValid || isTitleChosen;
    }

    private void addNewCategory() {
        String title = titleInput.getEditText().getText().toString();
        if (title == null || title.equals("")) {
            title = titleSpinner.getSelectedItem().toString();
        }

        //TODO  add image
        mOnInputListener.sendInput(title, iconImageValue);

        getDialog().dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInputListener = (OnInputListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    /**
     * Handles the event where the user chooses a category name
     */
    private void setCategoryTitle() {
        final ArrayAdapter<String> titlesAdapter = new ArrayAdapter<>(AddCategoryDialog.this.getActivity(), android.R.layout.simple_list_item_1, categoriesTitles);
        titlesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        titleSpinner.setAdapter(titlesAdapter);
        titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                titleSpinner.setSelection(position);
                String title = titlesAdapter.getItem(position);
                if (title.equals("Other")) {
                    titleInput.setVisibility(View.VISIBLE);
                    validateCategoryTitle();
                } else {
                    titleInput.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CATEGORY_ICON_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                iconImageValue = data.getIntExtra("iconIntValue", DEFAULT_ICON);
                iconPrevView.setVisibility(View.VISIBLE);
                iconPrevView.setImageResource(iconImageValue);
                Log.d(TAG, "set icon image to value: " + Integer.toString(iconImageValue));
            }
        }
    }

    /**
     * validate the entered category title.
     */
    private void validateCategoryTitle() {
        setIsDocumentTitleValidToTrueIfValid();
        titleInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isCategoryTitleValid = false;
                int inputLength = titleInput.getEditText().getText().toString().length();
                if (inputLength >= 16) {//todo check number
                    titleInput.setError("Maximum Limit Reached!");
                } else if (inputLength == 0) {
                    titleInput.setError("Category title is required!");
                } else {
                    titleInput.setError(null);
//                    curDocument.setTitle(documentTitleET.getEditText().getText().toString());
                    isCategoryTitleValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * set isDocumentTitleValid to true if document title is valid
     */
    private void setIsDocumentTitleValidToTrueIfValid() {
        int inputLength = titleInput.getEditText().getText().toString().length();
        if (inputLength < 16 && inputLength > 0) {
            isCategoryTitleValid = true;
        }
    }
}

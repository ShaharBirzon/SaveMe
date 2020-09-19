package com.example.saveme.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.widget.GridLayout;

import com.example.saveme.R;

import static android.app.Activity.RESULT_OK;

public class ChooseIconFragment extends DialogFragment {
    private static final int[] iconImages = {R.drawable.money, R.drawable.tax, R.drawable.lipstick, R.drawable.id, R.drawable.house, R.drawable.garden, R.drawable.fish, R.drawable.fan, R.drawable.email, R.drawable.dog, R.drawable.car, R.drawable.cake, R.drawable.buy, R.drawable.cat, R.drawable.company};
    private int lastImageToBeSelected = 0; //todo default image number
    private ImageView lastImage = null;
    private Button actionSaveButton, actionCancelButton;
    private static final String TAG = "ChooseIconFragment";
    private Drawable highlight;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_icon, container, false);
        highlight = getResources().getDrawable(R.drawable.icon_selection);
        setButtonsOnClickMethods(view);

        GridLayout grid = (GridLayout) view.findViewById(R.id.gl_choose_icon);
        int childCount = grid.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final ImageView currentImage = (ImageView) grid.getChildAt(i);
            final int finalI = i;
            currentImage.setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lastImage != null) {
                        lastImage.setBackground(null);
                    }
                    lastImage = currentImage;
                    lastImageToBeSelected = finalI;
                    currentImage.setBackground(highlight);
                }
            });
        }
        return view;
    }

    private void setButtonsOnClickMethods(View view) {
        actionSaveButton = view.findViewById(R.id.btn_save_category_icon);
        actionCancelButton = view.findViewById(R.id.btn_action_cancel_icon_selection);
        actionCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });
        actionSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: actionSaveButton");
                Intent intent = new Intent(ChooseIconFragment.this.getActivity(), AddCategoryDialog.class);
                intent.putExtra("iconIntValue", iconImages[lastImageToBeSelected]);
                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                getDialog().dismiss();
            }
        });
    }
}
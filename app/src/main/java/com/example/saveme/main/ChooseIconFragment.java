package com.example.saveme.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.GridLayout;

import com.example.saveme.R;
import static android.app.Activity.RESULT_OK;

public class ChooseIconFragment extends DialogFragment {
    int[] iconImages = {R.drawable.money, R.drawable.tax, R.drawable.lipstick, R.drawable.id, R.drawable.house, R.drawable.garden, R.drawable.fish, R.drawable.fan, R.drawable.email, R.drawable.dog, R.drawable.car, R.drawable.cake, R.drawable.buy, R.drawable.cat, R.drawable.company};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_icon, container, false);
        GridLayout grid = (GridLayout) view.findViewById(R.id.gl_choose_icon);
        int childCount = grid.getChildCount();

        for (int i= 0; i < childCount; i++){
            ImageView currentImage = (ImageView) grid.getChildAt(i);
            final int finalI = i;
            currentImage.setOnClickListener(new ImageView.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ChooseIconFragment.this.getActivity(), AddCategoryDialog.class);
                    intent.putExtra("iconIntValue", iconImages[finalI]);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                    getDialog().dismiss();
                }
            });
        }
        return view;
    }
}
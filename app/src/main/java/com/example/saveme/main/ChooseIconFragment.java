package com.example.saveme.main;

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

public class ChooseIconFragment extends DialogFragment {

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
            currentImage.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    getDialog().dismiss();
                }
            });
        }
        return view;
    }
}
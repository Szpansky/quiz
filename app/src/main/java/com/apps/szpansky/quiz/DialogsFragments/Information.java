package com.apps.szpansky.quiz.DialogsFragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.apps.szpansky.quiz.R;


/**
 * This class show simple text in dialog that's inserting in argument via newInstance
 */

public class Information extends DialogFragment {


    public static Information newInstance(String text){
        Information information = new Information();

        information.setStyle(STYLE_NO_TITLE,0);
        Bundle bundle = new Bundle();
        bundle.putString("text",text);
        information.setArguments(bundle);

        return information;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_information,null);

        Button close = view.findViewById(R.id.close);
        TextView textView = view.findViewById(R.id.text);
        textView.setText(getArguments().getString("text"));

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if(activity instanceof DialogInterface.OnDismissListener){
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}

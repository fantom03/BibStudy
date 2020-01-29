package com.example.biblicalstudies;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ProgressDialog extends Dialog {

    public ProgressBar progressBar2;
    public TextView percentage;
    private ProgressBar progressBar1;
    private TextView textView;


    public ProgressDialog(@NonNull Context context, boolean indeterminate) {
        super(context);
        setContentView(R.layout.progress_dialog);

        progressBar1 = findViewById(R.id.progress_dialog_bar1);
        progressBar2 = findViewById(R.id.progress_dialog_bar2);
        percentage = findViewById(R.id.progress_dialog_percentage);
        textView = findViewById(R.id.progress_dialog_text);

        if(indeterminate) {
            progressBar1.setVisibility(View.VISIBLE);
            progressBar2.setVisibility(View.GONE);
            percentage.setVisibility(View.GONE);
        }
        else {
            progressBar1.setVisibility(View.GONE);
            progressBar2.setVisibility(View.VISIBLE);
            percentage.setVisibility(View.VISIBLE);
        }
    }

    public void setMessage(String message){
        textView.setText(message);
    }

}

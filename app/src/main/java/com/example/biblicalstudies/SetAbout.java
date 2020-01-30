package com.example.biblicalstudies;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetAbout extends AppCompatActivity {

    private static DatabaseReference dbRef;

    static{
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_page_dialog);

        final CardView btn1 = findViewById(R.id.about_cancel);
        final CardView btn2 = findViewById(R.id.about_confirm);

        final EditText text = findViewById(R.id.about_edit_text);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt = text.getText().toString();
                if(txt==null || txt.isEmpty()) return;
                dbRef.child("about").child("text").setValue(txt);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

    }
}

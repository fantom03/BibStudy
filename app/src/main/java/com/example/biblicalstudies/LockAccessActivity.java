package com.example.biblicalstudies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class LockAccessActivity extends AppCompatActivity {

    private static DatabaseReference dbRef;
    private static List<HashMap<String, Object>> lock;
    private static String uid;
    private static String lockId;
    private static Integer index;
    private static RadioGroup radioGroup;
    private static Long indexAnswer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_access);

        Intent intent = getIntent();
        uid = intent.getStringExtra("UID");
        lockId = intent.getStringExtra("LOCK_ID");

        dbRef = FirebaseDatabase.getInstance().getReference();
        index = 0;

        DatabaseReference locRef = dbRef.child("lock");
        locRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    lock = (List<HashMap<String, Object>>) dataSnapshot.child(lockId).getValue();
                    createQuizMania();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createQuizMania() {

        final LinearLayout layout = findViewById(R.id.lock_access_layout);
        radioGroup = findViewById(R.id.option_radio_group);
        final Button submit = findViewById(R.id.ans_submit);


        final HashMap<String, Object> lk = giveMeNext();
        setParameters(lk.get("question"),lk.get("op1"), lk.get("op2"),lk.get("op3"),lk.get("op4"));
        indexAnswer = (Long)lk.get("correctAnswer");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lk==null) return;
                if(radioGroup.getCheckedRadioButtonId()==-1) {
                    Toast.makeText(LockAccessActivity.this, "Choose an option", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(radioGroup.getCheckedRadioButtonId()==
                        (radioGroup.getChildAt(Integer.parseInt(String.valueOf(indexAnswer)))).getId()){
                    final HashMap<String, Object> lk1 = giveMeNext();
                    if(lk1==null) {
                        unlock();
                        return;
                    }
                    layout.animate().scaleX(-5f).alpha(0).setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            setParameters(lk1.get("question"),lk1.get("op1"), lk1.get("op2"),lk1.get("op3"),lk1.get("op4"));
                            indexAnswer = (Long)lk1.get("correctAnswer");
                            layout.animate().scaleX(1).alpha(1f).setInterpolator(new AccelerateDecelerateInterpolator())
                                    .setDuration(500);
                            submit.animate().rotationX(360).setInterpolator(new AccelerateDecelerateInterpolator())
                                    .setDuration(1000);
                        }
                    });

                }else{
                    Toast.makeText(LockAccessActivity.this, "Wrong Answer! Try next time", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }

    private void unlock() {
        DatabaseReference user = dbRef.child("accounts");
        user.child(uid).child(lockId).setValue(true);
        Toast.makeText(this, "Unlocked Successfully!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, HomeDefault.class);
        finishAffinity();
        startActivity(intent);
    }

    private void setParameters(Object ques, Object op1, Object op2, Object op3, Object op4){
        radioGroup.clearCheck();
        TextView textView = findViewById(R.id.question_text_view);
        RadioButton btn1 = findViewById(R.id.ans_btn1);
        RadioButton btn2 = findViewById(R.id.ans_btn2);
        RadioButton btn3 = findViewById(R.id.ans_btn3);
        RadioButton btn4 = findViewById(R.id.ans_btn4);

        textView.setText((CharSequence) ques);
        btn1.setText((CharSequence) op1);
        btn2.setText((CharSequence) op2);
        btn3.setText((CharSequence) op3);
        btn4.setText((CharSequence) op4);

    }

    private HashMap<String, Object> giveMeNext(){

        if(index<lock.size()){
            return lock.get(index++);
        }
        return null;
    }

}

package com.example.biblicalstudies;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class LockAcitivity extends AppCompatActivity {

    private final static ArrayList<Lock> lockInitiation = new ArrayList<>();
    private static LinearLayout lockLayout;
    private static RadioGroup radioGroup;
    private static EditText question;
    private static EditText option1;
    private static EditText option2;
    private static EditText option3;
    private static EditText option4;
    private static Button addBtn;
    private static Button saveBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_activity);

        lockLayout = findViewById(R.id.lock_layout);
        question = findViewById(R.id.question_lock);
        option1 = findViewById(R.id.option1_lock);
        option2 = findViewById(R.id.option2_lock);
        option3 = findViewById(R.id.option3_lock);
        option4 = findViewById(R.id.option4_lock);
        addBtn = findViewById(R.id.btn_add_lock);
        saveBtn = findViewById(R.id.btn_save_lock);
        radioGroup = findViewById(R.id.radio_group_lock);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNullOrEmpty()){
                    Toast.makeText(LockAcitivity.this, "Some or All Parameters are empty", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder alert = new AlertDialog.Builder(LockAcitivity.this);
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            addData();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.setMessage("Are you sure you want to add more?").create();
                    alert.show();
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lockInitiation.isEmpty())
                    Toast.makeText(LockAcitivity.this, "Quiz not added yet", Toast.LENGTH_SHORT).show();
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(LockAcitivity.this);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.putExtra("Lock", lockInitiation);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.setMessage("Are you sure you want to initiate lock?").create();
                    builder.show();
                }
            }
        });
    }

    private void addData() {
        final String ques = question.getText().toString();
        final String op1 = option1.getText().toString();
        final String op2 = option2.getText().toString();
        final String op3 = option3.getText().toString();
        final String op4 = option4.getText().toString();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                for (int i = 0; i < rg.getChildCount(); i++) {
                    RadioButton btn = (RadioButton) rg.getChildAt(i);
                    if (btn.getId() == checkedId) {
                        lockInitiation.add(new Lock(ques, i, op1, op2, op3, op4));
                        break;
                    }
                }
            }
        });
        lockLayout.animate().translationX(-100f).alpha(0f).setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                clearAll();
                lockLayout.animate().translationX(0).setInterpolator(new AccelerateDecelerateInterpolator())
                        .setDuration(1).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        lockLayout.animate().alpha(1f).setInterpolator(new AccelerateDecelerateInterpolator())
                                .setDuration(300);
                    }
                });

            }
        });
    }

    private void clearAll() {
        question.getText().clear();
        option1.getText().clear();
        option2.getText().clear();
        option3.getText().clear();
        option4.getText().clear();
        radioGroup.clearCheck();
    }

    private boolean isNullOrEmpty(){
        return question.getText().toString().isEmpty()||
                option1.getText().toString().isEmpty()||
                option2.getText().toString().isEmpty()||
                option3.getText().toString().isEmpty()||
                option4.getText().toString().isEmpty()||
                radioGroup.getCheckedRadioButtonId()==-1;
    }
}

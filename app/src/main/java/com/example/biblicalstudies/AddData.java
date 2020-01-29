package com.example.biblicalstudies;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class AddData extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private static final int READ_REQ_CODE_DOCS = 1000;
    private static final int READ_REQ_CODE_AUDIO = 1001;
    private static final int READ_REQ_CODE_LOCK = 1;

    private static final String FILE_TYPE = "Choose File Type...";
    private static final String AUDIO_TRACK = "Choose Track...";
    private static final String DOCUMENT = "Choose Docs...";
    private static final String LESSON = "Choose Lessons...";

    private static String languageType;

    private static Spinner spinnerMain;
    private static Spinner spinnerLAV;
    private static Spinner spinnerAudio;
    private static Spinner spinnerDoc;
    private static EditText editLessons;
    private static CheckBox spinnerBox;
    private static Switch switchLesson;
    private static Switch switchDoc;
    private static Button switchBtn;
    private static RadioGroup langInput;
    private static Button delBtn;
    private static Button btn1;
    private static Button btn2;
    private static Button btn3;

    private static RecyclerView spinnerRecyclerView;
    private static ArrayList<FileObject> mUploadList;
    private static ArrayList<FileObject> mStoreList;
    private static ArrayList<FileObject> mDoneList;
    private static ArrayList<Integer> mProgressList;
    private static UploadAdapter uploadAdapter;

    private static ArrayList<String> mLessonData;
    private static ArrayList<String> mDocumentData;
    private static ArrayList<String> mAudioData;

    private static ArrayList<Lock> lockArrayList;

    private final StorageReference mRef = FirebaseStorage.getInstance().getReference();
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adddata_activity);

        spinnerMain = findViewById(R.id.spinner_main);
        spinnerMain.setAdapter(new ArrayAdapter<>(this,
                R.layout.spinner_layout, new String[]{FILE_TYPE, "Lessons",
                "Audios", "Videos"}));

        spinnerMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=0){
                    if(i==1) {
                        dealWithLessons();
                    }
                    else if(i==2) {
                        dealWithAudios();
                    }
                    else dealWithVideos();
                }else if(i==0){
                    disableAll();
                    final LinearLayout spinnerLayout = findViewById(R.id.spinner_layout);
                    PropertyValuesHolder rotationY = PropertyValuesHolder.ofFloat("rotationY", -360);
                    PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1);
                    final ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(spinnerLayout,
                            rotationY, alpha);
                    anim.setInterpolator(new AccelerateDecelerateInterpolator());
                    anim.setDuration(800);
                    anim.start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerLAV = findViewById(R.id.spinner_lesson_audio);
        spinnerAudio = findViewById(R.id.spinner_audio);
        spinnerDoc = findViewById(R.id.spinner_document);
        editLessons = findViewById(R.id.edit_text_lesson);
        spinnerBox = findViewById(R.id.spinner_checkBox);
        switchLesson = findViewById(R.id.switch1);
        switchDoc = findViewById(R.id.switch2);
        switchBtn = findViewById(R.id.switch_btn);
        delBtn = findViewById(R.id.adddata_list_view_delete_btn);
        btn1 = findViewById(R.id.spinner_btn1);
        btn2 = findViewById(R.id.spinner_btn2);
        btn3 = findViewById(R.id.spinner_btn3);

        langInput = findViewById(R.id.language_input);

        spinnerRecyclerView = findViewById(R.id.spinner_recycler_view);
        mUploadList = new ArrayList<>();
        mDoneList = new ArrayList<>();
        mStoreList = new ArrayList<>();
        mProgressList = new ArrayList<>();

        mLessonData = new ArrayList<>();
        mAudioData = new ArrayList<>();
        mDocumentData = new ArrayList<>();

        uploadAdapter = new UploadAdapter(mUploadList,mStoreList, mDoneList,mProgressList);
        spinnerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        spinnerRecyclerView.setHasFixedSize(true);
        spinnerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        spinnerRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        spinnerRecyclerView.setAdapter(uploadAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(spinnerRecyclerView);

        lockArrayList = new ArrayList<>();
    }

    private void dealWithVideos() {
        disableAll();
    }

    private void dealWithAudios() {
        disableAll();
        final LinearLayout spinnerLayout = findViewById(R.id.spinner_layout);
        PropertyValuesHolder rotationY = PropertyValuesHolder.ofFloat("rotationY", 360);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0);
        final ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(spinnerLayout,
                rotationY, alpha);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(800);
        anim.start();

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                spinnerLayout.animate().alpha(1).setDuration(500);
                spinnerBox.setVisibility(View.VISIBLE);
                switchLesson.setVisibility(View.VISIBLE);
                switchBtn.setVisibility(View.VISIBLE);
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                btn3.setVisibility(View.VISIBLE);
                spinnerLAV.setVisibility(View.GONE);
                spinnerDoc.setVisibility(View.GONE);
                switchDoc.setVisibility(View.GONE);
                langInput.setVisibility(View.VISIBLE);
                spinnerAudio.setVisibility(View.VISIBLE);
                spinnerRecyclerView.setVisibility(View.VISIBLE);
                delBtn.setVisibility(View.VISIBLE);
                spinnerBox.animate().setStartDelay(300).alpha(1f)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                spinnerAudio.animate().setStartDelay(300).alpha(1f)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                switchBtn.animate().setStartDelay(400).alpha(1f)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                switchLesson.animate().setStartDelay(400).alpha(1f)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                btn1.animate().setStartDelay(500).alpha(1f)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                btn2.animate().setStartDelay(500).alpha(1f)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                btn3.animate().setStartDelay(600).alpha(1f)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        switchLesson.setText("Audio");

        spinnerBox.setEnabled(false);
        spinnerBox.setChecked(true);

        mAudioData.add(AUDIO_TRACK);
        final ArrayAdapter<String> adapterAudio = new ArrayAdapter<>(this, R.layout.spinner_layout, mAudioData);
        spinnerAudio.setAdapter(adapterAudio);

        DatabaseReference audRef = dbRef.child("audios");
        audRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        String str = snap.getValue(ModelAudioData.class).getTitle();
                        if(!mAudioData.contains(str))mAudioData.add(str);
                        adapterAudio.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(langInput.getCheckedRadioButtonId()==-1){
                    Toast.makeText(AddData.this, "Language not selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mUploadList.isEmpty() && !mStoreList.isEmpty()){
                    Toast.makeText(AddData.this, "Clear the list before adding", Toast.LENGTH_SHORT).show();
                    vibrate(100);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("audio/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Document"), READ_REQ_CODE_AUDIO);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spinnerAudio.getSelectedItem().equals(AUDIO_TRACK)){
                    Toast.makeText(AddData.this, "No track selected.", Toast.LENGTH_SHORT).show();
                    return;
                }
                delete("audios", spinnerAudio.getSelectedItem().toString());
                mAudioData.remove(spinnerAudio.getSelectedItem().toString());
                adapterAudio.notifyDataSetChanged();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUploadList.isEmpty()){
                    Toast.makeText(AddData.this, "There is nothing to upload.", Toast.LENGTH_SHORT).show();
                }else{
                    for(int i=0; i<mUploadList.size(); i++){
                        upload("audios", mUploadList.get(i).name, mUploadList.get(i).uri, i, switchLesson.isChecked(), "",
                                switchDoc.isChecked(), languageType);
                    }
                }
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearList();
            }
        });

        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!switchLesson.isChecked()&&!switchDoc.isChecked()) {
                    Toast.makeText(AddData.this, "Lock switches not activated", Toast.LENGTH_SHORT).show();
                    vibrate(200);
                    return;
                }
                Intent intent = new Intent(AddData.this, LockAcitivity.class);
                startActivityForResult(intent, READ_REQ_CODE_LOCK);
            }
        });

        langInput.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton btn = ((RadioButton)radioGroup.getChildAt(i-1));
                if(btn!=null)languageType = btn.getText().toString();
            }
        });

    }

    private void dealWithLessons() {

        spinnerAudio.setVisibility(View.GONE);
        final LinearLayout spinnerLayout = findViewById(R.id.spinner_layout);
        PropertyValuesHolder rotationY = PropertyValuesHolder.ofFloat("rotationY", 360);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0);
        final ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(spinnerLayout,
                rotationY, alpha);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(800);
        anim.start();

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                spinnerLayout.animate().alpha(1).setDuration(500);
                spinnerDoc.setVisibility(View.INVISIBLE);
                spinnerBox.setVisibility(View.VISIBLE);
                switchLesson.setVisibility(View.VISIBLE);
                switchDoc.setVisibility(View.INVISIBLE);
                switchBtn.setVisibility(View.VISIBLE);
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                btn3.setVisibility(View.VISIBLE);
                langInput.setVisibility(View.VISIBLE);
                btn2.setEnabled(false);
                editLessons.setVisibility(View.VISIBLE);
                spinnerRecyclerView.setVisibility(View.VISIBLE);
                delBtn.setVisibility(View.VISIBLE);
                editLessons.animate().setStartDelay(200).alpha(1f)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                spinnerBox.animate().setStartDelay(300).alpha(1f)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                switchBtn.animate().setStartDelay(400).alpha(1f)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                switchLesson.animate().setStartDelay(400).alpha(1f)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                btn1.animate().setStartDelay(500).alpha(1f)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                btn2.animate().setStartDelay(500).alpha(1f)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                btn3.animate().setStartDelay(600).alpha(1f)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        spinnerBox.setChecked(false);
        spinnerBox.setEnabled(true);
        switchLesson.setText("Lesson");

        spinnerBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    btn2.setEnabled(true);
                    editLessons.animate().alpha(0).setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(200).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            editLessons.setVisibility(View.GONE);
                            spinnerLAV.setVisibility(View.VISIBLE);
                            spinnerLAV.animate().alpha(1f)
                                    .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400);
                        }
                    });
                    spinnerDoc.animate().withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            spinnerDoc.setVisibility(View.VISIBLE);
                        }
                    }).alpha(1f)
                            .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400);
                    switchDoc.animate().withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            switchDoc.setVisibility(View.VISIBLE);
                        }
                    }).alpha(1f)
                            .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400);
                }else{
                    btn2.setEnabled(false);
                    spinnerLAV.animate().alpha(0).setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(200).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            spinnerLAV.setVisibility(View.GONE);
                            editLessons.setVisibility(View.VISIBLE);
                            editLessons.animate().alpha(1f)
                                    .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400);
                            spinnerDoc.animate().alpha(0f)
                                    .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    spinnerDoc.setVisibility(View.INVISIBLE);
                                }
                            });

                            switchDoc.animate().alpha(0f)
                                    .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    switchDoc.setVisibility(View.INVISIBLE);
                                }
                            });

                        }
                    });
                }
            }
        });

        switchLesson.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    switchDoc.setEnabled(false);
                }else switchDoc.setEnabled(true);
            }
        });

        switchDoc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) switchLesson.setEnabled(false);
                else switchLesson.setEnabled(true);
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(langInput.getCheckedRadioButtonId()==-1){
                    Toast.makeText(AddData.this, "Language not selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mUploadList.isEmpty() && !mStoreList.isEmpty()){
                    Toast.makeText(AddData.this, "Clear the list before adding", Toast.LENGTH_SHORT).show();
                    vibrate(100);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Document"), READ_REQ_CODE_DOCS);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lessonName = null;
                if(editLessons.getVisibility()==View.VISIBLE){
                    if(editLessons.getText()==null||editLessons.getText().toString().isEmpty()) {
                        Toast.makeText(AddData.this, "Lesson name not provided", Toast.LENGTH_SHORT).show();
                        vibrate(100);
                        return;
                    }
                    else lessonName = editLessons.getText().toString();
                }
                if(spinnerLAV.getVisibility()==View.VISIBLE){
                    if(spinnerLAV.getSelectedItem().equals(DOCUMENT)){
                        Toast.makeText(AddData.this, "Select a lesson", Toast.LENGTH_SHORT).show();
                        vibrate(100);
                        return;
                    }else lessonName = (String)spinnerLAV.getSelectedItem();
                }
                if(mUploadList.isEmpty()){
                    Toast.makeText(AddData.this, "There is nothing to upload.", Toast.LENGTH_SHORT).show();
                }else{
                    for(int i=0; i<mUploadList.size(); i++){
                        upload("lessons", mUploadList.get(i).name, mUploadList.get(i).uri, i, switchLesson.isChecked(), lessonName,
                                switchDoc.isChecked(), languageType);
                    }
                }
            }
        });

        mLessonData.add(LESSON);
        spinnerLAV.setAdapter(new ArrayAdapter<>(AddData.this, R.layout.spinner_layout, mLessonData));
        DatabaseReference ref = dbRef.child("lessons");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snap : dataSnapshot.getChildren()){
                        String name = snap.getValue(ModelLessonData.class).getName();
                        if(!mLessonData.contains(name))mLessonData.add(name);
                    }((ArrayAdapter)spinnerLAV.getAdapter()).notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDocumentData.add(DOCUMENT);
        final ArrayAdapter documentAdapter = new ArrayAdapter<>(AddData.this,
                R.layout.spinner_document_layout, mDocumentData);
        spinnerDoc.setAdapter(documentAdapter);
        spinnerLAV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int k, long l) {
                final String name = (String)adapterView.getItemAtPosition(k);
                if(!name.equals(LESSON)){
                    DatabaseReference ref = dbRef.child("lessons");
                    Query query = ref.orderByChild("name").equalTo(name);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Map<String, Boolean> map = null;
                                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    map = snapshot.getValue(ModelLessonData.class).getDocs();
                                }
                                DatabaseReference ref = dbRef.child("docs");
                                if(map==null) return;
                                for(final Map.Entry<String, Boolean> entry : map.entrySet()){
                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                String name = dataSnapshot.child(entry.getKey()).child("name").getValue(String.class);
                                                if(!mDocumentData.contains(name))mDocumentData.add(name);
                                                documentAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    mDocumentData.clear();
                    mDocumentData.add(DOCUMENT);
                    documentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearList();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddData.this);
                alertDialog.setMessage("Delete Lesson or Document").setCancelable(false)
                        .setPositiveButton("Lesson", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(spinnerLAV.getSelectedItem().toString().equals(LESSON)){
                                    Toast.makeText(AddData.this, "Lesson not selected", Toast.LENGTH_SHORT).show();
                                    vibrate(100);
                                    return;
                                }
                                delete("lessons", spinnerLAV.getSelectedItem().toString());
                            }
                        }).setNegativeButton("Document", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(spinnerDoc.getSelectedItem().toString().equals(DOCUMENT)){
                            Toast.makeText(AddData.this, "Document not selected", Toast.LENGTH_SHORT).show();
                            vibrate(100);
                            return;
                        }
                        delete("docs", spinnerDoc.getSelectedItem().toString());
                    }
                }).create().setTitle("Choose");
                alertDialog.show();
            }
        });

        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!switchLesson.isChecked()&&!switchDoc.isChecked()) {
                    Toast.makeText(AddData.this, "Lock switches not activated", Toast.LENGTH_SHORT).show();
                    vibrate(200);
                    return;
                }
                Intent intent = new Intent(AddData.this, LockAcitivity.class);
                startActivityForResult(intent, READ_REQ_CODE_LOCK);
            }
        });

        langInput.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton btn = ((RadioButton)radioGroup.getChildAt(i-1));
                if(btn!=null)languageType = btn.getText().toString();
            }
        });

    }

    private void clearList() {
        lockArrayList.clear();
        mUploadList.clear();
        mStoreList.clear();
        mDoneList.clear();
        mProgressList.clear();
        uploadAdapter.notifyDataSetChanged();
        findViewById(R.id.adddata_list_view_lock_view).animate().setStartDelay(200).alpha(0f)
                .setInterpolator(new OvershootInterpolator()).setDuration(500)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.adddata_list_view_lock_view).setVisibility(View.GONE);
                    }
                });
    }

    private void delete(final String type,final String name) {
        clearList();
        if(type.equals("lessons")){
            final DatabaseReference ref = dbRef.child("lessons");
            final Query query = ref.orderByChild("name").equalTo(name);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Map<String, Boolean> map =  dataSnapshot.child(String.valueOf(Objects.hash(name)))
                                .getValue(ModelLessonData.class).getDocs();
                        dataSnapshot.child(String.valueOf(Objects.hash(name))).getRef().removeValue();
                        mLessonData.remove(name);
                        ((ArrayAdapter) spinnerLAV.getAdapter()).notifyDataSetChanged();
                        if(map == null) return;
                        final DatabaseReference docRef = dbRef.child("docs");
                        for (final Map.Entry<String, Boolean> entry : map.entrySet()) {
                            ValueEventListener listener1 = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                    ModelDocumentData data = dataSnapshot.getChildren().iterator().next()
                                            .getValue(ModelDocumentData.class);
                                    mRef.child(data.getPath()).delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    dataSnapshot.child(entry.getKey()).getRef().removeValue();
                                                }
                                            });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            };
                            docRef.addListenerForSingleValueEvent(listener1);
                            docRef.removeEventListener(listener1);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }else if(type.equals("docs")){

            DatabaseReference docRef = dbRef.child("docs");
            Query query = docRef.orderByChild("name").equalTo(name);
            mDocumentData.remove(name);
            mRef.child("documents/"+name).delete();
            ((ArrayAdapter)spinnerDoc.getAdapter()).notifyDataSetChanged();
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        final String key = dataSnapshot.getChildren().iterator().next().getKey();
                        DatabaseReference lesRef = dbRef.child("lessons");
                        Query query1 = lesRef.orderByChild("docs/"+key);
                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    if(snapshot.getChildren().iterator().next().child("docs").child(key).exists()){
                                        snapshot.getChildren().iterator().next().child("docs").child(key).getRef().removeValue();
                                        dataSnapshot.child(key).getRef().removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else if(type.equals("audios")){
            DatabaseReference docRef = dbRef.child("audios");
            Query query = docRef.orderByChild("title").equalTo(name);
            mRef.child("audio/"+name).delete();
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                        dataSnapshot.getChildren().iterator().next().getRef().removeValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{

        }
    }

    private void disableAll() {

        spinnerLAV.setVisibility(View.GONE);
        spinnerAudio.setVisibility(View.GONE);
        spinnerDoc.setVisibility(View.GONE);
        editLessons.setVisibility(View.GONE);
        spinnerBox.setVisibility(View.GONE);
        switchLesson.setVisibility(View.GONE);
        switchDoc.setVisibility(View.GONE);
        switchBtn.setVisibility(View.GONE);
        delBtn.setVisibility(View.GONE);
        btn1.setVisibility(View.GONE);
        btn2.setVisibility(View.GONE);
        btn3.setVisibility(View.GONE);
        spinnerRecyclerView.setVisibility(View.GONE);
        langInput.clearCheck();
        langInput.setVisibility(View.GONE);

    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        startActivity(new Intent(this, HomeDefault.class));
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==READ_REQ_CODE_DOCS && resultCode==RESULT_OK && data!=null){
            if(data.getClipData()!=null){
                int length = data.getClipData().getItemCount();
                for(int i=0; i<length; i++){
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    String name = getFileName(uri);
                    FileObject obj = new FileObject(name, uri);
                    if(!mStoreList.contains(obj)){
                        mStoreList.add(obj);
                        mUploadList.add(obj);
                        mProgressList.add(i, 1);
                        uploadAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(this, "File already present", Toast.LENGTH_SHORT).show();
                    }
                }
            }else if(data.getData()!=null){
                Uri uri = data.getData();
                String name = getFileName(uri);
                FileObject obj = new FileObject(name, uri);
                if(!mStoreList.contains(obj)){
                    mStoreList.add(obj);
                    mUploadList.add(obj);
                    mProgressList.add(0, 1);
                    uploadAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(this, "File already present", Toast.LENGTH_SHORT).show();
                }
            }else Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }else if(requestCode==READ_REQ_CODE_LOCK && resultCode==RESULT_OK && data!=null){
            lockArrayList = (ArrayList<Lock>) data.getExtras().getSerializable("Lock");
            TextView textView = findViewById(R.id.adddata_list_view_lock_view);
            textView.setVisibility(View.VISIBLE);
            textView.animate().setStartDelay(1000).rotationY(-360).alpha(1f).setInterpolator(new OvershootInterpolator())
                    .setDuration(500);
        }else if(requestCode==READ_REQ_CODE_AUDIO && resultCode==RESULT_OK && data!=null){
            if(data.getClipData()!=null){
                int length = data.getClipData().getItemCount();
                for(int i=0; i<length; i++){
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    String name = getFileName(uri);
                    FileObject obj = new FileObject(name, uri);
                    if(!mStoreList.contains(obj)){
                        mStoreList.add(obj);
                        mUploadList.add(obj);
                        mProgressList.add(i, 1);
                        uploadAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(this, "File already present", Toast.LENGTH_SHORT).show();
                    }
                }

            }else if(data.getData()!=null){
                Uri uri = data.getData();
                String name = getFileName(uri);
                FileObject obj = new FileObject(name, uri);
                if(!mStoreList.contains(obj)){
                    mStoreList.add(obj);
                    mUploadList.add(obj);
                    mProgressList.add(0, 1);
                    uploadAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(this, "File already present", Toast.LENGTH_SHORT).show();
                }
            }else Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }
    }

    private void upload(final String type, final String name, final Uri uri, final int index,
                        final boolean checked, final String lessonName, final boolean switchDocChecked, final String langType) {
        if(mUploadList.isEmpty()) return;
        if((switchDocChecked || checked) && lockArrayList.isEmpty()){
            Toast.makeText(this, "Lock not set", Toast.LENGTH_SHORT).show();
            vibrate(200);
            return;
        }if(lessonName.equals(LESSON)||lessonName.equals(AUDIO_TRACK)||lessonName.equals(DOCUMENT)){
            Toast.makeText(this, "Selection not made.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(type.equals("lessons")){
            DatabaseReference ref = dbRef.child("docs");
            Query query = ref.orderByChild("name").equalTo(name);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()) {
                        final StorageReference storeData1 = mRef.child("documents/"+name);
                        storeData1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if(mUploadList.isEmpty()) return;
                                if(!mDoneList.isEmpty())mDoneList.remove(0);
                                mDoneList.add(mUploadList.remove(0));
                                if(!mProgressList.isEmpty()){
                                    mProgressList.remove(index);
                                    mProgressList.add(index, Integer.MAX_VALUE);
                                }
                                Uri url = taskSnapshot.getUploadSessionUri();
                                uploadAdapter.notifyDataSetChanged();
                                addToDatabase(type, name, taskSnapshot.getMetadata().getPath(), System.currentTimeMillis(),
                                        checked, lessonName, switchDocChecked, langType, url);
                                Toast.makeText(AddData.this, name+" uploaded.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddData.this, name+" failed to upload. "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                int progress = (int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                if(!mProgressList.isEmpty()){
                                    mProgressList.remove(index);
                                    mProgressList.add(index, progress);
                                }
                                uploadAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else if(type.equals("audios")){
            DatabaseReference ref = dbRef.child("audios");
            Query query = ref.orderByChild("title").equalTo(name);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()) {
                        final StorageReference storeData1 = mRef.child("audio/"+name);
                        storeData1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if(mUploadList.isEmpty()) return;
                                if(!mDoneList.isEmpty())mDoneList.remove(0);
                                mDoneList.add(mUploadList.remove(0));
                                if(!mProgressList.isEmpty()){
                                    mProgressList.remove(index);
                                    mProgressList.add(index, Integer.MAX_VALUE);
                                }
                                Uri url = taskSnapshot.getUploadSessionUri();
                                uploadAdapter.notifyDataSetChanged();
                                addToDatabase(type, name, taskSnapshot.getMetadata().getPath(), System.currentTimeMillis(),
                                        checked, lessonName, switchDocChecked, langType, url);
                                Toast.makeText(AddData.this, name+" uploaded.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(AddData.this, name+" failed to upload. "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                int progress = (int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                if(!mProgressList.isEmpty()){
                                    mProgressList.remove(index);
                                    mProgressList.add(index, progress);
                                }
                                uploadAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{

        }
    }

    private void addToDatabase(String type, String name, String path, long timeStamp,
                               boolean checked, String lessonName, boolean switchDocChecked, String langType, Uri url){
        if(type.equals("lessons")){
            DatabaseReference refDoc = dbRef.child("docs"), refLesson = dbRef.child(type), refLock = dbRef.child("lock");
            if(checked){
                String lockId = String.valueOf(Objects.hash(lockArrayList.toString()));
                refLock.child(lockId).setValue(lockArrayList);
                String id1 = refDoc.push().getKey();
                refDoc.child(id1).child("name").setValue(name);
                refDoc.child(id1).child("path").setValue(path);
                refDoc.child(id1).child("timeStamp").setValue(timeStamp);
                refDoc.child(id1).child("isLocked").setValue(false);
                refDoc.child(id1).child("lockId").setValue(""+Integer.MAX_VALUE);
                refDoc.child(id1).child("language").setValue(langType);

                String id2 = String.valueOf(Objects.hash(lessonName));
                refLesson.child(id2).child("name").setValue(lessonName);
                refLesson.child(id2).child("timeStamp").setValue(timeStamp);
                refLesson.child(id2).child("isLocked").setValue(true);
                refLesson.child(id2).child("docs").child(id1).setValue(true);
                refLesson.child(id2).child("lockId").setValue(lockId);

            }else if(switchDocChecked){
                String lockId = String.valueOf(Objects.hash(lockArrayList.toString()));
                refLock.child(lockId).setValue(lockArrayList);
                String id1 = refDoc.push().getKey();
                refDoc.child(id1).child("name").setValue(name);
                refDoc.child(id1).child("path").setValue(path);
                refDoc.child(id1).child("timeStamp").setValue(timeStamp);
                refDoc.child(id1).child("isLocked").setValue(true);
                refDoc.child(id1).child("lockId").setValue(lockId);
                refDoc.child(id1).child("language").setValue(langType);

                String id2 = String.valueOf(Objects.hash(lessonName));
                refLesson.child(id2).child("name").setValue(lessonName);
                refLesson.child(id2).child("timeStamp").setValue(timeStamp);
                refLesson.child(id2).child("isLocked").setValue(false);
                refLesson.child(id2).child("docs").child(id1).setValue(true);
                refLesson.child(id2).child("lockId").setValue(""+Integer.MAX_VALUE);
            }else{
                String id1 = refDoc.push().getKey();
                refDoc.child(id1).child("name").setValue(name);
                refDoc.child(id1).child("path").setValue(path);
                refDoc.child(id1).child("timeStamp").setValue(timeStamp);
                refDoc.child(id1).child("isLocked").setValue(false);
                refDoc.child(id1).child("lockId").setValue(""+Integer.MAX_VALUE);
                refDoc.child(id1).child("language").setValue(langType);

                String id2 = String.valueOf(Objects.hash(lessonName));
                refLesson.child(id2).child("name").setValue(lessonName);
                refLesson.child(id2).child("timeStamp").setValue(timeStamp);
                refLesson.child(id2).child("isLocked").setValue(false);
                refLesson.child(id2).child("docs").child(id1).setValue(true);
                refLesson.child(id2).child("lockId").setValue(""+Integer.MAX_VALUE);

            }
        }
        else if(type.equals("audios")){
            if(checked){
                DatabaseReference ref = dbRef.child("audios"), refLock=dbRef.child("lock");
                String lockId = String.valueOf(Objects.hash(lockArrayList.toString()));
                refLock.child(lockId).setValue(lockArrayList);
                String audId = ref.push().getKey();
                ref.child(audId).child("title").setValue(name);
                ref.child(audId).child("path").setValue(path);
                ref.child(audId).child("timestamp").setValue(timeStamp);
                ref.child(audId).child("lockId").setValue(lockId);
                ref.child(audId).child("isLocked").setValue(true);
                ref.child(audId).child("language").setValue(langType);
            }else{
                DatabaseReference ref = dbRef.child("audios");
                String audId = ref.push().getKey();
                String lockId = ""+Integer.MAX_VALUE;
                ref.child(audId).child("title").setValue(name);
                ref.child(audId).child("path").setValue(path);
                ref.child(audId).child("timestamp").setValue(timeStamp);
                ref.child(audId).child("lockId").setValue(lockId);
                ref.child(audId).child("isLocked").setValue(false);
                ref.child(audId).child("language").setValue(langType);
            }
        }
    }

    private void vibrate(int mill){
        ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(mill);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof UploadAdapter.UploadViewHolder){

            final Integer index = viewHolder.getAdapterPosition();
            final FileObject obj = mStoreList.get(viewHolder.getAdapterPosition());

            uploadAdapter.itemRemoved(index);

            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(),"Item removed from list", Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadAdapter.itemRestored(index, obj);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    }
}

class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.UploadViewHolder>{

    private static ArrayList<FileObject> mUploadList;
    private static ArrayList<FileObject> mStoreList;
    private static ArrayList<FileObject> mDoneList;
    private static ArrayList<Integer> mProgressList;

    public UploadAdapter(ArrayList<FileObject> mUploadList, ArrayList<FileObject> mStoreList,
                         ArrayList<FileObject> mDoneList, ArrayList<Integer> mProgressList) {
        this.mProgressList = mProgressList;
        this.mStoreList = mStoreList;
        this.mDoneList = mDoneList;
        this.mUploadList = mUploadList;
    }

    @NonNull
    @Override
    public UploadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adddata_activity_list_view, parent, false);
        return new UploadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UploadViewHolder holder, int position) {
        holder.textView.setText(mStoreList.get(position).name);
        if(mProgressList.get(position)!=Integer.MAX_VALUE){
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            ObjectAnimator.ofInt(holder.progressBar, "progress",
                    holder.progressBar.getProgress(), mProgressList.get(position))
                    .setDuration(500).start();
        }else{
            holder.progressBar.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.animate().rotationY(360).setDuration(500);
        }
    }

    @Override
    public int getItemCount() {
        return mStoreList.size();
    }

    public void itemRemoved(int index){
        FileObject obj = mStoreList.remove(index);
        if(mUploadList.contains(obj)) mUploadList.remove(obj);
        notifyItemRemoved(index);
    }

    public void itemRestored(int index, Object obj){
        mStoreList.add(index,(FileObject) obj);
        if(!mDoneList.contains(obj)) mUploadList.add(index, (FileObject) obj);
        notifyItemInserted(index);
    }

    public static class UploadViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;
        public ImageView imageView;
        public ProgressBar progressBar;
        public RelativeLayout viewBackground, viewForeground;

        public UploadViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.adddata_list_view_text_view);
            progressBar = itemView.findViewById(R.id.adddata_list_view_progress_view);
            imageView = itemView.findViewById(R.id.adddata_list_view_image_view);
            viewBackground = itemView.findViewById(R.id.item_list_background);
            viewForeground = itemView.findViewById(R.id.item_list_foreground);
        }
    }
}

class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback{

    private RecyclerItemTouchHelperListener listener;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView = ((UploadAdapter.UploadViewHolder) viewHolder).viewForeground;

            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((UploadAdapter.UploadViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((UploadAdapter.UploadViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((UploadAdapter.UploadViewHolder) viewHolder).viewForeground;

        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    interface RecyclerItemTouchHelperListener{
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}

class FileObject{
    String name;
    Uri uri;

    public FileObject(String name, Uri uri) {
        this.name = name;
        this.uri = uri;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof FileObject){
            return this.name.equals(((FileObject) obj).name);
        }
        return false;
    }
}

package com.example.biblicalstudies;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class AddData extends AppCompatActivity {

    private static final int READ_REQ_CODE_DOCS = 1000;
    private static final int READ_REQ_CODE_AUDIO = 1001;

    private static final String FILE_TYPE = "Choose File Type...";
    private static final String AUDIO_TRACK = "Choose Track...";
    private static final String DOCUMENT = "Choose Docs...";
    private static final String LESSON = "Choose Lessons...";
    private static Spinner spinnerMain;
    private static int heightOfScreen;
    private static boolean isAnimationRunning = false;
    private static Spinner spinnerLAV;
    private static Spinner spinnerDoc;
    private static EditText editLessons;
    private static CheckBox spinnerBox;
    private static Switch switchLesson;
    private static Switch switchDoc;
    private static Button switchBtn;
    private static Button btn1;
    private static Button btn2;
    private static Button btn3;
    private static RecyclerView spinnerRecyclerView;
    private static ArrayList<FileObject> mUploadList;
    private static ArrayList<String> mDoneList;
    private static UploadAdapter uploadAdapter;
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
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        heightOfScreen = metrics.heightPixels;

        spinnerMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=0 && !isAnimationRunning){
                    if(i==1) dealWithLessons();
                    else if(i==2) dealWithAudios();
                    else dealWithVideos();
                }else if(i==0 && isAnimationRunning){
                    disableAll();
                    isAnimationRunning = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerLAV = findViewById(R.id.spinner_lesson_audio);
        spinnerDoc = findViewById(R.id.spinner_document);
        editLessons = findViewById(R.id.edit_text_lesson);
        spinnerBox = findViewById(R.id.spinner_checkBox);
        switchLesson = findViewById(R.id.switch1);
        switchDoc = findViewById(R.id.switch2);
        switchBtn = findViewById(R.id.switch_btn);
        btn1 = findViewById(R.id.spinner_btn1);
        btn2 = findViewById(R.id.spinner_btn2);
        btn3 = findViewById(R.id.spinner_btn3);

        spinnerRecyclerView = findViewById(R.id.spinner_recycler_view);
        mUploadList = new ArrayList<>();
        mDoneList = new ArrayList<>();

        uploadAdapter = new UploadAdapter(mUploadList, mDoneList);
        spinnerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        spinnerRecyclerView.setHasFixedSize(true);
        spinnerRecyclerView.setAdapter(uploadAdapter);


    }

    private void dealWithVideos() {
    }

    private void dealWithAudios() {
    }

    private void dealWithLessons() {

        isAnimationRunning = true;
        final int top = (heightOfScreen/2-(int)(0.45*heightOfScreen));
        final LinearLayout spinnerLayout = findViewById(R.id.spinner_layout);

        spinnerDoc.setVisibility(View.INVISIBLE);
        spinnerBox.setVisibility(View.VISIBLE);
        switchLesson.setVisibility(View.VISIBLE);
        switchDoc.setVisibility(View.INVISIBLE);
        switchBtn.setVisibility(View.VISIBLE);
        btn1.setVisibility(View.VISIBLE);
        btn2.setVisibility(View.VISIBLE);
        btn3.setVisibility(View.VISIBLE);
        btn2.setEnabled(false);
        editLessons.setVisibility(View.VISIBLE);
        spinnerRecyclerView.setVisibility(View.VISIBLE);

        final ObjectAnimator anim = ObjectAnimator.ofFloat(spinnerLayout, "translationY",
                -top);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(800);
        anim.start();

        editLessons.animate().setStartDelay(400).alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
        spinnerBox.animate().setStartDelay(800).alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
        switchBtn.animate().setStartDelay(1200).alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
        switchLesson.animate().setStartDelay(1200).alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
        btn1.animate().setStartDelay(1600).alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
        btn2.animate().setStartDelay(1800).alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
        btn3.animate().setStartDelay(2000).alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);

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
                        upload("lessons", mUploadList.get(i).name, mUploadList.get(i).uri, 0, switchLesson.isChecked(), lessonName);
                    }
                }
            }
        });

        DatabaseReference ref = dbRef.child("lessons");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<String> list = new ArrayList<>();
                    for(DataSnapshot snap : dataSnapshot.getChildren()){
                        list.add(snap.getValue(ModelLessonData.class).getName());
                    }String[] str = new String[list.size()+1];
                    str[0] = LESSON;
                    for (int i = 0; i < list.size(); i++) str[i+1] = list.get(i);
                    spinnerLAV.setAdapter(new ArrayAdapter<>(AddData.this, R.layout.spinner_layout, str));
                    list.clear();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        spinnerLAV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int k, long l) {
                final String name = (String)adapterView.getItemAtPosition(k);
                if(!name.equals(LESSON)){
                    DatabaseReference ref = dbRef.child("lessons");
                    Query query = ref.orderByChild("name").equalTo(name);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Map<String, Boolean> map = null;
                                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    map = snapshot.getValue(ModelLessonData.class).getDocs();
                                }
                                final ArrayList<String> list = new ArrayList<>();
                                DatabaseReference ref = dbRef.child("docs");
                                for(final Map.Entry<String, Boolean> entry : map.entrySet()){
                                    ref.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            list.add(dataSnapshot.child(entry.getKey()).child("name").getValue(String.class));
                                            String str[] = new String[list.size()+1];
                                            str[0] = DOCUMENT;
                                            for (int i = 0; i < list.size(); i++)
                                                str[i+1] = list.get(i);
                                            spinnerDoc.setAdapter(new ArrayAdapter<>(AddData.this, R.layout.spinner_document_layout, str));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }list.clear();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    String[] str = new String[1];
                    str[0] = DOCUMENT;
                    spinnerDoc.setAdapter(new ArrayAdapter<>(AddData.this, R.layout.spinner_document_layout, str));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void disableAll() {



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
                    mUploadList.add(new FileObject(name, uri));
                    mDoneList.add("uploading");
                    uploadAdapter.notifyDataSetChanged();
                }

            }else if(data.getData()!=null){
                Uri uri = data.getData();
                String name = getFileName(uri);
                mUploadList.add(new FileObject(name, uri));
                mDoneList.add("uploading");
                uploadAdapter.notifyDataSetChanged();
            }
        }
    }

    private void upload(final String type, final String name, final Uri uri, final int index, final boolean checked, final String lessonName) {
        if(type.equals("lessons")){
            StorageReference storeData = mRef.child("documents");
            storeData.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDoneList.remove(index);
                    mDoneList.add(index, "done");
                    mUploadList.remove(index);
                    uploadAdapter.notifyDataSetChanged();
                    addToDatabase(type, name, taskSnapshot.getMetadata().getPath(), System.currentTimeMillis(), checked, lessonName);
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

                }
            });
        }else if(type.equals("audios")){

        }else{

        }
    }

    private void addToDatabase(String type, String name, String path, long timeStamp, boolean checked, String lessonName){
        if(type.equals("lessons")){
            DatabaseReference refDoc = dbRef.child("docs"), refLesson = dbRef.child(type);
            String id1 = refDoc.push().getKey();
            refDoc.child(id1).child("name").setValue(name);
            refDoc.child(id1).child("path").setValue(path);
            refDoc.child(id1).child("timeStamp").setValue(timeStamp);
            refDoc.child(id1).child("isLocked").setValue(checked);

            String id2 = String.valueOf(Objects.hash(lessonName));
            refLesson.child(id2).child("name").setValue(lessonName);
            refLesson.child(id2).child("timeStamp").setValue(timeStamp);
            refLesson.child(id2).child("isLocked").setValue(checked);
            refLesson.child(id2).child("docs").child(id1).setValue(true);
        }
    }

    private void vibrate(int mill){
        ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(mill);
    }
}

class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.UploadViewHolder>{

    private ArrayList<FileObject> mUploadList;
    private ArrayList<String> mDoneList;

    public UploadAdapter(ArrayList<FileObject> mUploadList, ArrayList<String> mDoneList) {
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
    public void onBindViewHolder(@NonNull UploadViewHolder holder, int position) {
        TextView textView = holder.itemView.findViewById(R.id.adddata_list_view_text_view);
        textView.setText(mUploadList.get(position).name);
    }

    @Override
    public int getItemCount() {
        return mUploadList.size();
    }

    public static class UploadViewHolder extends RecyclerView.ViewHolder{

        public UploadViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

class FileObject{
    String name;
    Uri uri;

    public FileObject(String name, Uri uri) {
        this.name = name;
        this.uri = uri;
    }
}
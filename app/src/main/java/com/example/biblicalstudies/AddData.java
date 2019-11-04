package com.example.biblicalstudies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddData extends AppCompatActivity {

    private static final int READ_REQ_CODE_DOCS = 2926;
    private final String FILETYPE = "Choose File Type...";
    private CheckBox checkBox;
    private Spinner fileType;
    private Spinner lessons;
    private Spinner item;
    private EditText editText;
    private Button addBtn, delBtn;
    private TextView textView, countView;
    private RelativeLayout blackScreen;
    private ProgressBar progressBar;
    private TextView progressPercentage;
    private String lessonText;

    private LinkedHashMap<String, Uri> list = new LinkedHashMap<>();
    private ArrayList<String> fileName = new ArrayList<>();

    private StorageReference reference;
    private DatabaseReference dbReference;

    private HashMap<String, Object> map = new HashMap<>();
    private String lessonName;
    private List<HashMap<String, Object>> docs = new ArrayList<>();
    private long uploadedTimeStamp;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adddata_activity);

        checkBox = findViewById(R.id.add_checkBox);
        fileType = findViewById(R.id.add_filetype);
        lessons = findViewById(R.id.add_lessons);
        item = findViewById(R.id.add_item);
        editText = findViewById(R.id.add_edittext);
        addBtn = findViewById(R.id.add_btn);
        delBtn = findViewById(R.id.delete_btn);
        textView = findViewById(R.id.add_result);
        countView = findViewById(R.id.count_view);

        blackScreen = findViewById(R.id.black_view);
        progressBar = findViewById(R.id.upload_progress);
        progressPercentage = findViewById(R.id.upload_percentage);

        dbReference = FirebaseDatabase.getInstance().getReference();

        reference = FirebaseStorage.getInstance().getReference();

        textView.setMovementMethod(new ScrollingMovementMethod());

        String[] fileTypeItems = new String[]{FILETYPE,"Lessons", "Documents", "Audios", "Videos"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_layout, fileTypeItems);
        fileType.setAdapter(adapter);

        fileType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String str = fileType.getSelectedItem().toString();
                switch (str){
                    case "Lessons": lessonsPart();
                        break;
                    case "Documents": documentPart();
                        break;
                    case "Audios": audioPart();
                        break;
                    case "Videos": videoPart();
                        break;
                        default:
                            disableAll();
                            break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                disableAll();
            }
        });



    }

    private void videoPart() {
        disableAll();
    }

    private void audioPart() {
        disableAll();
    }

    private void documentPart() {
        disableAll();
    }

    private void lessonsPart() {
        disableAll();
        checkBox.setEnabled(true);
        addBtn.setEnabled(true);
        delBtn.setEnabled(true);
        lessons.setVisibility(View.VISIBLE);
        item.setVisibility(View.VISIBLE);
        item.setBackgroundResource(R.drawable.spinner_document);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    lessons.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.GONE);
                }else{
                    editText.setVisibility(View.VISIBLE);
                    lessons.setVisibility(View.GONE);
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFileChooses();
            }
        });

        addBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                upload();
                return true;
            }
        });

        delBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!list.isEmpty()){
                    Toast.makeText(AddData.this, "File Removed", Toast.LENGTH_SHORT).show();
                    list.remove(fileName.get(fileName.size()-1));
                    fileName.remove(fileName.get(fileName.size()-1));
                    countView.setText("File Count: "+fileName.size());
                    setTextView();
                }
                else{
                    Toast.makeText(AddData.this, "No File Present", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

    }

    private void performFileChooses() {
        Intent in = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        in.addCategory(Intent.CATEGORY_OPENABLE);
        in.setType("application/pdf");
        startActivityForResult(in, READ_REQ_CODE_DOCS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==READ_REQ_CODE_DOCS && data!=null) {
            Uri uri = data.getData();
            list.put(getFileName(uri), uri);
            createFileName();
        }
    }

    public void createFileName(){
        for(Map.Entry<String, Uri> entry: list.entrySet()){
            if(!fileName.contains(entry.getKey()))
                fileName.add(entry.getKey());
        }
        setTextView();
    }

    public void setTextView(){
        String str = "";
        for(String s:fileName)
            str += s +"\n";
        textView.setText(str);
        countView.setText("File Count: "+fileName.size());
    }


    private void upload(){

        for(final LinkedHashMap.Entry<String, Uri> entry: list.entrySet()){
            StorageReference childRef = reference.child("pdf/"+entry.getKey());
            childRef.putFile(entry.getValue()).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progressPercentage.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    blackScreen.setVisibility(View.VISIBLE);
                    int calculate = (int)(100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressBar.setProgress(calculate);
                    progressPercentage.setText("Uploading.. "+calculate+"%");
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        fileName.remove(fileName.size()-1);
                        setTextView();
                        Toast.makeText(AddData.this, entry.getKey()+" Successfully Uploaded!", Toast.LENGTH_SHORT).show();
                        StorageMetadata metadata = task.getResult().getMetadata();
                        addToDatabase("lessons",metadata);
                        progressPercentage.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        blackScreen.setVisibility(View.GONE);
                    }else{
                        Toast.makeText(AddData.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        progressPercentage.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        blackScreen.setVisibility(View.GONE);
                    }
                }
            });
        }
        list.clear();
    }

    private void addToDatabase(String item,StorageMetadata metadata) {

        DatabaseReference ref = dbReference.getDatabase().getReference(item);
        lessonName = (View.VISIBLE==editText.getVisibility())?editText.getText().toString():
                lessons.getSelectedItem().toString();
        DatabaseReference ref1 = dbReference.getDatabase().getReference("docs");
        String docId = ref1.push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put("name", metadata.getName());
        map.put("path", metadata.getPath());
        map.put("metadata", metadata);
        map.put("timeStamp", System.currentTimeMillis());
        ref1.child(docId).setValue(map);
        String lessonId = ref.push().getKey();
        map.clear();
        map.put("name", lessonName);
        map.put("docId", docId);
        map.put("timeStamp", System.currentTimeMillis());
        ref.child(docId).setValue(map);
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

    private void disableAll(){
        progressPercentage.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        blackScreen.setVisibility(View.GONE);
        checkBox.setEnabled(false);
        lessons.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        item.setVisibility(View.GONE);
        addBtn.setEnabled(false);
        delBtn.setEnabled(false);
        textView.setText(null);
    }
}

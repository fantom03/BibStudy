package com.example.biblicalstudies;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocFragment extends DialogFragment {

    private final List<ModelDocumentData> documents;
    private static DocFragment fragment;
    private final Map<String, Boolean> map;
    private static String language;
    private String title;
    private DocRecyclerAdapter recyclerAdapter;

    private DocFragment(String title, final Map<String, Boolean> map){
        this.title = title;
        documents = new ArrayList<>();
        this.map = map;
    }

    public static DocFragment getInstance(String title, Map<String, Boolean> map, String lang){
        if(fragment!=null) fragment.dismiss();
        language = lang;
        return fragment = new DocFragment(title, map);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.doc_popup, container,false);

        RecyclerView recyclerView = view.findViewById(R.id.popup_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter = new DocRecyclerAdapter(view.getContext(), documents, this);
        recyclerView.setAdapter(recyclerAdapter);

        final ProgressBar progressBar = view.findViewById(R.id.loading_progress_popup);
        progressBar.setVisibility(View.VISIBLE);


        DatabaseReference docRef = FirebaseDatabase.getInstance().getReference().child("docs");
        docRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(progressBar.isShown()) progressBar.setVisibility(View.GONE);
                if(map==null) return;
                for(Map.Entry<String, Boolean> entry : map.entrySet()){
                    if(dataSnapshot.hasChild(entry.getKey())){
                        ModelDocumentData data = dataSnapshot.child(entry.getKey()).getValue(ModelDocumentData.class);
                        if(data.getLanguage().equals(language)) documents.add(data);
                        recyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if(progressBar.isShown()) progressBar.setVisibility(View.GONE);
                Toast.makeText(view.getContext(),databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView tv = view.findViewById(R.id.popup_text_view);
        tv.setText(this.title.replace(" ", "\n"));

        view.findViewById(R.id.popup_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

}

class DocRecyclerAdapter extends RecyclerView.Adapter<DocRecyclerAdapter.DocViewHolder> {

    private static List<ModelDocumentData> docList;
    private static Context context;
    private static DocFragment fragment;

    public DocRecyclerAdapter(Context cm, List<ModelDocumentData> data, DocFragment docFragment) {
        this.docList = data;
        context = cm;
        fragment = docFragment;
    }

    @NonNull
    @Override
    public DocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doc_list_view, parent, false);
        return new DocViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DocViewHolder holder, int position) {
        String str = docList.get(position).getName().split("(.pdf)$")[0];
        holder.textView.setText(getCamelCase(str));
        holder.isLocked = docList.get(position).getIsLocked();
        holder.lockId = docList.get(position).getLockId();

        if(holder.isLocked){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("accounts");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(FirebaseAuth.getInstance().getCurrentUser()!=null &&
                            dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("locks").child(holder.lockId).exists()){
                        holder.lock.setVisibility(View.GONE);
                        holder.share.setVisibility(View.VISIBLE);
                        holder.isLocked = false;
                    }else{
                        holder.share.setVisibility(View.GONE);
                        holder.lock.setVisibility(View.VISIBLE);
                        holder.isLocked = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private String getCamelCase(String str){
        StringBuilder build = new StringBuilder(str.toLowerCase());
        build.setCharAt(0, Character.toUpperCase(build.charAt(0)));
        return build.toString();
    }

    @Override
    public int getItemCount() {
        return docList.size();
    }

    public static class DocViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener, View.OnLongClickListener {

        TextView textView;

        public ImageButton share;
        public ImageView lock;
        public boolean isLocked;
        public String lockId;

        public DocViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.doc_text_view_title);
            lock = itemView.findViewById(R.id.doc_image_view_lock);
            share = itemView.findViewById(R.id.doc_share);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        final File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+
                                "/BiblicalStudies/docs");
                        dir.mkdir();
                        String name = docList.get(getAdapterPosition()).getName();
                        final File tempFile = new File(dir,"(BS)"+(name.endsWith(".pdf")?name:name+".pdf"));
                        if(tempFile.exists()) {
                            shareFile(tempFile);
                            return;
                        }
                        if(ContextCompat.checkSelfPermission(context.getApplicationContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED)){
                            shareFile(tempFile);
                        }else{
                            ActivityCompat.requestPermissions(fragment.getActivity(),
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            int result = ContextCompat.checkSelfPermission(context.getApplicationContext(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            if(result == 1){
                                shareFile(tempFile);
                            }else{
                                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                private void shareFile(final File file) {
                    final ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(context, true);
                    progressDialog.setMessage("Please Wait");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    String path = docList.get(getAdapterPosition()).getPath();
                    FirebaseStorage.getInstance().getReference()
                            .child(path).getFile(file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("application/pdf");
                                intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                                        context, "com.biblicalstudies.fileprovider",file
                                ));
                                context.startActivity(Intent.createChooser(intent, "Share File.."));
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(context, ""+task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }

        @Override
        public void onClick(final View view) {
            if(this.isLocked){
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    Toast.makeText(view.getContext(), "Sign in to proceed!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(context, LockAccessActivity.class);
                intent.putExtra("UID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("LOCK_ID", this.lockId);
                fragment.dismiss();
                context.startActivity(intent);
            }else{
                StorageReference ref = FirebaseStorage.getInstance().getReference();
                StorageReference doc = ref.child(docList.get(getAdapterPosition()).getPath());
                final ProgressDialog progressDialog = new ProgressDialog(context, true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading");
                progressDialog.show();
                doc.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/pdf");
                        view.getContext().startActivity(Intent.createChooser(intent, "Choose an Application:"));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(view.getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(menuItem.getTitle().equals("Download")){
                try {
                    final File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+
                            "/BiblicalStudies/docs");
                    dir.mkdir();
                    final File tempFile = new File(dir,"(BS)"+docList.get(getAdapterPosition()).getName());
                    if(tempFile.exists()) {
                        Toast.makeText(context, "File present at "+tempFile.getPath(), Toast.LENGTH_LONG).show();
                        return true;
                    }
                    if(ContextCompat.checkSelfPermission(context.getApplicationContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED)){
                        shareFile(tempFile);
                    }else{
                        ActivityCompat.requestPermissions(fragment.getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        int result = ContextCompat.checkSelfPermission(context.getApplicationContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if(result == 1){
                            shareFile(tempFile);
                        }else{
                            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        private void shareFile(final File file) {
            final ProgressDialog dialog = new ProgressDialog(context, false);
            dialog.setMessage("Downloading");
            dialog.setCancelable(false);
            dialog.show();
            String path = docList.get(getAdapterPosition()).getPath();
            FirebaseStorage.getInstance().getReference()
                    .child(path).getFile(file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        dialog.progressBar2.post(new Runnable() {
                            @Override
                            public void run() {
                                ObjectAnimator anim = ObjectAnimator.ofInt(dialog.progressBar2, "progress",
                                        dialog.progressBar2.getProgress(), 100);
                                anim.start();
                                anim.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                });
                            }
                        });
                        dialog.percentage.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.percentage.setText(100+"%");
                            }
                        });
                        Toast.makeText(context, "File downloaded at "+file.getPath(), Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    final int progress = (int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    dialog.progressBar2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ObjectAnimator.ofInt(dialog.progressBar2, "progress",
                                    dialog.progressBar2.getProgress(), progress).start();
                        }
                    }, 100);
                    dialog.percentage.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.percentage.setText(progress+"%");
                        }
                    }, 100);
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem download = contextMenu.add(Menu.NONE,1,1, "Download");
            download.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            return true;
        }
    }

}

package com.example.biblicalstudies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DocFragment extends DialogFragment {

    private RecyclerView recyclerView;
    private final List<ModelDocumentData> documents;
    private final Map<String, Boolean> map;
    private DocRecycler recycler;
    private View view;
    private String title;
    private static DocFragment fragment;
    private String lessonId;
    private ArrayList<Map<String, Boolean>> data;

    private DocFragment(String title, final Map<String, Boolean> map){
        this.title = title;
        this.lessonId = String.valueOf(Objects.hash(title));
        documents = new ArrayList<>();
        this.map = map;

    }

    public static DocFragment getInstance(String title, Map<String, Boolean> map){
        if(fragment!=null){
            fragment.dismiss();
        }
        return fragment = new DocFragment(title, map);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.doc_popup, container,false);

        recyclerView = view.findViewById(R.id.popup_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final ProgressBar progressBar = view.findViewById(R.id.loading_progress_popup);
        progressBar.setVisibility(View.VISIBLE);


        DatabaseReference docRef = FirebaseDatabase.getInstance().getReference().child("docs");
        docRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(progressBar.isShown()) progressBar.setVisibility(View.GONE);
                for(Map.Entry<String, Boolean> entry : map.entrySet()){
                    if(dataSnapshot.hasChild(entry.getKey())){
                        documents.add(dataSnapshot.child(entry.getKey()).getValue(ModelDocumentData.class));
                    }
                }
                recycler = new DocRecycler(getContext(), documents);
                recyclerView.setAdapter(recycler);
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

class DocRecycler extends RecyclerView.Adapter<DocRecycler.DocViewHolder> {

    private static List<ModelDocumentData> data;
    private View view;
    private Context ct;


    public DocRecycler(Context cm, List<ModelDocumentData> data) {
        this.data = data;
        ct = cm;
    }

    @NonNull
    @Override
    public DocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(ct).inflate(R.layout.doc_list_view, parent, false);
        return new DocViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocViewHolder holder, int position) {
        String str = data.get(position).getName().split("(.pdf)$")[0];
        holder.textView.setText(getCamelCase(str));
    }

    private String getCamelCase(String str){
        StringBuilder build = new StringBuilder(str.toLowerCase());
        build.setCharAt(0, Character.toUpperCase(build.charAt(0)));
        return build.toString();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class DocViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener, View.OnLongClickListener {
        TextView textView;


        public DocViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.doc_text_view_title);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(final View view) {
            StorageReference ref = FirebaseStorage.getInstance().getReference();
            StorageReference doc = ref.child(data.get(getAdapterPosition()).getPath());
            final ProgressBar progressBar = view.getRootView().findViewById(R.id.loading_progress_popup);
            progressBar.setVisibility(View.VISIBLE);

            doc.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/pdf");
                    view.getContext().startActivity(Intent.createChooser(intent, "Choose an Application:"));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(view.getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            return true;
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

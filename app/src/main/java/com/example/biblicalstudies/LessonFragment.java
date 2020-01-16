package com.example.biblicalstudies;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LessonFragment extends Fragment {

    private RecyclerView recyclerView;
    private LessonRecycler recycler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.lessons_frag_layout, container, false);
        final List<ModelLessonData> ls = new ArrayList<>();
        final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = root.child("lessons");

        final ProgressBar progressBar = this.getActivity().findViewById(R.id.loading_progress);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.lessons_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(progressBar.isShown()){
                    progressBar.setVisibility(View.GONE);
                }
                if (dataSnapshot.exists()) {
                    for(DataSnapshot snap : dataSnapshot.getChildren()){
                        ModelLessonData data = snap.getValue(ModelLessonData.class);
                        ls.add(data);
                    }
                }
                recycler = new LessonRecycler(LessonFragment.this.getContext(), ls);
                recyclerView.setAdapter(recycler);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if(progressBar.isShown()){
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(view.getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

}

class LessonRecycler extends RecyclerView.Adapter<LessonRecycler.CustomViewHolder> {

    private static List<ModelLessonData> classData;
    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_LOADING = 0;
    private static Context context;

    public LessonRecycler(Context cm, List<ModelLessonData> ls) {
        this.classData = ls;
        context = cm;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(context).inflate(R.layout.lessons_list_view, parent, false);
            return new LessonViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.loading_progress_bar, parent, false);
            return new ProgressViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        ModelLessonData data = classData.get(position);
        holder.textView.setText(data.getName());
    }

    @Override
    public int getItemCount() {
        return classData.size();
    }

    @Override
    public int getItemViewType(int position) {
        try{
            if(classData.get(position)!=null){
                return VIEW_TYPE_ITEM;
            }else return VIEW_TYPE_LOADING;
        }catch (ArrayIndexOutOfBoundsException e){
            return VIEW_TYPE_LOADING;
        }
    }

    public static class LessonViewHolder extends CustomViewHolder implements View.OnClickListener {


        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = itemView.findViewById(R.id.lessons_text_view);
        }

        @Override
        public void onClick(View view) {
            AppCompatActivity activity = (AppCompatActivity) context;
            FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.addToBackStack(null);

            DocFragment frag = DocFragment.getInstance(classData.get(getAdapterPosition()).getName(),
                    classData.get(getAdapterPosition()).getDocs());
            frag.show(transaction, "dialog");


        }
    }

    public static class ProgressViewHolder extends CustomViewHolder{

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}



package com.example.biblicalstudies;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class LessonFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<String> classData = Arrays.asList("King of Kings",
            "Prince of Peace",
            "Lord of lords",
            "John the Baptist",
            "Life on Earth",
            "Royalty in Heaven");
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.lessons_frag_layout, container,false);
        recyclerView = view.findViewById(R.id.lessons_recycler_view);
        LessonRecycler recycler = new LessonRecycler(getContext(), classData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recycler);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}

class LessonRecycler extends RecyclerView.Adapter<LessonRecycler.LessonViewHolder> {

    private List<String> classData;
    private View view;
    private static Context context;
    public LessonRecycler(Context cm, List<String> classData) {
        this.classData = classData;
        context = cm;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(context).inflate(R.layout.lessons_list_view, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        String str = classData.get(position);
        holder.textView.setText(str);
    }

    @Override
    public int getItemCount() {
        return classData.size();
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;
        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = itemView.findViewById(R.id.lessons_text_view);
        }

        @Override
        public void onClick(View view) {
            AppCompatActivity activity = (AppCompatActivity) context;
            FragmentManager fm = activity.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment prev = fm.findFragmentByTag("dialog");
            DialogFragment popup = new DocFragment(textView.getText().toString());
            if(prev!= null){
                fm.popBackStack();
            }else{
                popup.show(fm, "dialog");
            }
        }
    }

}
package com.example.biblicalstudies;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LessonFragment extends Fragment {

    private RecyclerView recyclerView;
    private String[] data;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.lessons_frag_layout, container,false);
        recyclerView = view.findViewById(R.id.lessons_recycler_view);
        LessonRecycler recycler = new LessonRecycler(getContext(), data);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recycler);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new String[]{"","","","","","","",""};
    }
}

class LessonRecycler extends RecyclerView.Adapter<LessonRecycler.LessonViewHolder> {

    private String[] data;
    private View view;
    private Context ct;
    public LessonRecycler(Context cm, String[] data) {
        this.data = data;
        ct = cm;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(ct).inflate(R.layout.lessons_list_view, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        String str = "Lesson: Lesson " + (position+1);
        holder.textView.setText(str);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.lessons_text_view);
        }
    }

}

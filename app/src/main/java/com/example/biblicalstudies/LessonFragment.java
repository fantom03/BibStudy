package com.example.biblicalstudies;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class LessonFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<String> classData = Arrays.asList("King of Kings", "Prince of Peace", "Lord of lords");
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
    private Context ct;
    private Dialog popup;
    public LessonRecycler(Context cm, List<String> classData) {
        this.classData = classData;
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
            final Dialog popup = new Dialog(view.getContext());
            popup.setContentView(R.layout.doc_popup);
            TextView textView1 = popup.findViewById(R.id.popup_text_view);
            textView1.setText(textView.getText().toString());
            Button close = popup.findViewById(R.id.popup_close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popup.dismiss();
                }
            });
            popup.show();
        }
    }

}
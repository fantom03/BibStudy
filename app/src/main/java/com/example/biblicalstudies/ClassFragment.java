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

public class ClassFragment extends Fragment {

    private RecyclerView recyclerView;
    private String[] data;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.class_frag_layout, container,false);
        recyclerView = view.findViewById(R.id.class_recycler_view);
        ClassRecycler recycler = new ClassRecycler(getContext(), data);
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

class ClassRecycler extends RecyclerView.Adapter<ClassRecycler.ClassViewHolder> {

    private String[] data;
    private View view;
    private Context ct;
    public ClassRecycler(Context cm, String[] data) {
        this.data = data;
        ct = cm;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(ct).inflate(R.layout.class_list_view, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        String str = "Class: Day " + (position+1);
        holder.textView.setText(str);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.class_text_view);
        }
    }

}
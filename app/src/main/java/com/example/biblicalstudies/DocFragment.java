package com.example.biblicalstudies;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DocFragment extends DialogFragment {

    private RecyclerView recyclerView;
    private String[] data;
    private View view;
    private String title;
    private static DocFragment fragment;

    private DocFragment(String title){
        this.title = title;
    }

    public static DocFragment getInstance(String title){
        if(fragment!=null){
            fragment.dismiss();
        }
        return fragment = new DocFragment(title);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.doc_popup, container,false);

        recyclerView = view.findViewById(R.id.popup_recycler_view);

        DocRecycler recycler = new DocRecycler(getContext(), data);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(recycler);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tv = view.findViewById(R.id.popup_text_view);
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
        data = new String[10];
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

}

class DocRecycler extends RecyclerView.Adapter<DocRecycler.DocViewHolder> {

    private String[] data;
    private View view;
    private Context ct;
    public DocRecycler(Context cm, String[] data) {
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
        String str = "Document " + (position+1);
        holder.textView.setText(str);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public static class DocViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public DocViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.doc_text_view_title);
        }
    }

}

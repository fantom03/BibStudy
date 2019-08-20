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

public class AudioFragment extends Fragment {

    private RecyclerView recyclerView;
    private String[] data;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.audio_frag_layout, container,false);
        recyclerView = view.findViewById(R.id.audio_recycler_view);
        AudioRecycler recycler = new AudioRecycler(getContext(), data);
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

class AudioRecycler extends RecyclerView.Adapter<AudioRecycler.AudioViewHolder> {

    private String[] data;
    private View view;
    private Context ct;
    public AudioRecycler(Context cm, String[] data) {
        this.data = data;
        ct = cm;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(ct).inflate(R.layout.audio_list_view, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        String str = "Track: " + (position+1);
        holder.textView.setText(str);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.audio_text_view_title);
        }
    }


}

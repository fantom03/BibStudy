package com.example.biblicalstudies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LessonFragment extends Fragment {

    private static LessonRecyclerAdapter lessonAdapter;

    private static ArrayList<ModelLessonData> listOfLessonData;
    private static DatabaseReference dbRef;

    private static String language;

    public LessonFragment(String language){
        this.language = language;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.lessons_frag_layout, container, false);

        dbRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = dbRef.child("lessons");

        final ProgressDialog dialog = new ProgressDialog(view.getContext(), true);
        dialog.setMessage("Loading");
        dialog.setCancelable(false);
        dialog.show();

        RecyclerView recyclerView = view.findViewById(R.id.lessons_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listOfLessonData = new ArrayList<>();
        lessonAdapter = new LessonRecyclerAdapter(view.getContext(), listOfLessonData, this.language);
        recyclerView.setAdapter(lessonAdapter);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              dialog.dismiss();
                if (dataSnapshot.exists()) {
                    for(DataSnapshot snap : dataSnapshot.getChildren()){
                        ModelLessonData data = snap.getValue(ModelLessonData.class);
                        listOfLessonData.add(data);
                        lessonAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(view.getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

}

class LessonRecyclerAdapter extends RecyclerView.Adapter<LessonRecyclerAdapter.CustomViewHolder> {


    private static DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private static List<ModelLessonData> lessonDataList;
    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_LOADING = 0;
    private static Context context;
    private static String language;

    public LessonRecyclerAdapter(Context cm, List<ModelLessonData> ls, String language) {
        this.language = language;
        this.lessonDataList = ls;
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
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, int position) {
        holder.textView.setText(lessonDataList.get(position).getName());
        holder.isLocked = lessonDataList.get(position).getIsLocked();
        holder.lockId = lessonDataList.get(position).getLockId();
        holder.lock.setBackgroundResource(R.drawable.ic_goto);
        if(holder.isLocked){
            final DatabaseReference users = dbRef.child("accounts");
            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(FirebaseAuth.getInstance().getCurrentUser()!=null &&
                            dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(holder.lockId).exists()){
                        holder.lock.setBackgroundResource(R.drawable.ic_goto);
                        holder.isLocked = false;
                    }else{
                        holder.lock.setBackgroundResource(R.drawable.ic_signin_lock);
                        holder.isLocked = true;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return lessonDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        try{
            if(lessonDataList.get(position)!=null){
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
            lock = itemView.findViewById(R.id.lessons_isLocked);
        }

        @Override
        public void onClick(View view) {
            if (!this.isLocked) {
                lock.setBackgroundResource(R.drawable.ic_goto);
                AppCompatActivity activity = (AppCompatActivity) context;
                FragmentManager manager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                transaction.addToBackStack(null);

                DocFragment frag = DocFragment.getInstance(lessonDataList.get(getAdapterPosition()).getName(),
                        lessonDataList.get(getAdapterPosition()).getDocs(), language);
                frag.show(transaction, "dialog");
            } else {
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    Toast.makeText(view.getContext(), "Sign in to proceed!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(context, LockAccessActivity.class);
                intent.putExtra("UID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("LOCK_ID", this.lockId);
                context.startActivity(intent);
            }
        }
    }

    public static class ProgressViewHolder extends CustomViewHolder{

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;
        public boolean isLocked;
        public String lockId;
        public ImageView lock;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}



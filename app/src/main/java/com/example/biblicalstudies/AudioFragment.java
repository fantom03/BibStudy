package com.example.biblicalstudies;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

    public static class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
    View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener, View.OnLongClickListener {

        private static boolean isPlaying = false, isFinished = false;
        private final TextView textView;
        private final TextView txtView;
        private final Button button;
        private final MediaPlayer player;
        final Runnable mUpdateTime = new Runnable() {
            public void run() {
                int currentDuration;
                if (player.isPlaying()) {
                    currentDuration = player.getCurrentPosition();
                    updatePlayer(txtView, currentDuration);
                    txtView.postDelayed(this, 1000);
                }else {
                    txtView.removeCallbacks(this);
                }
            }
        };
        private final int duration;
        private final ProgressBar progressBar;
        final Runnable mUpdateProgressBar = new Runnable() {
            @Override
            public void run() {
                int currentDuration;
                if(player.isPlaying()){
                    currentDuration = player.getCurrentPosition();
                    progressBar.setProgress(currentDuration);
                    progressBar.postDelayed(this,100);
                }else{
                    progressBar.removeCallbacks(this);
                }
            }
        };
        private final AnimatorSet set;




        public AudioViewHolder(@NonNull final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            textView = itemView.findViewById(R.id.audio_text_view_title);


            set = (AnimatorSet) AnimatorInflater.loadAnimator(itemView.getContext(), R.animator.rotation_repeat);

            txtView = itemView.findViewById(R.id.time_stamp);
            button = itemView.findViewById(R.id.audio_play);

            player = MediaPlayer.create(itemView.getContext(), R.raw.song);
            duration = player.getDuration();

            progressBar = itemView.findViewById(R.id.determinateBar);
            progressBar.setMax(duration);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(player.getCurrentPosition()==player.getDuration())
                        stop(itemView);
                }
            }).start();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AudioViewHolder.this.onClick(itemView);
                }
            });

            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(view.getContext(), "Stopped Playing", Toast.LENGTH_SHORT).show();
                    stop(view);
                    return true;
                }
            });

            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View view) {
            if(!player.isPlaying()){
                play(view);
            }else{
                pause(view);
            }
        }

        private void play(final View view){
            view.findViewById(R.id.audio_play).setBackgroundResource(R.drawable.ic_pause);
            set.setTarget(view.findViewById(R.id.audio_img));
            set.setStartDelay(500);
            set.start();
            player.start();
            txtView.post(mUpdateTime);
            progressBar.post(mUpdateProgressBar);
        }

        private void pause(final View view){
            view.findViewById(R.id.audio_play).setBackgroundResource(R.drawable.ic_audio_play);
            set.pause();
            player.pause();
            textView.post(mUpdateTime);
            progressBar.post(mUpdateProgressBar);
        }

        private void stop(final View view){
            view.findViewById(R.id.audio_play).setBackgroundResource(R.drawable.ic_audio_play);
            set.end();
            player.stop();
            txtView.setText("0:00");
            progressBar.setProgress(0);
        }



        private void updatePlayer(final TextView txtView, int currentDuration){
            txtView.setText("" + milliSecondsToTimer((long) currentDuration));
        }

        /**
         * Function to convert milliseconds time to Timer Format
         * Hours:Minutes:Seconds
         * */
        public  String milliSecondsToTimer(long milliseconds) {
            String finalTimerString = "";
            String secondsString = "";

            // Convert total duration into time
            int hours = (int) (milliseconds / (1000 * 60 * 60));
            int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
            int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
            // Add hours if there
            if (hours > 0) {
                finalTimerString = hours + ":";
            }

            // Prepending 0 to seconds if it is one digit
            if (seconds < 10) {
                secondsString = "0" + seconds;
            } else {
                secondsString = "" + seconds;
            }

            finalTimerString = finalTimerString + minutes + ":" + secondsString;

            // return timer string
            return finalTimerString;
        }


        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
//            switch (menuItem.getTitle().toString()){
//
//            }

            return true;
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem download = contextMenu.add(Menu.NONE,1,1, "Download");
            MenuItem share = contextMenu.add(Menu.NONE, 2, 0, "Share");
            download.setOnMenuItemClickListener(this);
            share.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {

            return true;
        }
    }


}

package com.example.biblicalstudies;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioFragment extends Fragment {

    private static AudioRecyclerAdapter adapter;
    private static List<ModelAudioData> data;
    private static String language;

    public AudioFragment(String string) {
        language = string;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.audio_frag_layout, container,false);
        final RecyclerView recyclerView = view.findViewById(R.id.audio_recycler_view);
        data = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AudioRecyclerAdapter(view.getContext(), data, language, this);
        recyclerView.setAdapter(adapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference audioRef = ref.child("audios");
        audioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()){
                        ModelAudioData audioData = snap.getValue(ModelAudioData.class);
                        if(audioData.getLanguage()!= null &&
                                audioData.getLanguage().equals(language))data.add(audioData);
                    }adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(view.getContext(),databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}

class AudioRecyclerAdapter extends RecyclerView.Adapter<AudioRecyclerAdapter.AudioViewHolder> {

    private static List<ModelAudioData> data;
    private static PendingIntent pendingIntent;
    private static Context context;
    private static String language;
    private static AudioFragment fragment;

    public AudioRecyclerAdapter(Context cm, List<ModelAudioData> data, String lang, AudioFragment audioFragment) {
        this.data = data;
        this.context = cm;
        Intent intent = new Intent(cm, HomeDefault.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(cm,0,intent,0);
        language = lang;
        fragment = audioFragment;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_list_view, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AudioViewHolder holder, int position) {
        String str = data.get(position).getTitle();
        holder.textView.setText(str);
        holder.isLocked = data.get(position).getIsLocked();
        holder.lockId = data.get(position).getLockId();
        holder.txtView.setVisibility(View.VISIBLE);
        holder.txtView.setVisibility(View.VISIBLE);
        holder.txtView.setVisibility(View.VISIBLE);

        if(holder.isLocked){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("accounts");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(FirebaseAuth.getInstance().getCurrentUser()!=null &&
                            dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("locks")
                                    .child(holder.lockId).exists()){
                        holder.lock.setVisibility(View.GONE);
                        holder.txtView.setVisibility(View.VISIBLE);
                        holder.progressBar.setVisibility(View.VISIBLE);
                        holder.playButton.setVisibility(View.VISIBLE);
                        holder.isLocked = false;
                    }else{
                        holder.lock.setVisibility(View.VISIBLE);
                        holder.txtView.setVisibility(View.INVISIBLE);
                        holder.progressBar.setVisibility(View.INVISIBLE);
                        holder.playButton.setVisibility(View.GONE);
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
        return data.size();
    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener,
                        View.OnLongClickListener {

        private static final int NOTIFICATION_ID = 28346;
        private static final String CHANNEL_ID = "NOTIFICATION_CHANNEL_007";

        private static boolean isPaused = false;
        private static AnimatorSet rotateIllusion;
        private final TextView textView;
        private final TextView txtView;
        private final Button playButton;
        private final Handler handler1, handler2;
        private final AnimatorSet blinkIllusion;
        private final ProgressBar progressBar;
        private NotificationManagerCompat notificationManager;
        private MediaPlayer player;

        public ImageView lock;
        public Boolean isLocked;
        public String lockId;

        final Runnable mUpdateTime = new Runnable() {
            public void run() {
                int currentDuration;
                if (player!=null && player.isPlaying()) {
                    currentDuration = player.getCurrentPosition();
                    updatePlayer(txtView, currentDuration);
                    txtView.postDelayed(this, 1000);
                }else {
                    txtView.removeCallbacks(this);
                }
            }
        };
        final Runnable mUpdateProgressBar = new Runnable() {
            @Override
            public void run() {
                int currentDuration;
                if(player!=null && player.isPlaying()){
                    currentDuration = player.getCurrentPosition();
                    progressBar.setProgress(currentDuration);
                    progressBar.postDelayed(this,100);
                }else{
                    progressBar.removeCallbacks(this);
                }
            }
        };
        private NotificationCompat.Builder notificationBuilder;



        public AudioViewHolder(@NonNull final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            lock = itemView.findViewById(R.id.audio_lock);

            textView = itemView.findViewById(R.id.audio_text_view_title);

            txtView = itemView.findViewById(R.id.time_stamp);

            playButton = itemView.findViewById(R.id.audio_play);

            blinkIllusion = (AnimatorSet) AnimatorInflater.loadAnimator(itemView.getContext(), R.animator.blink);

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AudioViewHolder.this.onClick(itemView);
                }
            });
            playButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AudioViewHolder.this.onLongClick(itemView);
                    return true;
                }
            });

            progressBar = itemView.findViewById(R.id.determinateBar);

            handler1 = new Handler();
            handler2 = new Handler();

            itemView.setOnCreateContextMenuListener(this);

            createNotificationChannel();
        }


        private void createNotificationChannel() {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Biblical Studies";
                String description = "Notification for Biblical Studies";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = itemView.getContext().getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }

        @Override
        public void onClick(final View view) {
            if(this.isLocked){
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    Toast.makeText(view.getContext(), "Sign in to proceed!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(context, LockAccessActivity.class);
                intent.putExtra("UID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("LOCK_ID", this.lockId);
                context.startActivity(intent);
            }else{
                String uri = data.get(getAdapterPosition()).getPath();
                player = Player.getInstance(uri);
                player.setLooping(true);
                if(Player.isURISame(uri)){
                    if(isPaused) resume(view);
                    else if(player.isPlaying()) pause(view);
                    else play(view);
                }else {
                    Vibrator vibrator = (Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                    Toast.makeText(view.getContext(), "An audio is already " +
                            "playing. Stop current audio to play another! ", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if(player!=null) stop(view);
            return true;
        }

        private void play(final View view){

            notificationBuilder = new NotificationCompat.Builder(view.getContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo_notification)
            .setContentTitle(data.get(getAdapterPosition()).getTitle())
            .setContentText("Currently Playing...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setContentIntent(pendingIntent);

            notificationManager = NotificationManagerCompat.from(view.getContext());
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

            rotateIllusion = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), R.animator.rotation_repeat);
            rotateIllusion.setTarget(view.findViewById(R.id.audio_img));
            playButton.animate().alpha(0f).setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            playButton.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_pause));
                            playButton.animate().alpha(1f).setInterpolator(new AccelerateDecelerateInterpolator())
                                    .setDuration(400);
                        }
                    }).setDuration(400);
            StorageReference ref = FirebaseStorage.getInstance().getReference();
            StorageReference audRef = ref.child(data.get(getAdapterPosition()).getPath());
            audRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        String url = uri.toString();
                        if(player.isPlaying()) throw new Exception("Multiple instances not allowed.");
                        player.setDataSource(url);
                        player.prepare();
                        progressBar.setMax(player.getDuration());
                        player.start();
                        handler1.post(mUpdateTime);
                        handler2.post(mUpdateProgressBar);
                        rotateIllusion.setStartDelay(100);
                        rotateIllusion.start();

                    } catch (Exception e) {
                        Toast.makeText(view.getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(view.getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void resume(final View view){

            notificationBuilder.setContentText("Currently Playing..");
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

            blinkIllusion.end();
            rotateIllusion.resume();
            playButton.animate().alpha(0f).setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            playButton.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_pause));
                            playButton.animate().alpha(1f).setInterpolator(new AccelerateDecelerateInterpolator())
                                    .setDuration(400);
                        }
                    }).setDuration(400);
            isPaused = false;
            player.start();
            handler1.post(mUpdateTime);
            handler2.post(mUpdateProgressBar);
        }

        private void pause(final View view) {

            notificationBuilder.setContentText("Currently Paused..");
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

            rotateIllusion.pause();
            playButton.animate().alpha(0f).setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            playButton.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_audio_play));
                            blinkIllusion.setTarget(playButton);
                            blinkIllusion.start();
                        }
                    }).setDuration(400);
            isPaused = true;
            player.pause();
        }

        private void stop(final View view){
            if(player!=null){
                notificationManager.cancel(NOTIFICATION_ID);

                blinkIllusion.end();
                rotateIllusion.end();
                playButton.animate().alpha(0f).setInterpolator(new AccelerateDecelerateInterpolator())
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                playButton.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_audio_play));
                                playButton.animate().alpha(1f).setInterpolator(new AccelerateDecelerateInterpolator())
                                        .setDuration(400);
                            }
                        }).setDuration(400);
                isPaused = false;
                handler1.removeCallbacksAndMessages(null);
                handler2.removeCallbacksAndMessages(null);
                txtView.setText("0:00");
                progressBar.setProgress(0);
                player = Player.stopPlayer();
                Toast.makeText(view.getContext(), "Audio Stopped", Toast.LENGTH_SHORT).show();
                Vibrator vibrator = (Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(200);
            }
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
            if(menuItem.getTitle().equals("Share")){
                try {
                    final File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+
                            "/BiblicalStudies/audios");
                    dir.mkdir();
                    final File tempFile = new File(dir,"(BS)"+data.get(getAdapterPosition()).getTitle()+".mp3");
                    if(tempFile.exists()) {
                        shareFile(tempFile);
                        return true;
                    }
                    if(ContextCompat.checkSelfPermission(context.getApplicationContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED)){
                        download(tempFile, true);
                        shareFile(tempFile);
                    }else{
                        ActivityCompat.requestPermissions(fragment.getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        int result = ContextCompat.checkSelfPermission(context.getApplicationContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if(result == 1){
                            download(tempFile, true);
                            shareFile(tempFile);
                        }else{
                            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(menuItem.getTitle().equals("Download")){
                final File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+
                        "/BiblicalStudies/audios");
                dir.mkdir();
                final File tempFile = new File(dir,"(BS)"+data.get(getAdapterPosition()).getTitle()+".mp3");
                if(tempFile.exists()){
                    Toast.makeText(context, "File present at "+tempFile.getPath(), Toast.LENGTH_LONG).show();
                    return true;
                }
                download(tempFile, false);
                return true;
            }
            return true;
        }

        private void download(final File tempFile, final boolean indeterminate) {
            final ProgressDialog progressDialog = new ProgressDialog(context, indeterminate);
            if(indeterminate)progressDialog.setMessage("Please Wait");
            else progressDialog.setMessage("Downloading");
            progressDialog.setCancelable(false);
            progressDialog.show();
            FirebaseStorage.getInstance().getReference().child(data.get(getAdapterPosition()).getPath())
                    .getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    if(!indeterminate){
                        progressDialog.progressBar2.post(new Runnable() {
                            @Override
                            public void run() {
                                ObjectAnimator anim = ObjectAnimator.ofInt(progressDialog.progressBar2,"progress",
                                        progressDialog.progressBar2.getProgress(), 100);
                                anim.start();
                                anim.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Downloaded at "+tempFile.getPath(), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                });
                            }
                        });
                        progressDialog.percentage.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.percentage.setText(100+"%");
                            }
                        });
                    }else{
                        progressDialog.dismiss();
                    }
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    final int progress = (int)(100*taskSnapshot.getBytesTransferred()/
                            taskSnapshot.getTotalByteCount());
                    if(!indeterminate){
                        progressDialog.progressBar2.post(new Runnable() {
                            @Override
                            public void run() {
                                ObjectAnimator.ofInt(progressDialog.progressBar2,"progress",
                                        progressDialog.progressBar2.getProgress(), progress).start();
                            }
                        });
                        progressDialog.percentage.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.percentage.setText(progress+"%");
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(context, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        private void shareFile(final File file){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("audio/*");
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context,
                    "com.biblicalstudies.fileprovider", file));
            context.startActivity(Intent.createChooser(intent, "Share File.."));
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem download = contextMenu.add(Menu.NONE,1,1, "Download");
            MenuItem share = contextMenu.add(Menu.NONE, 2, 0, "Share");
            download.setOnMenuItemClickListener(this);
            share.setOnMenuItemClickListener(this);
        }

    }

    static class Player{
        private static MediaPlayer mediaPlayer;
        private static String uri;

        private Player() {
        }

        public static MediaPlayer getInstance(String uri){
            if(mediaPlayer!=null){

                return mediaPlayer;
            }
            Player.uri = uri;
            return mediaPlayer = new MediaPlayer();
        }

        public static boolean isURISame(String uri){
            return Player.uri.equals(uri);
        }

        public static MediaPlayer stopPlayer(){
            mediaPlayer.stop();
            mediaPlayer.release();
            uri = null;
            return mediaPlayer = null;
        }
    }

}




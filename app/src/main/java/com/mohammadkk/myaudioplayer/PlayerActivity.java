package com.mohammadkk.myaudioplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.mohammadkk.myaudioplayer.databinding.ActivityPlayerBinding;
import com.mohammadkk.myaudioplayer.helper.MusicUtil;
import com.mohammadkk.myaudioplayer.model.Songs;
import com.mohammadkk.myaudioplayer.service.CallBackService;
import com.mohammadkk.myaudioplayer.service.MediaService;

import java.util.ArrayList;

import static com.mohammadkk.myaudioplayer.MainActivity.isFadeActivity;
import static com.mohammadkk.myaudioplayer.MainActivity.isRestartActivity;

public class PlayerActivity extends AppCompatActivity implements CallBackService, ServiceConnection {
    private MediaService mediaService;
    private int currentTime = 0;
    private int totalTime = 0;
    private static int musicIndex = 0;
    private ArrayList<Songs> songsListPlayer = new ArrayList<>();

    private Toolbar actionTop;
    private ShapeableImageView coverMusic;
    private TextView titleMusic, artistMusic, durationPlayedMusic, totalDurationMusic;
    private SeekBar sliderMusic;
    private ImageButton btnPrevMusic, btnNextMusic;
    private FloatingActionButton fabPlayPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViewById();
        if (isFadeActivity) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        musicIndex = getIntent().getIntExtra("positionStart", 0);
        ArrayList<? extends Songs> list = getIntent().getParcelableArrayListExtra("songs_list");
        songsListPlayer.addAll(list);
        actionTop.setNavigationOnClickListener(v -> onBackPressed());
    }
    private void initViewById() {
        actionTop = findViewById(R.id.actionTop);
        coverMusic = findViewById(R.id.coverMusic);
        titleMusic = findViewById(R.id.titleMusic);
        artistMusic = findViewById(R.id.artistMusic);
        sliderMusic = findViewById(R.id.sliderMusic);
        durationPlayedMusic = findViewById(R.id.durationPlayedMusic);
        totalDurationMusic = findViewById(R.id.totalDurationMusic);
        btnPrevMusic = findViewById(R.id.btnPrevMusic);
        fabPlayPause = findViewById(R.id.fabPlayPause);
        btnNextMusic = findViewById(R.id.btnNextMusic);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (isFadeActivity) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent intentService = new Intent(this, MediaService.class);
        ContextCompat.startForegroundService(this, intentService);
        bindService(intentService, this, BIND_AUTO_CREATE);
    }
    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
    }
    private void setMusic() {
        sliderMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTime = progress;
                    durationPlayedMusic.setText(MusicUtil.formatTimeMusic(currentTime));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentTime = seekBar.getProgress();
                mediaService.seekTo(currentTime);
            }
        });
        if (isRestartActivity) {
            playMusic(musicIndex);
            isRestartActivity = false;
        }
        changePrevMusic();
        changeNextMusic();
        changePlayPauseMusic();
    }
    private void playMusic(int pos) {
        try {
            mediaService.reset();
            mediaService.createMediaPlayer(pos);
            mediaService.prepare();
            mediaService.start();
            setDrawableAnimationPlayPause(true);
            metaData(pos);
            mediaService.showNotification(R.drawable.ic_pause);
            musicIndex = pos;
        } catch (Exception e) {
            e.printStackTrace();
        }
        setMusicProgress();
    }
    private void setMusicProgress() {
        currentTime = mediaService.getCurrentPosition();
        totalTime = mediaService.getDuration();
        totalDurationMusic.setText(MusicUtil.formatTimeMusic(totalTime));
        durationPlayedMusic.setText(MusicUtil.formatTimeMusic(currentTime));
        sliderMusic.setMax(totalTime);
        Handler handle = new Handler(Looper.getMainLooper());
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    currentTime = mediaService.getCurrentPosition();
                    durationPlayedMusic.setText(MusicUtil.formatTimeMusic(currentTime));
                    sliderMusic.setProgress(currentTime);
                    handle.postDelayed(this, 1000);
                } catch (IllegalStateException ed) {
                    ed.printStackTrace();
                }
            }
        });
    }
    private void changePrevMusic() {
        btnPrevMusic.setOnClickListener(v -> setPrevMusic());
    }
    private void changeNextMusic() {
        btnNextMusic.setOnClickListener(v -> setNextMusic());
    }
    private void changePlayPauseMusic() {
        fabPlayPause.setOnClickListener(v -> setPlayPauseMusic());
    }
    @Override
    public void setPrevMusic() {
        if (musicIndex > 0)
            musicIndex--;
        else
            musicIndex = songsListPlayer.size() - 1;
        playMusic(musicIndex);
    }
    @Override
    public void setNextMusic() {
        if (musicIndex < songsListPlayer.size() - 1)
            musicIndex++;
        else
            musicIndex = 0;
        playMusic(musicIndex);
    }
    @Override
    public void setPlayPauseMusic() {
        if (mediaService.isPlaying()) {
            mediaService.pause();
            mediaService.showNotification(R.drawable.ic_play);
            setDrawableAnimationPlayPause(false);
        } else {
            mediaService.start();
            mediaService.showNotification(R.drawable.ic_pause);
            setDrawableAnimationPlayPause(true);
        }
    }
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mediaService = ((MediaService.BindService) service).getService();
        mediaService.setCallBackService(this);
        int servicePosition = mediaService.getServicePosition();

        metaData(servicePosition);
        if (mediaService.getMediaList() != songsListPlayer) {
            mediaService.setMediaList(songsListPlayer);
        }
        fabPlayPause.setImageResource((mediaService.isPlaying()) ? R.drawable.ic_pause : R.drawable.ic_play);
        setMusic();
        setMusicProgress();
    }
    @Override
    public void onServiceDisconnected(ComponentName name) {
        mediaService = null;
    }
    private void metaData(int pos){
        titleMusic.setText(songsListPlayer.get(pos).getTitle());
        artistMusic.setText(songsListPlayer.get(pos).getArtist());
        Bitmap cover = MusicUtil.getAlbumCoverByUri(this, Uri.parse(songsListPlayer.get(pos).getAlbumArt()));
        if (cover != null) {
            coverMusic.setImageBitmap(cover);
            coverMusic.setScaleType(ImageView.ScaleType.CENTER_CROP);
            coverMusic.setImageTintList(null);
        } else {
            coverMusic.setImageResource(R.drawable.ic_songs);
            coverMusic.setScaleType(ImageView.ScaleType.FIT_CENTER);
            coverMusic.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.pink_500)));
        }
    }
    private void setDrawableAnimationPlayPause(boolean isPlaying) {
        AnimatedVectorDrawableCompat playAnimCompat;
        AnimatedVectorDrawable playAnim;
        if (isPlaying) {
            fabPlayPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.play_to_pause));
            Drawable drawable = fabPlayPause.getDrawable();
            if (drawable instanceof AnimatedVectorDrawableCompat) {
                playAnimCompat = (AnimatedVectorDrawableCompat) drawable;
                playAnimCompat.start();
            } else if (drawable instanceof AnimatedVectorDrawable) {
                playAnim = (AnimatedVectorDrawable) drawable;
                playAnim.start();
            }
        } else {
            fabPlayPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pause_to_play));
            Drawable drawable = fabPlayPause.getDrawable();
            if (drawable instanceof AnimatedVectorDrawableCompat) {
                playAnimCompat = (AnimatedVectorDrawableCompat) drawable;
                playAnimCompat.start();
            } else if (drawable instanceof AnimatedVectorDrawable) {
                playAnim = (AnimatedVectorDrawable) drawable;
                playAnim.start();
            }
        }
    }
}
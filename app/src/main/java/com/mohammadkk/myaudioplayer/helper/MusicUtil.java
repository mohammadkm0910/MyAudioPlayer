package com.mohammadkk.myaudioplayer.helper;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.MediaStore;

import com.mohammadkk.myaudioplayer.model.Songs;


public class MusicUtil {
    public static final Uri ALBUM_ART = Uri.parse("content://media/external/audio/albumart");
    public static Bitmap getSongCover(Context context, Uri uri) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, uri);
        byte[] cover = mmr.getEmbeddedPicture();
        mmr.release();
        return cover != null ? BitmapFactory.decodeByteArray(cover, 0, cover.length) : null;
    }
    public static void createThread(Threads threads) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread(threads::callback).start();
        } else {
            threads.callback();
        }
    }

    public static Uri getUriPath(Songs song) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.getId());
        } else {
            return Uri.parse(song.getPath());
        }
    }
    public interface Threads {
        void callback();
    }
}

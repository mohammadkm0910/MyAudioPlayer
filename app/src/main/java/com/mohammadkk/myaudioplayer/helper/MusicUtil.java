package com.mohammadkk.myaudioplayer.helper;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.mohammadkk.myaudioplayer.model.Songs;

import java.io.FileDescriptor;
import java.util.Locale;

public class MusicUtil {
    public static final Uri ALBUM_ART = Uri.parse("content://media/external/audio/albumart");
    public static Bitmap getSongCover(Context context, Uri uri) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, uri);
        byte[] cover = mmr.getEmbeddedPicture();
        mmr.release();
        return cover != null ? BitmapFactory.decodeByteArray(cover, 0, cover.length) : null;
    }
    public static String formatTimeMusic(int millis) {
        int h = ((millis / (1000*60*60)) % 24);
        int m = ((millis / (1000*60)) % 60);
        int s = (millis / 1000) % 60;
        if (h > 0) {
            return String.format(Locale.ENGLISH, "%02d:%02d:%02d", h, m, s);
        } else {
            return String.format(Locale.ENGLISH, "%02d:%02d", m, s);
        }
    }
    public static void createThread(Threads threads) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread(threads::callback).start();
        } else {
            threads.callback();
        }
    }
    public static Bitmap getAlbumCoverByUri(Context context, Uri uri) {
        Bitmap cover = null;
        try {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            cover = BitmapFactory.decodeFileDescriptor(fd);
            pfd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cover;
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

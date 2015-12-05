package com.example.weixindemo.manager;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by user on 2015/11/30.
 */
public class MediaManager {
    private static MediaPlayer mMediaPlayer;
    private static boolean isPause;

    public static void playSound(String filaPath, MediaPlayer.OnCompletionListener onCompletionListener)  {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.release();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(filaPath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }catch (IllegalStateException e)
        {
            e.printStackTrace();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void pause()
    {
        if(mMediaPlayer != null && mMediaPlayer.isPlaying())
        {
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    public static void resume()
    {
        if(mMediaPlayer != null)
        {
            if(!mMediaPlayer.isPlaying())
            {
                if(isPause)
                {
                    mMediaPlayer.start();
                }
            }
        }
    }

    public static void release()
    {
        if(mMediaPlayer != null)
        {
            mMediaPlayer .release();
            mMediaPlayer = null;
        }
    }
}

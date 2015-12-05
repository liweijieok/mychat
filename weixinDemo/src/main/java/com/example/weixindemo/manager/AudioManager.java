package com.example.weixindemo.manager;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by user on 2015/11/30.
 */
public class AudioManager {
    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;
    private static AudioManager mInstance;
    private File dir;

    private boolean isPrepare;

    public void deleteAllFile()
    {
        if(dir != null)
        {
            if(dir.exists())
            {
                File[] filelist = dir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.endsWith(".amr");
                    }
                });
                for (int i =0;i<filelist.length;i++)
                {
                    filelist[i].delete();
                }
            }
        }
    }

    private AudioManager(String path) {
        mDir = path;
        dir = new File(mDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }


    private AudioStateListener listener;

    /**
     * 准备好的回调接口
     */
    public interface AudioStateListener {
        void wellPrepare();
    }

    public void setAudioStateListener(AudioStateListener listener) {
        this.listener = listener;
    }


    public static AudioManager getInstance(String path) {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (null == mInstance) {
                    mInstance = new AudioManager(path);
                }
            }
        }
        return mInstance;
    }

    public void prepareAudio() {
        try {
            isPrepare = false;
            String filename = generateFileName();
            File file = new File(dir, filename);
            mCurrentFilePath = file.getAbsolutePath();
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.reset();
            //设置MediaRecorder音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setOutputFile(mCurrentFilePath);
            //设置音频编码
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            //准备结束
            isPrepare = true;
            if (listener != null) {
                listener.wellPrepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成文件的名称
     *
     * @return
     */
    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }


    /**
     * 获取录制文件的文件全路径
     *
     * @return
     */
    public String getFilePath() {
        return mCurrentFilePath;
    }

    public int getVoiceLevel(int maxLevel) {
        try {
            if (isPrepare) {
                //这个值的范围是1-32767之间
                if (mMediaRecorder == null) {
                }
                int level = ((int) (maxLevel * (mMediaRecorder.getMaxAmplitude() * 1.0 / 32768)) + 1);
                return level;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public void release() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    public boolean isNullOfMediaRecorder()
    {
        return null == mMediaRecorder;
    }

    public void cancel() {
        release();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            if (file.exists()) {
                file.delete();
                mCurrentFilePath = null;
            }
        }
    }

}

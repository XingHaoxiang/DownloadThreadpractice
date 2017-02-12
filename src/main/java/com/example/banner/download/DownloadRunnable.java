package com.example.banner.download;

import com.example.banner.download.db.DownloadEntity;
import com.example.banner.download.db.DownloadHelper;
import com.example.banner.download.file.FileStorageManager;
import com.example.banner.download.http.DownloadCallback;
import com.example.banner.download.http.HttpManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Response;

/**
 * Created by Herry on 2017/2/11.
 * 处理多线程下载
 */

public class DownloadRunnable implements Runnable {
    private long mStart;
    private long mEnd;
    private String mUrl;
    private DownloadCallback mCallback;
    private DownloadEntity mEntity;

    public DownloadRunnable(long mStart, long mEnd, String mUrl, DownloadCallback mCallback, DownloadEntity mEntity) {
        this.mStart = mStart;
        this.mEnd = mEnd;
        this.mUrl = mUrl;
        this.mCallback = mCallback;
        this.mEntity = mEntity;
    }

    public DownloadRunnable(long mStart, long mEnd, String mUrl, DownloadCallback mCallback) {
        this.mStart = mStart;
        this.mEnd = mEnd;
        this.mUrl = mUrl;
        this.mCallback = mCallback;
    }

    @Override
    public void run() {
        Response response = HttpManager.getsManager().syncRequestByRange(mUrl, mStart, mEnd);
        if (response == null && mCallback != null) {
            mCallback.fail(HttpManager.NETWORK_ERR_CODE,"网络异常");
            return;
        }
        File file = FileStorageManager.getInstance().getFileByname(mUrl);
        long progress = 0;
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file,"rwd");
            randomAccessFile.seek(mStart);
            byte[] b = new byte[1024 * 500];
            int len;
            InputStream inputStream = response.body().byteStream();
            while ((len = inputStream.read(b, 0, b.length)) != -1) {
                randomAccessFile.write(b,0,len);
                progress += len;
                mEntity.setProgress_position(progress);
            }

            mCallback.success(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            DownloadHelper.getInstance().insert(mEntity);
        }
    }
}

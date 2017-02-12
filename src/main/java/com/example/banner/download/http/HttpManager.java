package com.example.banner.download.http;

import android.content.Context;

import com.example.banner.download.file.FileStorageManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Herry on 2017/2/9.
 */

public class HttpManager {

    public static final int NETWORK_ERR_CODE = 1;
    public static final int COTENT_LENGTH_ERR_CODE = 2;
    public static final int TASK_RUNNING_ERR_CODE = 3;

    private static final HttpManager sManager = new HttpManager();
    private Context mContext;
    private OkHttpClient mOkHttpClient;
    public static HttpManager getsManager(){
        return sManager;
    }

    private HttpManager() {
        mOkHttpClient = new OkHttpClient();
    }

    public void init(Context context) {
        this.mContext = context;
    }

    /**
     * 同步请求
     * @param url
     * @return
     */
    public Response syncRequest(String url) {
        Request request = new Request.Builder().url(url).build();
        try {
            return mOkHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 同步请求
     * @param url
     * @return
     */
    public Response syncRequestByRange(String url,long start,long end) {
        Request request = new Request.Builder().url(url)
                .addHeader("Range", "bytes=" + start + "-" +end)
                .build();
        try {
            return mOkHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final String TAG = "HttpManager";
    public void asyncRequest(final String url, final DownloadCallback downloadCallback) {
        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() && downloadCallback != null) {
                    downloadCallback.fail(NETWORK_ERR_CODE,"下载失败");
                }

                File file = FileStorageManager.getInstance().getFileByname(url);
                byte[] b = new byte[1024 * 500];
                int len;
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                InputStream inputStream = response.body().byteStream();
                while ((len = inputStream.read(b, 0, b.length)) != -1) {
                    fileOutputStream.write(b,0,len);
                    fileOutputStream.flush();
                }

                downloadCallback.success(file);
            }
        });
    }

    public void asyncRequest(String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }
}

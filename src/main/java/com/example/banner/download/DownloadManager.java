package com.example.banner.download;

import com.example.banner.download.Utils.Logger;
import com.example.banner.download.db.DownloadEntity;
import com.example.banner.download.db.DownloadHelper;
import com.example.banner.download.http.DownloadCallback;
import com.example.banner.download.http.HttpManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Herry on 2017/2/10.
 */

public class DownloadManager {

    private static final DownloadManager sManager = new DownloadManager();
    private static final int MAX_THREAD = 2;

    private HashSet<DownloadTask> mHashSet = new HashSet<>();
    private static final ThreadPoolExecutor sThreadPool = new ThreadPoolExecutor(MAX_THREAD, MAX_THREAD, 60, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
        private AtomicInteger mInteger = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "download thread #" + mInteger.getAndIncrement());

            return thread;
        }
    });
    private List<DownloadEntity> mCache;

    private DownloadManager() {

    }

    public static DownloadManager getInstance() {
        return sManager;
    }

    private void finish(DownloadTask task) {
        mHashSet.remove(task);
    }

    public void download(final String url, final DownloadCallback callback) {


        final DownloadTask task = new DownloadTask(url, callback);
        if (mHashSet.contains(task)) {
            callback.fail(HttpManager.TASK_RUNNING_ERR_CODE, "任务已经在执行了");
            return;
        }
        mHashSet.add(task);

        mCache = DownloadHelper.getInstance().getAll(url);
        if (mCache == null || mCache.size() == 0) {


            HttpManager.getsManager().asyncRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    finish(task);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful() && callback != null) {
                        callback.fail(HttpManager.NETWORK_ERR_CODE, "网络异常");
                        return;
                    }

                    long length = response.body().contentLength();
                    if (length == -1) {
                        callback.fail(HttpManager.COTENT_LENGTH_ERR_CODE, "Content length -1");
                        return;
                    }

                    processDownload(url, length, callback,mCache);
                    finish(task);
                }
            });
        } else {
            // TODO: 2017/2/12 处理已经下载过的数据

        }
    }


    private void processDownload(String url, long length, DownloadCallback callback,List<DownloadEntity> cache) {
        Logger.debug("debug", length + " length");
        // 设100kb  现在线程数2 每个50kb 第一个线程0-49 第二个 50-99
        long threadDownloadSize = length / MAX_THREAD;
        if (cache == null || cache.size() == 0) {
            mCache = new ArrayList<>();
        }
        Logger.debug("debug", threadDownloadSize + " threadDownloadSize");
        for (int i = 0; i < MAX_THREAD; i++) {
            Logger.debug("debug", i + "");
            DownloadEntity downloadEntity = new DownloadEntity();
            long startSize = i * threadDownloadSize;
            long endSize;
            if (i == MAX_THREAD - 1) {
                endSize = length - 1;
                Logger.debug("debug", endSize + " if");
            } else {
                endSize = (i + 1) * threadDownloadSize - 1;
                Logger.debug("debug", endSize + " else");
            }

            downloadEntity.setDownload_url(url);
            downloadEntity.setStart_position(startSize);
            downloadEntity.setEnd_position(endSize);
            downloadEntity.setThread_id(i + 1);

            sThreadPool.execute(new DownloadRunnable(startSize, endSize, url, callback,downloadEntity));
        }
    }
}

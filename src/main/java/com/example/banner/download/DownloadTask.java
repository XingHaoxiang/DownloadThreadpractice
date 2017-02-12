package com.example.banner.download;

import com.example.banner.download.http.DownloadCallback;

/**
 * Created by Herry on 2017/2/11.
 */

public class DownloadTask {
    private String mUrl;
    private DownloadCallback mCallback;


    public DownloadTask(String mUrl, DownloadCallback mCallback) {
        this.mUrl = mUrl;
        this.mCallback = mCallback;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public DownloadCallback getCallback() {
        return mCallback;
    }

    public void setCallback(DownloadCallback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownloadTask that = (DownloadTask) o;

        if (!mUrl.equals(that.mUrl)) return false;
        return mCallback.equals(that.mCallback);

    }

    @Override
    public int hashCode() {
        int result = mUrl.hashCode();
        result = 31 * result + mCallback.hashCode();
        return result;
    }

}

package com.example.banner.download.http;

import java.io.File;

/**
 * Created by Herry on 2017/2/9.
 */

public interface DownloadCallback {
    void success(File file);

    void fail(int errCode,String errorMessage);

    void progress(int progress);
}

package com.example.banner.download.file;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.banner.download.Utils.Md5Utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Herry on 2017/2/7.
 */

public class FileStorageManager {
    private static final FileStorageManager sFile = new FileStorageManager();

    private Context mContext;
    private FileStorageManager() {

    }

    public static FileStorageManager getInstance() {
        return sFile;
    }

    public void init(Context context) {
        this.mContext = context;
    }

    public File getFileByname(String url) {
        File parent;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d("mounted", "mounted");
            parent = mContext.getExternalCacheDir();
        } else {
            parent = mContext.getCacheDir();
        }

        String filename = Md5Utils.generateCode(url);
        File file = new File(parent, filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        return file;
    }

}

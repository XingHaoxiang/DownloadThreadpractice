package com.example.banner.download.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by Herry on 2017/2/12.
 */

public class DownloadHelper {
    private static DownloadHelper mHelper = new DownloadHelper();

    private DaoMaster mMaster;
    private DaoSession mSession;
    private DownloadEntityDao mDao;

    public static DownloadHelper getInstance() {
        return mHelper;
    }

    private DownloadHelper() {

    }

    public void init(Context context) {
        SQLiteDatabase db = new DaoMaster.DevOpenHelper(context, "download.db", null).getWritableDatabase();
        mMaster = new DaoMaster(db);
        mSession = mMaster.newSession();
        mDao = mSession.getDownloadEntityDao();

    }

    public void insert(DownloadEntity entity) {
        mDao.insertOrReplace(entity);
    }

    public List<DownloadEntity> getAll(String url) {
        return mDao.queryBuilder().where(DownloadEntityDao.Properties.Download_url.eq(url)).orderAsc(DownloadEntityDao.Properties.Thread_id).list();
    }
}

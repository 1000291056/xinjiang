package com.syhx.network;

import android.text.TextUtils;

import com.syhx.dao.ProgressListener;

/**
 * Created by wufeifei on 2016/11/21.
 */

public class DownLoadBean {
    private String url;
    private String filePath;
    private ProgressListener progressListener;

    public DownLoadBean(String url) {
        this.url = url;
    }

    public String getFileName() {
        String fileName = "";
        String temp = "fileid=";
        if (TextUtils.isEmpty(url)) {
            return fileName;
        }
        if (url.contains(temp)) {
            fileName = url.substring(url.lastIndexOf(temp) + temp.length());
        }

        return fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        return FileUtil.PARENTPARENT + getFileName();
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }
}

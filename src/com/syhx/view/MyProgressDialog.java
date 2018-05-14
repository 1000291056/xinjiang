package com.syhx.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.syhx.dao.ProgressListener;
import com.xjsw.R;

/**
 * Created by wufeifei on 2016/11/18.
 */

public class MyProgressDialog extends ProgressDialog {
    private Handler mHandler = new Handler(Looper.myLooper());
    private ProgressListener mProgressListener = new ProgressListener() {
        @Override
        public void onProgress(final long progress, final long total, boolean done) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    setProgress((int) (progress * 1.0 * 100 / total));
                    if (progress == total) {
                        dismiss();
                    }
                }
            });

        }
    };

    public MyProgressDialog(Context context) {
        super(context);
        init();
    }

    public MyProgressDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public ProgressListener getProgressListener() {
        return mProgressListener;
    }

    private void init() {
        this.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        this.setCancelable(true);// 设置是否可以通过点击Back键取消
        this.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        this.setIcon(R.drawable.logo);// 设置提示的title的图标，默认是没有的
        this.setTitle("请稍后");
        this.setMax(100);

    }
}

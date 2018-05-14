package com.syhx.network;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.syhx.MainActivity;
import com.syhx.PdfActivity;
import com.syhx.dao.CallBack;
import com.syhx.service.DownLoadService;
import com.syhx.view.MyProgressDialog;

import org.apache.cordova.BaseActivity;

import java.io.File;
import java.io.RandomAccessFile;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wufeifei on 2016/11/18.
 */

public class NetWork {
    private Handler mHandler = new Handler(Looper.myLooper());
    private MyProgressDialog mProgressDialog;

    /**
     * 默认弹框
     *
     * @param
     * @param context
     */
    public void downLoad(DownLoadBean downLoadBean, Context context) {
        mProgressDialog = new MyProgressDialog(context);
        downLoad(downLoadBean, context, null);
    }

    /**
     * 下载文件
     *
     * @param
     * @param context
     */
    public void downLoad(final DownLoadBean downLoadBean, final Context context, final CallBack callBack) {
        try {

            File file = new File(downLoadBean.getFilePath());
            if (file.exists() && file.length() > 0) {
                openFile(context, file);
                return;
            }
//            Toast.makeText(context, "开始下载" + downLoadBean.getUrl(), Toast.LENGTH_SHORT).show();
            downLoadBean.setProgressListener(mProgressDialog.getProgressListener());
            if (context instanceof BaseActivity) {
                BaseActivity baseActivity = (BaseActivity) context;
                if (mProgressDialog != null && baseActivity.isNormol()) {
                    mProgressDialog.show();
                }
            }
            RequestParams params = new RequestParams(downLoadBean);
            RetrofitUtil.getRetrofit(context, params).create(DownLoadService.class).downloadFile(downLoadBean.getUrl())
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<ResponseBody, File>() {
                        @Override
                        public File call(ResponseBody responseBody) {
                            String parentParent = FileUtil.PARENTPARENT;
                            String fileName = downLoadBean.getFileName();

                            final File parent = new File(parentParent);
                            Log.i(NetWork.class.getSimpleName(), "可读" + parent.canRead());
                            Log.i(NetWork.class.getSimpleName(), "可写" + parent.canWrite());
                            Log.i(NetWork.class.getSimpleName(), "可执行" + parent.canExecute());
//                            mHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(context, "文件夹可读" + parent.canRead() + "可写" + parent.canWrite() + "可执行" + parent.canExecute(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
                            if (!parent.exists()) {
                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                    parent.mkdirs();
//                                    mHandler.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(context, "文件夹创建" + suc, Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
                                }
                            } else {
//                                mHandler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(context, "文件夹存在", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
                            }
                            final File file = new File(parentParent + fileName);
                            try {
                                if (!file.exists()) {
                                    file.createNewFile();
                                }
//                                mHandler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(context, "开始保存文件" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });

                                byte[] buffer = responseBody.bytes();
                                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                                randomAccessFile.write(buffer);
                            } catch (Exception e) {
                                e.printStackTrace();
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "文件创建失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            return file;
                        }
                    }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<File>() {
                        @Override
                        public void onStart() {
                            super.onStart();
//                            mHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (context instanceof BaseActivity) {
//                                        BaseActivity baseActivity = (BaseActivity) context;
//                                        if (mProgressDialog != null && baseActivity.isNormol()) {
//                                            mProgressDialog.show();
//                                        }
//                                    }
//
//                                }
//                            });

                        }

                        @Override
                        public void onCompleted() {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onNext(File file) {
                            openFile(context, file);
                            if (callBack != null) {
                                callBack.callBack(file);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开文件
     *
     * @param context
     * @param file
     */
    private void openFile(Context context, File file) {
        try {

            Intent intent;
            if (file.getAbsolutePath().contains("pdf")) {
                intent = new Intent(context, PdfActivity.class);
                intent.putExtra("file", file);
            } else {
                intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(file);
                intent.setDataAndType(uri, FileUtil.getMIMEType(file));
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void open(final Context context, final File file) {
        try {
            if (context instanceof BaseActivity) {
                Snackbar.make(((MainActivity) context).getWindow().getDecorView(), "文件已下载，是否打开？", Snackbar.LENGTH_SHORT).setAction("打开", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFile(context, file);
                    }
                }).show();
            } else {
                openFile(context, file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

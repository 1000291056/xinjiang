package com.syhx;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.syhx.dao.CallBack;
import com.syhx.network.DownLoadBean;
import com.syhx.network.FileUtil;
import com.syhx.network.NetWork;
import com.xjsw.R;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by wufeifei on 2016/11/18.
 */

public class PdfActivity extends Activity {
    private PDFView mPDFView;
    private String url = "";
    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_layout);
        mPDFView = (PDFView) findViewById(R.id.pdfView);
        mFile = (File) getIntent().getSerializableExtra("file");

        showPDF();
//        showPdf();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pdf_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_other:
                try {
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri uri = Uri.fromFile(mFile);
                    intent.setDataAndType(uri, FileUtil.getMIMEType(mFile));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPDF() {

        showPDF(mFile);
    }

    private void showPDF(File file) {
        if (file == null) {
            return;
        }
        try {
            long resultSize = getFileSize(file);
            if (resultSize > 0) {
                mPDFView.fromFile(file)
                        .enableSwipe(true)
                        .enableDoubletap(true)
                        .swipeVertical(false)
                        .defaultPage(1)
                        .showMinimap(false)
                        .onError(new OnErrorListener() {
                            @Override
                            public void onError(Throwable t) {
                                onPDFError();
                            }
                        })
                        .load();
            } else {
                onPDFError();
            }
        } catch (Exception ex) {
            onPDFError();
        }
    }

    private void showPdf() {
        new NetWork().downLoad(new DownLoadBean(url), this, new CallBack<File>() {
            @Override
            public void callBack(File file) {

                try {
                    long resultSize = getFileSize(file);
                    if (resultSize > 0) {
                        mPDFView.fromFile(file)
                                .enableSwipe(true)
                                .enableDoubletap(true)
                                .swipeVertical(false)
                                .defaultPage(1)
                                .showMinimap(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        onPDFError();
                                    }
                                })
                                .load();
                    } else {
                        onPDFError();
                    }
                } catch (Exception ex) {
                    onPDFError();
                }
            }
        });
    }

    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
        }
        return size;
    }

    protected void onPDFError() {
        new AlertDialog.Builder(this).setTitle("文件打开失败！").setMessage(null)
                .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }
}

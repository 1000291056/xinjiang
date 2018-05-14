package com.syhx;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by wufeifei on 2016/11/21.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private final String TAG = CrashHandler.class.getSimpleName();
    private static CrashHandler INSTANCE;
    // 系统默认的 UncaughtException 处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;

    public static CrashHandler getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;

        // 获取系统默认的 UncaughtException 处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        // 设置该 CrashHandler 为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(INSTANCE);
    }

    /**
     * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
     *
     * @param ex
     * @return true：如果处理了该异常信息；否则返回 false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
/**
 * 自定义处理
 */
        return true;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.i(TAG, "uncaughtException");
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }

            // 退出程序,注释下面的重启启动程序代码
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            // 重新启动程序，注释上面的退出程序
            Intent intent = new Intent();
            intent.setClass(mContext, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}

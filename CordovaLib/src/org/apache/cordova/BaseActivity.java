package org.apache.cordova;

import android.app.Activity;

/**
 * Created by wufeifei on 2016/11/21.
 */

public class BaseActivity extends Activity {
    private boolean isNormol = false;

    @Override
    protected void onResume() {
        super.onResume();
        isNormol = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isNormol = false;
    }

    public boolean isNormol() {
        return isNormol;
    }
}

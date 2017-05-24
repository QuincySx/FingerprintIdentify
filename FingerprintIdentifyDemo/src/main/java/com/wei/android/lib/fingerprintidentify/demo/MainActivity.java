package com.wei.android.lib.fingerprintidentify.demo;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;

public class MainActivity extends AppCompatActivity {

    private TextView mTvTips;
    private boolean mIsCalledStartIdentify = false;
    private FingerprintIdentify mFingerprintIdentify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvTips = (TextView) findViewById(R.id.mTvTips);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mIsCalledStartIdentify) {
            mTvTips.append("\nresume identify if needed");
            mFingerprintIdentify.resumeIdentify();
            return;
        }

        mIsCalledStartIdentify = true;
        mFingerprintIdentify = new FingerprintIdentify(this, new BaseFingerprint
                .FingerprintIdentifyExceptionListener() {
            @Override
            public void onCatchException(Throwable exception) {
                mTvTips.append("\n" + exception.getLocalizedMessage());
            }
        });
        mTvTips.append("手机品牌:" + Build.BRAND);
        mTvTips.append("\ncreate fingerprintIdentify");
        mTvTips.append("\n硬件支持: " + mFingerprintIdentify.isHardwareEnable());
        mTvTips.append("\n注册指纹: " + mFingerprintIdentify.isRegisteredFingerprint());
        mTvTips.append("\n指纹可用: " + mFingerprintIdentify.isFingerprintEnable());

        if (!mFingerprintIdentify.isFingerprintEnable()) {
            mTvTips.append("\n你的手机不支持此App指纹识别 →_→");
            return;
        }

        mTvTips.append("\n开始识别\n把你的手指放在传感器");
        mFingerprintIdentify.resumeIdentify();
        mFingerprintIdentify.startIdentify(3, new BaseFingerprint.FingerprintIdentifyListener() {
            @Override
            public void onSucceed() {
                mTvTips.append("\n识别正确,重启APP再次验证");
            }

            @Override
            public void onNotMatch(int availableTimes) {
                mTvTips.append("\n不匹配, 还有" + availableTimes + " 次机会");
            }

            @Override
            public void onFailed() {
                mTvTips.append("\n识别失败，重启APP进行重试");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        mTvTips.append("\nrelease");
        mFingerprintIdentify.cancelIdentify();
    }

    public void release(View view) {
        mTvTips.append("\n放弃指纹识别，重启APP再次验证");
        mFingerprintIdentify.cancelIdentify();
    }
}

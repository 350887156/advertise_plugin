package com.lajiaoyang.flutter.advertise;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.PermissionChecker;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class SplashAdManager implements SplashADListener, View.OnClickListener {
    private SplashAD splashAD;
    private TextView skipView;
    private Activity activity;
    private RelativeLayout layout;
    private static SplashAdManager _instance;
    private Button loadAdOnlyCloseButton;
    private static final String SKIP_TEXT = "跳过 %d";
    static SplashAdManager getInstance() {
        if (_instance == null) {
            _instance = new SplashAdManager();
        }
        return _instance;
    }
    public void show(String appID, String placementId, Activity activity) throws Exception {

        if (TextUtils.isEmpty(appID) || TextUtils.isEmpty(placementId) || activity == null) {
            throw new Exception("parameters Error");
        }
        this.activity = activity;
        layout = (RelativeLayout) LayoutInflater.from(activity).inflate(R.layout.activity_splash, null);
        activity.addContentView(layout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        skipView = (TextView) layout.findViewById(R.id.skip_view);
        ViewGroup splashContainer = (ViewGroup) layout.findViewById(R.id.splash_container);
        loadAdOnlyCloseButton = layout.findViewById(R.id.splash_load_ad_close);
        loadAdOnlyCloseButton.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermission(appID,placementId);
        } else {
            fetchSplashAD(appID, placementId);
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission(String appID, String placementId) {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // 快手SDK所需相关权限，存储权限，此处配置作用于流量分配功能，关于流量分配，详情请咨询商务;如果您的APP不需要快手SDK的流量分配功能，则无需申请SD卡权限
        if (!(activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED )){
            lackedPermission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!(activity.checkSelfPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (lackedPermission.size() != 0) {
            // 否则，建议请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            activity.requestPermissions(requestPermissions, 1024);
        }
        fetchSplashAD(appID, placementId);
    }
    /**
     * 拉取开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
     *
     * @param appId           应用ID
     * @param posId           广告位ID
     */
    private void fetchSplashAD(String appId, String posId) {
        splashAD = new SplashAD(activity, skipView, appId, posId, this, 3000);
        ViewGroup splashContainer = (ViewGroup) layout.findViewById(R.id.splash_container);
        if (layout != null) {
            splashAD.fetchAndShowIn(splashContainer);
        }

    }
    private void destroy() {
        if (layout != null) {
            ViewGroup vg = (ViewGroup) layout.getParent();
            vg.removeView(layout);
        }
        activity = null;
        skipView = null;
        layout = null;
        loadAdOnlyCloseButton = null;
        _instance = null;
    }
    @Override
    public void onADDismissed() {
        HashMap<String,String> message = new HashMap<>();
        message.put("splashAd","close");
        AdvertisePlugin.messageChannel.send(message);
        destroy();
    }
    @Override
    public void onNoAD(AdError adError) {
        HashMap<String,String> message = new HashMap<>();
        message.put("splashAd","error");
        AdvertisePlugin.messageChannel.send(message);
        destroy();
    }

    @Override
    public void onADPresent() {

        System.out.print("onADPresent");
    }

    @Override
    public void onADClicked() {
        HashMap<String,String> message = new HashMap<>();
        message.put("splashAd","click");
        AdvertisePlugin.messageChannel.send(message);
    }

    @Override
    public void onADTick(long millisUntilFinished) {
        int l = Math.round(millisUntilFinished / 1000f);
        if (skipView != null) {
            skipView.setText(String.format(SKIP_TEXT, l));
        }

    }

    @Override
    public void onADExposure() {

    }

    @Override
    public void onADLoaded(long l) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.splash_load_ad_close) {
            if (activity != null) {
                activity.finish();
            }
        }
    }


}

package com.lajiaoyang.flutter.advertise;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.util.AdError;

import java.util.HashMap;

public class RewardVideoManager implements RewardVideoADListener {
    private static RewardVideoManager _instance;
    private RewardVideoAD rewardVideoAD;
    static RewardVideoManager getInstance() {
        if (_instance == null) {
            _instance = new RewardVideoManager();
        }
        return _instance;
    }
    public void show(Context context,String appID, String placementId) throws Exception {
        if (TextUtils.isEmpty(appID) || TextUtils.isEmpty(placementId) || context == null) {
            throw new Exception("parameters Error");
        }

        if (rewardVideoAD == null) {
            refreshRewardVideoAd(context,appID,placementId);
            return;
        }
        if (rewardVideoAD.hasShown()) {//广告展示检查2：当前广告数据还没有展示过
            refreshRewardVideoAd(context,appID,placementId);
            return;
        }
        long delta = 1000;//建议给广告过期时间加个buffer，单位ms，这里demo采用1000ms的buffer
        //广告展示检查3：展示广告前判断广告数据未过期
        if (SystemClock.elapsedRealtime() < (rewardVideoAD.getExpireTimestamp() - delta)) {
            rewardVideoAD.showAD();
        } else {
            refreshRewardVideoAd(context,appID,placementId);
        }
    }
    private void refreshRewardVideoAd(Context context, String appID, String posID) {
        rewardVideoAD = new RewardVideoAD(context, appID, posID, this);
        rewardVideoAD.loadAD();

    }
    private void destroy() {
        rewardVideoAD = null;
    }

    /**
     * 广告加载成功，可在此回调后进行广告展示
     **/
    @Override
    public void onADLoad() {
        if (rewardVideoAD != null) {
            rewardVideoAD.showAD();
        }
    }
    /**
     * 视频素材缓存成功，可在此回调后进行广告展示
     */
    @Override
    public void onVideoCached() {
    }
    /**
     * 激励视频广告页面展示
     */
    @Override
    public void onADShow() {

    }
    /**
     * 激励视频广告曝光
     */
    @Override
    public void onADExpose() {

    }
    /**
     * 激励视频触发激励（观看视频大于一定时长或者视频播放完毕）
     */
    @Override
    public void onReward() {
        HashMap<String,String> message = new HashMap<>();
        message.put("rewardVideo","rewardEffective");
        AdvertisePlugin.messageChannel.send(message);

    }
    /**
     * 激励视频广告被点击
     */
    @Override
    public void onADClick() {
        HashMap<String,String> message = new HashMap<>();
        message.put("rewardVideo","click");
        AdvertisePlugin.messageChannel.send(message);
    }
    /**
     * 激励视频播放完毕
     */
    @Override
    public void onVideoComplete() {
        HashMap<String,String> message = new HashMap<>();
        message.put("rewardVideo","didPlayFinish");
        AdvertisePlugin.messageChannel.send(message);

    }
    /**
     * 激励视频广告被关闭
     */
    @Override
    public void onADClose() {
        HashMap<String,String> message = new HashMap<>();
        message.put("rewardVideo","close");
        AdvertisePlugin.messageChannel.send(message);
    }
    /**
     * 广告流程出错
     */
    @Override
    public void onError(AdError adError) {
        HashMap<String,String> message = new HashMap<>();
        message.put("rewardVideo","error");
        AdvertisePlugin.messageChannel.send(message);
    }

}

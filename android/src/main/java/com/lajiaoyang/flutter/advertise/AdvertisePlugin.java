package com.lajiaoyang.flutter.advertise;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.qq.e.ads.splash.SplashADListener;

import java.util.logging.Logger;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.StandardMessageCodec;

/** AdvertisePlugin */
public class AdvertisePlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
//  private static Registrar myRegistrar;
  static BasicMessageChannel<Object> messageChannel;
  private static Context context;
  private static Activity activity;

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {

  }

  @Override
  public void onDetachedFromActivity() {

  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    BinaryMessenger messenger = flutterPluginBinding.getBinaryMessenger();
    messageChannel = new BasicMessageChannel<>(messenger, Constant.messageChannelName, new StandardMessageCodec());
    context = flutterPluginBinding.getApplicationContext();
    final MethodChannel channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "advertise");
    channel.setMethodCallHandler(new AdvertisePlugin());
  }
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "advertise");
    channel.setMethodCallHandler(new AdvertisePlugin());
  }
  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    String appId = call.argument(Constant.adAppId);
    String placementId = call.argument(Constant.placementId);

    if (call.method.equals(Constant.splashAdKey)) {
      try {
        SplashAdManager.getInstance().show(appId,placementId,activity);
      } catch (Exception e) {
        result.error("500",e.getMessage(),e.getMessage());
      }
    } else if (call.method.equals(Constant.rewardVideoKey)) {
      try {
        RewardVideoManager.getInstance().show(context,appId,placementId);
      } catch (Exception e) {
        result.error("500",e.getMessage(),e.getMessage());
      }
    } else {
      result.notImplemented();
    }

  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
  }
}

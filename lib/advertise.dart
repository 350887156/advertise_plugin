import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class Advertise {
  static const MethodChannel _channel =
      const MethodChannel('advertise');

static const BasicMessageChannel _messageChannel =
      const BasicMessageChannel('advertisePluginMessageChannel',StandardMessageCodec());

  static void initMessageHandler({@required Function callback}) async {
    Advertise._messageChannel.setMessageHandler((dynamic value) async {
      if (callback != null) {
        callback(value);
      }
    });
  }
  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
  static Future<bool> splashAdLoadAndShow({@required String adAppId,
  @required String placementId,Uint8List backgroundImage}) async {
    final result = await _channel.invokeMethod('splashAd.loadAdAndShow',{
      'adAppId':adAppId,
      'placementId':placementId,
      'background':backgroundImage
    });
    return result;
  }
  static Future<bool> rewardVideoShow({@required String adAppId,@required String placementId,Uint8List backgroundImage}) async {
    final result = await _channel.invokeMethod('rewardVideo.show',{
      'adAppId':adAppId,
      'placementId':placementId,
      'background':backgroundImage
    });
    return result;
  }
}

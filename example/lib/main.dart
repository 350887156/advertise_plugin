import 'dart:io';

import 'package:flutter/material.dart';
import 'package:advertise/advertise.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  @override
  void initState() {
    super.initState();
    init();
  }
  void init() async {
    Advertise.initMessageHandler(callback: (value){
      print(value);
    });
  }
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              MaterialButton(
                child: Center(
                  child: Text('开屏广告'),
                ),
                onPressed: () async {
                  final appId = Platform.isAndroid ? '1101152570' : '1105344611';
                  final placementId = Platform.isAndroid ? '8863364436303842593' : '9040714184494018';
                  final result = await Advertise.splashAdLoadAndShow(
                      adAppId: appId, placementId: placementId);
                  print(result ? '播放成功' : '播放失败');
                },
              ),
              SizedBox(
                height: 30,
              ),
              MaterialButton(
                child: Center(
                  child: Text('激励广告'),
                ),
                onPressed: () async {
                  final appId = Platform.isAndroid ? '1101152570' : '1105344611';
                  final placementId = Platform.isAndroid ? '6040295592058680' : '8020744212936426';
                  final result = await Advertise.rewardVideoShow(
                      adAppId: appId, placementId: placementId);
                  print(result ? '播放成功' : '播放失败');
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}


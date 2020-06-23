#import "AdvertisePlugin.h"
#import <GDTMobSDK/GDTSplashAd.h>
#import <GDTMobSDK/GDTRewardVideoAd.h>
#import "AdvertisePluginConstant.h"
@interface AdvertisePlugin()
<
GDTSplashAdDelegate,
GDTRewardedVideoAdDelegate
>
@property (nonatomic, strong) GDTSplashAd *splashAd;
@property (nonatomic, strong) GDTRewardVideoAd *rewardVideoAd;
@property (nonatomic, copy) FlutterResult rewardEffectiveCallback;
@property (nonatomic, copy) FlutterResult splashAdCallback;
@property (nonatomic, strong) FlutterBasicMessageChannel *messageChannel;
@end
@implementation AdvertisePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"advertise"
            binaryMessenger:[registrar messenger]];
  AdvertisePlugin* instance = [[AdvertisePlugin alloc] initWithRegistrar:registrar];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (instancetype)initWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar
{
    self = [super init];
    if (self) {
        self.messageChannel = [FlutterBasicMessageChannel messageChannelWithName:kAdvertisePluginMessageChannel binaryMessenger:[registrar messenger]];
    }
    return self;
}
- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    NSString *const appID = call.arguments[kAdAppIdKey];
    NSString *const placementId = call.arguments[kPlacementIdKey];
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    UIViewController *rootViewController = window.rootViewController;
    
    if (![appID isKindOfClass:[NSString class]] ||
        ![placementId isKindOfClass:[NSString class]]) {
        return;
    }
    if ([call.method hasPrefix:kSplashAdKey]) {
      if ([call.method containsString:kLoadAdAndShowKey]) {
          
          self.splashAdCallback = result;
          if (![placementId isKindOfClass:[NSString class]]) return;
          self.splashAd = [[GDTSplashAd alloc] initWithAppId:appID
                                                 placementId:placementId];
          FlutterStandardTypedData *standarData = call.arguments[kBackgroundKey];
          if ([standarData isKindOfClass:[FlutterStandardTypedData class]]) {
              NSData *data = standarData.data;
              if (data) {
                  UIImage *backgroundImage = [UIImage imageWithData:data];
                  self.splashAd.backgroundImage = backgroundImage;
              }
          }
          self.splashAd.fetchDelay = 3;
          self.splashAd.delegate = self;
          [self.splashAd loadAdAndShowInWindow:window];
      }
    } else if ([call.method hasPrefix:kRewardVideoKey]) {
        if ([call.method hasSuffix:kShowKey]) {
            self.rewardEffectiveCallback = result;
            self.rewardVideoAd = [[GDTRewardVideoAd alloc] initWithAppId:appID placementId:placementId];
            self.rewardVideoAd.delegate = self;
            if (self.rewardVideoAd.expiredTimestamp <= [[NSDate date] timeIntervalSince1970] || !self.rewardVideoAd.isAdValid) {
                [self.rewardVideoAd loadAd];
            } else {
                BOOL showResult = [self.rewardVideoAd showAdFromRootViewController:rootViewController];
                result(@(showResult));
            }
        }
        
    } else {
        result(FlutterMethodNotImplemented);
    }
}
#pragma mark - GDTRewardedVideoAdDelegate
- (void)gdt_rewardVideoAdDidRewardEffective:(GDTRewardVideoAd *)rewardedVideoAd
{
    if (self.messageChannel) {
        [self.messageChannel sendMessage:@{kRewardVideoKey:@"rewardEffective"}];
    }
}
- (void)gdt_rewardVideoAdDidPlayFinish:(GDTRewardVideoAd *)rewardedVideoAd
{
    if (self.messageChannel) {
        [self.messageChannel sendMessage:@{kRewardVideoKey:@"didPlayFinish"}];
    }
}
- (void)gdt_rewardVideoAdDidClose:(GDTRewardVideoAd *)rewardedVideoAd
{
    if (self.messageChannel) {
        [self.messageChannel sendMessage:@{kRewardVideoKey:@"close"}];
    }
}
- (void)gdt_rewardVideoAdVideoDidLoad:(GDTRewardVideoAd *)rewardedVideoAd
{
    UIViewController *rootViewController = [UIApplication sharedApplication].keyWindow.rootViewController;
    if (self.rewardVideoAd && self.rewardEffectiveCallback) {
        BOOL showResult = [self.rewardVideoAd showAdFromRootViewController:rootViewController];
        self.rewardEffectiveCallback(@(showResult));
        self.rewardEffectiveCallback = nil;
    }
    
}
- (void)gdt_rewardVideoAd:(GDTRewardVideoAd *)rewardedVideoAd didFailWithError:(NSError *)error
{
    if (self.messageChannel) {
        [self.messageChannel sendMessage:@{kRewardVideoKey:@"error"}];
    }
}

/**
 *  开屏广告成功展示
 */
- (void)splashAdSuccessPresentScreen:(GDTSplashAd *)splashAd
{
    if (self.splashAdCallback) {
        self.splashAdCallback(@(YES));
    }
}


/**
 *  开屏广告展示失败
 */
- (void)splashAdFailToPresent:(GDTSplashAd *)splashAd withError:(NSError *)error
{
    if (self.messageChannel) {
        [self.messageChannel sendMessage:@{kSplashAdKey:@"error"}];
    }
}

/**
 *  开屏广告关闭回调
 */
- (void)splashAdClosed:(GDTSplashAd *)splashAd
{
    if (self.messageChannel) {
        [self.messageChannel sendMessage:@{kSplashAdKey:@"close"}];
    }
}
@end

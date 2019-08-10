# WifiLinker
Wifi连接封装库，适用于智能硬件Wifi通讯。

<a href="https://github.com/kongzue/WifiLinker/">
<img src="https://img.shields.io/badge/WifiLinker-1.0.4-green.svg" alt="Kongzue WifiLinker">
</a>
<a href="https://bintray.com/myzchh/maven/WifiLinker">
<img src="https://img.shields.io/badge/Maven-1.0.4-blue.svg" alt="Maven">
</a>
<a href="http://www.apache.org/licenses/LICENSE-2.0">
<img src="https://img.shields.io/badge/License-Apache%202.0-red.svg" alt="License">
</a>
<a href="http://www.kongzue.com">
<img src="https://img.shields.io/badge/Homepage-Kongzue.com-brightgreen.svg" alt="Homepage">
</a>

Demo下载：<https://fir.im/WifiLinker>

## 前言

本库主要是针对Wifi连接过程的封装

## 使用方法
1) 从 Maven 仓库或 jCenter 引入：
Maven仓库：
```
<dependency>
  <groupId>com.kongzue.smart</groupId>
  <artifactId>wifilinker</artifactId>
  <version>1.0.4</version>
  <type>pom</type>
</dependency>
```
Gradle：
在dependencies{}中添加引用：
```
implementation 'com.kongzue.smart:wifilinker:1.0.4'
```

## 关于权限
您需要申请蓝牙权限后才可以正常使用

主要权限：
```
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

需要申请：
```
Manifest.permission.ACCESS_COARSE_LOCATION,
Manifest.permission.ACCESS_FINE_LOCATION
```

若不声明或申请权限，可能导致无法查找到要连接的目标设备，或无法正常使用功能。

在最新版本的 MIUI 中，因小米私加 WIFI 开关权限导致使用过程中可能出现不停弹出“是否允许程序开关 WIFI”的提示框，而该权限属于私有权限未找到申请方法，请进入软件设置界面手动开启权限：

![MIUI权限](https://github.com/kongzue/Res/raw/master/app/src/main/res/mipmap-xxxhdpi/img_wifilinker_miui_tip.png)

另外请注意，已知在部分设备上，OnWifiConnectStatusChangeListener.onConnect 中返回的已连接 WifiInfo 中 SSID 值为“unknow ssid”系系统策略，并非本库的问题。

### 开始使用

创建 wifiUtil 对象：
```
wifiUtil = new WifiUtil(this);
```

### 查找

1) 查找附近 Wifi（不推荐，部分国产系统对此功能做了限制，建议参考《连接》章节的“对于已知类型的Wifi的连接方式”）
```
wifiUtil.scan(new OnWifiScanListener() {
    @Override
    public void onScan(List<ScanResult> result) {
        //自行处理 result
    }
});
```

2) 停止查找
```
wifiUtil.stopScan();
```

### 连接

对于未知类型的 Wifi，需要先进行查找附近的 Wifi，然后执行：
```
wifiUtil.link(ssid, password, new OnWifiConnectStatusChangeListener() {
    @Override
    public void onStatusChange(boolean isSuccess, int statusCode) {
        //根据 statusCode 判断连接状态
        switch (statusCode){
                    case ERROR_DEVICE_NOT_HAVE_WIFI:
                        txtLog.setText("错误：设备无Wifi");
                        break;
                    case ERROR_CONNECT:
                        txtLog.setText("错误：连接失败");
                        break;
                    case ERROR_CONNECT_SYS_EXISTS_SAME_CONFIG:
                        txtLog.setText("错误：设备已存在相同Wifi配置");
                        break;
                    case ERROR_PASSWORD:
                        txtLog.setText("错误：密码错误");
                        break;
                    case CONNECT_FINISH:
                        txtLog.setText("已连接");
                        break;
                    case DISCONNECTED:
                        txtLog.setText("已断开连接");
                        break;
                }
    }
    @Override
    public void onConnect(WifiInfo wifiInfo) {
        //连接完成后获取 Wifi 信息
        
    }
});
```

对于已知类型的 Wifi，使用以下连接方式：
```
wifiUtil.link(ssid, password, WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA,
        new OnWifiConnectStatusChangeListener() {
            @Override
            public void onStatusChange(boolean isSuccess, int statusCode) {
                switch (statusCode){
                    case ERROR_DEVICE_NOT_HAVE_WIFI:
                        txtLog.setText("错误：设备无Wifi");
                        break;
                    case ERROR_CONNECT:
                        txtLog.setText("错误：连接失败");
                        break;
                    case ERROR_CONNECT_SYS_EXISTS_SAME_CONFIG:
                        txtLog.setText("错误：设备已存在相同Wifi配置");
                        break;
                    case ERROR_PASSWORD:
                        txtLog.setText("错误：密码错误");
                        break;
                    case CONNECT_FINISH:
                        txtLog.setText("已连接");
                        break;
                    case DISCONNECTED:
                        txtLog.setText("已断开连接");
                        break;
                }
            }
            @Override
            public void onConnect(WifiInfo wifiInfo) {
                //连接完成后获取 Wifi 信息
            }
        }
);
```

返回值中，isSuccess 代表是否成功连接，statusCode 表示了所处的状态，状态值对应含义如下：
```
ERROR_DEVICE_NOT_HAVE_WIFI = -1;    //设备无Wifi模块
ERROR_CONNECT = -2;                 //连接失败
ERROR_CONNECT_SYS_EXISTS_SAME_CONFIG = -3;                 //连接失败：系统已存在相同Wifi配置（需手动删除已存储连接）
ERROR_PASSWORD = -11;               //密码错误
CONNECT_START = 1;                  //开始连接
CONNECT_FINISH = 2;                 //已连接
DISCONNECTED = 3;                   //已断开连接
```

额外方法：
```
//断开连接
wifiUtil.disconnect();
```

### 需要注意
请在 Activity 退出时执行 close() 方法：
```
@Override
protected void onDestroy() {
    wifiUtil.close();
    super.onDestroy();
}
```

⚠ 警告！请勿以 statusCode 状态为主进行判断，因为这个吃的是系统广播回调，不准，要知道连不连成功的判断方法是检查 onConnect 有没有执行。

另外我们注意到某些设备在附近没有要连接的 Wifi 的情况下，会主动重新连接之前连接的 Wifi，此时也会走 CONNECT_FINISH 回调，同时 onConnect 也会执行，这种问题的解决方法是判断：
```
@Override
public void onConnect(WifiInfo wifiInfo) {
    //连接完成后获取 Wifi 信息
    if (wifiInfo.getName().contains(你连接的Wifi的SSID)){      //用contains方法判断的原因是wifiInfo.getName()可能是加了引号的WifiSSID
        //差不多是真的连接成功了
    }
}
```
因部分设备限制，wifiInfo.getName() 获取的值可能是 “unknow ssid”这个问题已经在新版本中解决了，方案是通过 networkId 去已存储的Wifi信息列表中找到对应的 Wifi 信息获取其 SSID 是正确的 SSID。

## 开源协议
```
Copyright Kongzue WifiLinker

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

核心方法感谢 @lakebobo 的开源贡献：https://blog.csdn.net/lakebobo/article/details/79581211

## 更新日志
v1.0.4：
- 增加延时 1 秒后再返回 onConnect 以保证获取的 SSID 是正确的；

v1.0.3：
- 解决连接 Wifi 成功状态判断问题；

v1.0.2：
- 修复了 OnWifiConnectStatusChangeListener.onConnect 被多次调用的问题；

v1.0.1：
- 增加了 stopScan() 方法；
- 增加 CONNECT_START 状态；

v1.0.0：
- 首次上传；
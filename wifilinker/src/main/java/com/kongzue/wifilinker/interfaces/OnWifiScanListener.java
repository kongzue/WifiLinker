package com.kongzue.wifilinker.interfaces;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2019/7/26 15:47
 */
public abstract class OnWifiScanListener {
    
    public abstract void onScan(List<ScanResult> result);
    
    public void onScanStart(){};
    
    public void onScanStop(List<ScanResult> result){};
    
}

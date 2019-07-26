package com.kongzue.wifilinker.interfaces;

import com.kongzue.wifilinker.util.WifiInfo;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2019/7/26 15:49
 */
public abstract class OnWifiConnectStatusChangeListener {
    
    public abstract void onStatusChange(boolean isSuccess,int statusCode);
    
    public abstract void onConnect(WifiInfo wifiInfo);
    
}

package com.kongzue.wifilinker.util;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lake on 2017/10/17.
 * huboa1234@126.com
 */
public class WifiAutoConnectManager {
    private static final String TAG = WifiAutoConnectManager.class
            .getSimpleName();
    
    public static WifiManager wifiManager = null;
    private static WifiAutoConnectManager mWifiAutoConnectManager;
    
    // 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }
    
    // 构造函数
    private WifiAutoConnectManager(WifiManager wifiManager) {
        this.wifiManager = wifiManager;
    }
    
    public static WifiAutoConnectManager newInstance(WifiManager wifiManager) {
        if (mWifiAutoConnectManager == null) {
            mWifiAutoConnectManager = new WifiAutoConnectManager(wifiManager);
        }
        return mWifiAutoConnectManager;
    }
    
    
    // 查看以前是否也配置过这个网络
    public WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }
    
    /**
     * 创建wifi配置文件
     *
     * @param SSID
     * @param Password
     * @param Type
     * @return
     */
    public WifiConfiguration createWifiInfo(String SSID, String Password, WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // config.SSID = SSID;
        // nopass
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            // config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            // config.wepTxKeyIndex = 0;
        } else if (Type == WifiCipherType.WIFICIPHER_WEP) {// wep
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (Type == WifiCipherType.WIFICIPHER_WPA) {// wpa
            config.preSharedKey = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }
    
    // 打开wifi功能
    public boolean openWifi() {
        boolean bRet = true;
        if (!wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(true);
        }
        return bRet;
    }
    
    // 关闭WIFI
    public void closeWifi() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }
    
    private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();
        
        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }
        
        return isHex(wepKey);
    }
    
    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 根据给定的ssid信号量和总级别，判断当前信号量，在什么级别
     *
     * @param rssi
     * @param numLevels
     * @return
     */
    public static int getSignalNumsLevel(int rssi, int numLevels) {
        if (wifiManager == null) {
            return -1;
        }
        return WifiManager.calculateSignalLevel(rssi, numLevels);
    }
    
    /**
     * 获取ssid的加密方式
     */
    public static WifiCipherType getCipherType(String ssid) {
        if (wifiManager == null) {
            return null;
        }
        List<ScanResult> list = wifiManager.getScanResults();
        
        for (ScanResult scResult : list) {
            
            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
                String capabilities = scResult.capabilities;
                if (!TextUtils.isEmpty(capabilities)) {
                    
                    if (capabilities.contains("WPA")
                            || capabilities.contains("wpa")) {
                        Log.e("wifidemo", "wpa");
                        return WifiCipherType.WIFICIPHER_WPA;
                    } else if (capabilities.contains("WEP")
                            || capabilities.contains("wep")) {
                        Log.e("wifidemo", "wep");
                        return WifiCipherType.WIFICIPHER_WEP;
                    } else {
                        Log.e("wifidemo", "no");
                        return WifiCipherType.WIFICIPHER_NOPASS;
                    }
                }
            }
        }
        return WifiCipherType.WIFICIPHER_INVALID;
    }
    
    /**
     * 获取 bssid 接入点的地址
     *
     * @return
     */
    public static String getBSSID() {
        if (wifiManager == null) {
            return null;
        }
        WifiInfo info = wifiManager.getConnectionInfo();
        Log.e("wifidemo", "getBSSID" + info.getBSSID());
        if (info == null) {
            return null;
        }
        return info.getBSSID();
    }
    
    /**
     * 获取网关地址
     *
     * @return
     */
    public static String getGateway() {
        if (wifiManager == null) {
            return "";
        }
        InetAddress inetAddress = NetworkUtils.intToInetAddress(wifiManager.getDhcpInfo().gateway);
        if (inetAddress == null) {
            return "";
        }
        return inetAddress.getHostAddress();
    }
    
    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getIpAddress() {
        if (wifiManager == null) {
            return "";
        }
        InetAddress inetAddress = NetworkUtils.intToInetAddress(wifiManager.getConnectionInfo().getIpAddress());
        if (inetAddress == null) {
            return "";
        }
        return inetAddress.getHostAddress();
    }
    
    /**
     * 获取mac地址
     *
     * @return
     */
    public static String getMacAddress() {
        if (wifiManager == null) {
            return "";
        }
        return wifiManager.getConnectionInfo().getMacAddress();
    }
    
    /**
     * 获取wifi名称
     *
     * @return
     */
    public static String getSSID() {
        if (wifiManager == null) {
            return null;
        }
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = info.getSSID();
        if (ssid != null) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }
    
    /**
     * 扫描WIFI AP
     */
    public static boolean startStan() {
        if (wifiManager == null) {
            return false;
        }
        return wifiManager.startScan();
    }
    
    /**
     * 获取所有WIFI AP
     */
    public static List<ScanResult> getScanResults() {
        List<ScanResult> srList = wifiManager.getScanResults();
        if (srList == null) {
            srList = new ArrayList<ScanResult>();
        }
        return srList;
    }
    
}


class NetworkUtils {
    /**
     * Convert a IPv4 address from an integer to an InetAddress.
     *
     * @param hostAddress an int corresponding to the IPv4 address in network byte order
     */
    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = {(byte) (0xff & hostAddress), (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)), (byte) (0xff & (hostAddress >> 24))};
        
        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }
    
    /**
     * Convert a IPv4 address from an InetAddress to an integer
     *
     * @param inetAddr is an InetAddress corresponding to the IPv4 address
     * @return the IP address as an integer in network byte order
     */
    public static int inetAddressToInt(InetAddress inetAddr) throws IllegalArgumentException {
        byte[] addr = inetAddr.getAddress();
        return ((addr[3] & 0xff) << 24) | ((addr[2] & 0xff) << 16) | ((addr[1] & 0xff) << 8) | (addr[0] & 0xff);
    }
}
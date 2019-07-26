package com.kongzue.wifilinker.util;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2019/7/26 16:00
 */
public class WifiInfo {
    
    private String name;
    private String ip;
    private String mac;
    private String gatway;
    
    public WifiInfo() {
    }
    
    public WifiInfo(String name, String ip, String mac, String gatway) {
        this.name = name;
        this.ip = ip;
        this.mac = mac;
        this.gatway = gatway;
    }
    
    public String getName() {
        return name;
    }
    
    public WifiInfo setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getIp() {
        return ip;
    }
    
    public WifiInfo setIp(String ip) {
        this.ip = ip;
        return this;
    }
    
    public String getMac() {
        return mac;
    }
    
    public WifiInfo setMac(String mac) {
        this.mac = mac;
        return this;
    }
    
    public String getGatway() {
        return gatway;
    }
    
    public WifiInfo setGatway(String gatway) {
        this.gatway = gatway;
        return this;
    }
    
    @Override
    public String toString() {
        return "WifiInfo{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                ", gatway='" + gatway + '\'' +
                '}';
    }
}

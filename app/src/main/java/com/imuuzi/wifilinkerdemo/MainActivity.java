package com.imuuzi.wifilinkerdemo;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kongzue.wifilinker.WifiUtil;
import com.kongzue.wifilinker.interfaces.OnWifiConnectStatusChangeListener;
import com.kongzue.wifilinker.interfaces.OnWifiScanListener;
import com.kongzue.wifilinker.util.WifiAutoConnectManager;
import com.kongzue.wifilinker.util.WifiInfo;

import java.util.List;

import static com.kongzue.wifilinker.WifiUtil.CONNECT_FINISH;
import static com.kongzue.wifilinker.WifiUtil.DISCONNECTED;
import static com.kongzue.wifilinker.WifiUtil.ERROR_CONNECT;
import static com.kongzue.wifilinker.WifiUtil.ERROR_CONNECT_SYS_EXISTS_SAME_CONFIG;
import static com.kongzue.wifilinker.WifiUtil.ERROR_DEVICE_NOT_HAVE_WIFI;
import static com.kongzue.wifilinker.WifiUtil.ERROR_PASSWORD;

public class MainActivity extends AppCompatActivity {
    
    private EditText editSsid;
    private EditText editPassword;
    private Button btnLink;
    private Button btnBroken;
    private TextView txtLog;
    
    private WifiUtil wifiUtil;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        wifiUtil = new WifiUtil(this);
        
        editSsid = findViewById(R.id.edit_ssid);
        editPassword = findViewById(R.id.edit_password);
        btnLink = findViewById(R.id.btn_link);
        btnBroken = findViewById(R.id.btn_broken);
        txtLog = findViewById(R.id.txt_log);
    
        btnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiUtil.link(
                        editSsid.getText().toString().trim(),
                        editPassword.getText().toString().trim(),
                        WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA,
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
                            
                            }
                        }
                );
            }
        });
        
        btnBroken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiUtil.disconnect();
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        wifiUtil.close();
        super.onDestroy();
    }
}

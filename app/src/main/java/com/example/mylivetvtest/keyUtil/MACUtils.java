package com.example.mylivetvtest.keyUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import android.telephony.TelephonyManager;
import android.text.TextUtils;


import androidx.core.app.ActivityCompat;


import com.example.mylivetvtest.BuildConfig;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Pattern;

public class MACUtils {
    public static String Mymac = "";

    public static String getMac2() {
        FileInputStream localFileInputStream;
        String mac = "";
        try {
            localFileInputStream = new FileInputStream(
                    "/sys/class/net/eth0/address");
            byte[] arrayOfByte = new byte[17];
            localFileInputStream.read(arrayOfByte, 0, 17);
            mac = new String(arrayOfByte);
            localFileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return mac.toLowerCase();
//		return "002157f3b51c";
//		return "002157f3d8f6";
    }

    public static String wifiMacKey = "wifimackey";

    public static void initMac(Context context) {

        FileInputStream localFileInputStream;
        String str = "";

        //当为TV时
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                String name = networkInterface.getName();
                byte[] addr = networkInterface.getHardwareAddress();
                if ((addr == null) || (addr.length == 0)) {
                    continue;
                }
                StringBuilder buffer = new StringBuilder();
                buffer.setLength(0);
                for (byte b : addr) {
                    buffer.append(String.format("%02X:", b));
                }
                if (buffer.length() > 0) {
                    buffer.deleteCharAt(buffer.length() - 1);
                }

                str = buffer.toString().toLowerCase(Locale.ENGLISH);
                if (name.startsWith("eth")) {
                    if (!jyMac(str)) {
                        str = null;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            try {
                e.printStackTrace();
                localFileInputStream = new FileInputStream("/sys/class/net/eth0/address");
                byte[] arrayOfByte = new byte[17];
                localFileInputStream.read(arrayOfByte, 0, 17);
                str = new String(arrayOfByte);
                localFileInputStream.close();
            } catch (Exception i) {
                i.printStackTrace();
                str = "null";
            }
        }

        if (str.contains(":"))
            str = str.replace(":", "").trim();
        if (str.contains("-"))
            str = str.replace("-", "").trim();

        Mymac = str.toLowerCase();

        if(BuildConfig.DEBUG){
//            Mymac = "36C9E3F1B805";
            //Mymac = "c210e3f1b805";
        }
    }

    public static String getMac() {
//        Log.d("maccache","getMac( ):"+Mymac);
        /*if(Configs.debug){
            if (Mymac == "") {
                Mymac = "c210e3f1b805";
//                MACUtils.initMac(MyApplication.ctx());
            }
        }*/
//        if(Mymac==""){
//            MACUtils.initMac(MyApplication.ctx());
//        }
        return Mymac;
    }


    @SuppressLint("NewApi")
    public static String GetKEY() {
        String m_szImei = "";
        try {
            /*TelephonyManager TelephonyMgr = (TelephonyManager) MyApplication.ctx()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(MyApplication.ctx(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
//                return TODO;
            }
            m_szImei = TelephonyMgr.getDeviceId();*/
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        String m_szDevIDShort = "";
        try {
            m_szDevIDShort = "35"
                    + // we make this look like a valid IMEI

                    Build.BOARD.length() % 2
                    + Build.CPU_ABI.length() % 2 + Build.DEVICE.length() % 2
                    + Build.ID.length() % 2 + Build.MANUFACTURER.length() % 2
                    + Build.PRODUCT.length() % 2
                    + Build.TYPE.length() % 2
                    + Build.USER.length() % 2; // 13 digits
        } catch (Exception e) {
            // TODO: handle exception
        }


        String m_szAndroidID = "";
        try {
            //m_szAndroidID = Settings.Secure.getString(MyApplication.ctx().getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            // TODO: handle exception
        }
        String m_szWLANMAC = "";
        try {
            //	WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            m_szWLANMAC = getWifiMac();
        } catch (Exception e) {
            // TODO: handle exception
        }

        String m_szBTMAC = "";
        try {
            BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
            m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            m_szBTMAC = m_BluetoothAdapter.getAddress();
        } catch (Exception e) {
            // TODO: handle exception
        }

        String m_szLongID = m_szDevIDShort + getMac();
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF)
                m_szUniqueID += "0";
            // add number to string
            m_szUniqueID += Integer.toHexString(b);
        }   // hex string to uppercase
        m_szUniqueID = m_szUniqueID.toUpperCase();
        return m_szUniqueID;

        //Log.d(tag, "----:" + m_szUniqueID+"");
    }

    public static String wifimac="";
    public static void initWifiMac(Context context){
        //String maccache=ACache.get(context).getAsString(wifiMacKey);
//        Log.d("maccache","xx:"+maccache);
        if(true){
            if (context != null) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                boolean wifiInitState = wifiManager.isWifiEnabled();

                String mac = null;

                try {

                    if (!wifiInitState) {
                        boolean openWifi = wifiManager.setWifiEnabled(true);
                    }

                    for (int i = 0; i < 10; i++) {
                        if (wifiManager.isWifiEnabled()) {
                            break;
                        }
                        Thread.sleep(1000);
                    }

                    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                    while (interfaces.hasMoreElements()) {
                        NetworkInterface networkInterface = interfaces.nextElement();
                        String name = networkInterface.getName();
                        byte[] addr = networkInterface.getHardwareAddress();
                        if ((addr == null) || (addr.length == 0)) {
                            continue;
                        }
                        StringBuilder buffer = new StringBuilder();
                        buffer.setLength(0);
                        for (byte b : addr) {
                            buffer.append(String.format("%02X:", b));
                        }
                        if (buffer.length() > 0) {
                            buffer.deleteCharAt(buffer.length() - 1);
                        }
                        mac = buffer.toString().toLowerCase(Locale.ENGLISH);

                        if (name.startsWith("wlan")) {
                            if (!jyMac(mac)) {
                                mac = null;
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mac = null;
                } finally {
                    if (!wifiInitState) {
                        wifiManager.setWifiEnabled(false);
                    }
                }
                wifimac=mac;
                ACache.get(context).put(wifiMacKey,wifimac);
            }

        }else{
//
            wifimac=ACache.get(context).getAsString(wifiMacKey);
        }
//        Log.d("MAC","wifimac:"+wifimac);



    }
    public static String getWifiMac() {
        return wifimac;

    }


    private static boolean jyMac(String mac) {

        if (TextUtils.isEmpty(mac)) {
            return false;
        }

        String patternMac = "^[a-f0-9]{2}(:[a-f0-9]{2}){5}$";

        return Pattern.compile(patternMac).matcher(mac).find();
    }

    public static int getVerCode() {
        int verCode = -1;
        try {
            //verCode = MyApplication.ctx().getPackageManager().getPackageInfo(
                    //MyApplication.ctx().getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verCode;
    }

    public static String GetTK() {
        String cpuid = "";
        String m_szDevIDShort = "";
        try {
            m_szDevIDShort = "35"
                    + // we make this look like a valid IMEI
                    Build.BOARD.length()
                    + Build.CPU_ABI.length() + Build.DEVICE.length()
                    + Build.ID.length(); // 13 digits
        } catch (Exception e) {
            // TODO: handle exception


        }
        String m_szLongID = m_szDevIDShort + getMac();
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF)
                m_szUniqueID += "0";
            // add number to string
            m_szUniqueID += Integer.toHexString(b);
        }   // hex string to uppercase
        m_szUniqueID = m_szUniqueID.toUpperCase();
        cpuid = m_szUniqueID;
        return cpuid;
    }

    public static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    public static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }
}

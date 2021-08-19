package com.example.mylivetvtest;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface JnaCore extends Library {
    String JNA_LIBRARY_NAME = "core";
    //public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(JnaFromThird.JNA_LIBRARY_NAME);
    JnaCore INSTANCE = (JnaCore) Native.loadLibrary(JnaCore.JNA_LIBRARY_NAME, JnaCore.class); //直接加载第三方动态库  第2个参数:JnaFromThird.class的类加载

    void OnVodStart(int port);
    void OnVodStop();
    void OnLiveStart(int port);
    void OnLiveStop();
}

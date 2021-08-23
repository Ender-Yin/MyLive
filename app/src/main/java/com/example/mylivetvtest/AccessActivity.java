package com.example.mylivetvtest;

import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.example.mylivetvtest.keyUtil.ACache;
import com.example.mylivetvtest.keyUtil.AESSecurity;
import com.example.mylivetvtest.keyUtil.AuthInfo;
import com.example.mylivetvtest.keyUtil.Configs;
import com.example.mylivetvtest.keyUtil.MACUtils;
import com.example.mylivetvtest.keyUtil.MD5Util;
import com.example.mylivetvtest.keyUtil.RSACoder;
import com.example.mylivetvtest.keyUtil.RandomCharData;
import com.example.mylivetvtest.keyUtil.Tools;
import com.example.mylivetvtest.keyUtil.ExitUtil;
import com.example.mylivetvtest.module.MD_INFO;
import com.example.mylivetvtest.module.ModelTV;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AccessActivity extends Activity {

    public static String Cache_pwd_key = "Cache_pwd_key";
    public static String Cache_fmac_key = "Cache_fmac_key";

    Intent intentToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

        ExitUtil.getInstance().addActivity(this);

        intentToMain = new Intent(this,MainActivity.class);

        MACUtils.initMac(this);
        MACUtils.initWifiMac(this);
        MAC = MACUtils.getMac();
        Log.e("MAC",MAC);
        Log.e("wifiMAC",MACUtils.getWifiMac());
        findFromNet(true);



    }

    private List<ModelTV> allProgramList=new ArrayList<ModelTV>();//一级菜单数据列表
    String HOST = "http://vodlist.maoq.pw";
    String MAC = "";
    //--------------------------------获取节目列表相关--------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getAllPrograms() {
        String key = "1234567891234567";
        String data = "exampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexample";    //明文
        String miwen1 = AESSecurity.encrypt(data, MD5Util.getStringMD5_32(key));
        String mingwen1 =  AESSecurity.decrypt( miwen1 , MD5Util.getStringMD5_32(key));

        String miwen = "Cn1/7c8lduvU29w7Kcigww==";      //密文
        String key1 = "nhsojedjif083ycG";
        Log.e("AESSecurity要加密的原文长度：", String.valueOf(data.length()));
        Log.e("AESSecurity加密 得到的密文", miwen1);
        Log.e("AESSecurity要解密的密文长度：", miwen1.length() + "");
        Log.e("AESSecurity解密 得到的明文",  mingwen1);

        Log.e("AESSecurity加密", AESSecurity.encrypt(AESSecurity.decrypt(miwen, key1), key1));
        Log.e("AESSecurity解密", AESSecurity.decrypt(miwen, key1));


        //Log.e("Aes_decode加密", AESSecurity.encrypt(AESSecurity.decrypt(miwen, key1), key1));
        //Log.e("Aes_decode解密", AESSecurity.decrypt(miwen, MD5Util.getStringMD5_32(key1)));
//        MyKey= RandomCharData.createRandomCharData(8);

        String srcData = String.format("%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s",
                "200",
                MACUtils.GetTK(),
                MACUtils.GetKEY(),
                info.getIp(),
                info.getT(),
                MAC,
                Build.MODEL,
                String.valueOf(Tools.getVerCode(this)),
                Build.BOARD,
                Build.BRAND,
                Build.HARDWARE,
                Build.SERIAL,
                Build.CPU_ABI,
                Build.CPU_ABI2,
                Build.ID,
                MACUtils.getWifiMac());

        Log.e("TVLIST,srcData:" , srcData);
        String info[] = GetRsaPost(srcData);        //加密上传信息

        //FinalHttp fn = new FinalHttp();
        //AjaxParams param = new AjaxParams();
        //String Url = Configs.URL.getTVlistUrl();
        String HOST = "http://vodlist.maoq.pw";
        String Url =  HOST + "/Pvod/Vod/InitList.jsp?l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        Log.i("TVLIST,Url:" , Url);
        Log.i("加密过的密钥:" ,  " " + info[0]);
        Log.i("加密的上传参数信息:" , " " + info[1]);
        /*param.put("appid", "200");
        param.put("mac", macStr);
        param.put("gkey", info[0]);
        param.put("token", info[1]);*/
        //param.put("debug", "1");

        long startTime = System.currentTimeMillis();
        final long[] endTime = new long[1];

        final String[] endJson = {""};

        //------------------使用OKHttp 请求！！！--------------------------------------
        OkHttpClient okHttpClient = new OkHttpClient();

        //MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        requestBody.addFormDataPart("appid", "200");
        requestBody.addFormDataPart("mac", MAC);
        requestBody.addFormDataPart("gkey", info[0]);
        requestBody.addFormDataPart("token", info[1]);
        final Request request = new Request.Builder()
                .url(Url)
                .post(requestBody.build())      //参数放在body体里
                .build();

        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("okhttp: ", "连接失败"+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                endTime[0] = System.currentTimeMillis();
                Log.e("onResponse","TVLIST,onSuccess: " + "取列表成功耗时:" + ((endTime[0] - startTime) / 1000 + "秒 "));
                String responseData = response.body().string();
                Log.e("onResponse","TVLIST,onSuccess: 返回的body的string数据: " + responseData);
                Log.e("onResponse","TVLIST,onSuccess: toString " + response.toString());

                Log.e("得到的json长度： ","" + responseData.length());
                endJson[0] = Aes_decode(responseData);
                Log.e("解密后的json原文： ",endJson[0]);

                allProgramList = new Gson().fromJson(endJson[0], new TypeToken<List<ModelTV>>(){}.getType());
                ModelTV test = allProgramList.get(0);

                List<ModelTV.ListItem> testTvList = test.getList();
                ModelTV.ListItem item = testTvList.get(1);
                List<String> UrlList = item.getUrlList();
                String url1 = UrlList.get(0);
                Log.e("节目地址： ", url1);
                Log.e("类名： ", test.getClassify());
                Log.e("类名的个数： ", allProgramList.size() + "");
                Log.e("频道1的图片： ", item.getIco());
                Log.e("频道1的图片： ", item.isPlaying()?"yes" : "no" );

                //
                Log.e("节目地址： ", url1.substring(26) + "");
                Log.e("str长度： ", url1.length() + "");

                for(int i = 0; i < allProgramList.size(); i++) {
                    ModelTV modelTV = allProgramList.get(i);
                    //categoryItemList.add(new CategoryItem(modelTV.getClassify()));
                }

                MyApplication.TvListCache = allProgramList;

                if(allProgramList.size() != 0){
                    startActivity(intentToMain);
                }
            }
        });

        //------------------使用OKHttp 请求！！！  2  获得ip和t  --------------------------------------
        String t = "1627900441";
        String ip = "113.90.29.85";
        String dataFrom= "{\"t\":1627900441,\"c\":\"CN\",\"ip\":\"113.90.29.85\",\"key\":\"9936669a73ed775ee05d5376538c6665\",\"test\":\"e6ea33596a890b0c8e9868e27c0c9d0f\"}";
        final String[] ipAndT = {null};
        String UrlForIPandT =   HOST + "/Pvod/Init/info.jsp?appid=" + "200" + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        OkHttpClient okHttpClient1 = new OkHttpClient();

        //MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
        MultipartBody.Builder requestBody1 = new MultipartBody.Builder().setType(MultipartBody.FORM);
        final Request request1 = new Request.Builder()
                .url(UrlForIPandT)
                .get()     //参数放在body体里
                .build();

        Call call1 = okHttpClient1.newCall(request1);

        call1.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("okhttp: ", "连接失败"+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ipAndT[0] = response.body().string();
                Log.e("获得的数据： ",ipAndT[0]);
            }
        });

        //----------------------------------认证部分-------------------------------------------
        String urlForAuthor = HOST + "/Pvod/Init/init.jsp?appid=" + "200" + "&mac=" + MAC;
    }

    public  String Aes_decode(String aesstr) {
        String key = info.getIp() + info.getT() +  "qq395585991";
        //String key =  "qq395585991";

        String EndKey = MD5Util.getStringMD5_32(key);
        String EedJson = AESSecurity.decrypt(aesstr, EndKey);
        return EedJson;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String[] GetRsaPost(String srcData){
        String enStr = RandomCharData.createRandomCharData(8);
        String AESKEY = MD5Util.getStringMD5_16(enStr);     //随机生成数据
        Log.i("加工过的8位随机数字",AESKEY);

        String keyStr = RSACoder.loadKeyAssets("rsa_public_key.pem",this);
        Log.e("密钥", "" + keyStr);

        String tokenStr;
        String RSAKEY = null;

        try {
            //RSA-KEY 将<一组随机生成数据P>，通过<一串 RSA-公钥K>（通过<RSA算法E>） 加密而成。
            RSAKEY = Base64.encodeToString(RSACoder.encrytByPublicKey(
                    AESKEY.getBytes("UTF-8"), keyStr), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }Log.e("RSAKey的值", "" + RSAKEY);

        tokenStr = AESSecurity.encrypt(srcData, AESKEY);        //加密参数的数据 由<一组随机生成数据P>作为密钥（通过<AES算法E>）
        String [] re={RSAKEY,tokenStr};     //<一组随机生成数据P>通过RSA加密而成密钥  ，  通过以<一组随机生成数据P>为密钥加密过的 参数数据
        return re;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void doAuth(final boolean flag, int time) {
        //FinalHttp finalHttp = new FinalHttp();
        String macStr = MACUtils.getMac();
        String enStr = RandomCharData.createRandomCharData(8);
        String keyStr = RSACoder.loadKeyAssets("rsa_public_key.pem",this);
        String AESKEY = MD5Util.getStringMD5_16(enStr);
//		String srcData = String.format("%s@%s@%s@%s@%s",Configs.APPID, MACUtils.GetTK(), MACUtils.GetKEY(), enStr,time);

        //cache和随机数
        ACache aCache = ACache.get(this);
        String pwd = aCache.getAsString(Cache_pwd_key);
        String mRandomPassword = null;//由随机数生成的密码
        if (pwd != null) {
            mRandomPassword = pwd;
        } else {
            String tmpStr = RandomCharData.createRandomCharData(8);
            mRandomPassword = tmpStr;
            aCache.put(Cache_pwd_key, tmpStr);
        }


        String srcData = String.format("%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s",
                Configs.APPID,
                MACUtils.GetTK(),
                MACUtils.GetKEY(),
                enStr,
                time,
                macStr,
                Build.MODEL,
                String.valueOf(Tools.getVerCode(this)),
                Build.BOARD,
                Build.BRAND,
                Build.HARDWARE,
                Build.SERIAL,
                Build.CPU_ABI,
                Build.CPU_ABI2,
                Build.ID,
                MACUtils.getWifiMac(),
                mRandomPassword);

        Log.e("author","参数src:" + srcData);
        String tokenStr = null;
        String RSAKEY = null;
        try {
            RSAKEY = Base64.encodeToString(RSACoder.encrytByPublicKey(
                    AESKEY.getBytes("utf-8"), keyStr), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tokenStr = AESSecurity.encrypt(srcData, AESKEY);

        //------------------使用OKHttp 请求！！！--------------------------------------
        OkHttpClient okHttpClient = new OkHttpClient();

        String urlForAuthor = HOST + "/Pvod/Init/init.jsp?appid=" + "200" + "&mac=" + MAC;
        //MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        assert RSAKEY != null;
        requestBody.addFormDataPart("gkey", RSAKEY);
        requestBody.addFormDataPart("token", tokenStr);

        final Request request = new Request.Builder()
                .url(urlForAuthor)   // 认证的url
                .post(requestBody.build())      //参数放在body体里
                .build();

        Call call = okHttpClient.newCall(request);

        long startTime = System.currentTimeMillis();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("okhttp: ", "连接失败"+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                long[] endTime = new long[0];
                //endTime[0] = System.currentTimeMillis();
                String responseData = response.body().string();
                AuthInfo mAuthInfo = new Gson().fromJson(responseData, AuthInfo.class);
                Log.e("onResponse--doAuth","请求成功:");
                Log.e("onResponse--doAuth","" + response.toString());
                Log.e("onResponse--doAuth","得到数据: " + responseData);
                //Log.e("onResponse--doAuth","解密msg: " + Aes_decode(s));

                getAllPrograms();
            }
        });

    }

    MD_INFO info = null;
    //获取ip和time
    public void findFromNet(final boolean flag) {
        String UrlInfo = HOST + "/Pvod/Init/info.jsp?appid=" + "200" + "&mac=" + MAC + "&l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        String infokey = "";

        long startTime = System.currentTimeMillis();

        OkHttpClient okHttpClient1 = new OkHttpClient();

        //MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
        MultipartBody.Builder requestBody1 = new MultipartBody.Builder().setType(MultipartBody.FORM);
        final Request request1 = new Request.Builder()
                .url(UrlInfo)
                .get()     //参数放在body体里
                .build();

        Call call1 = okHttpClient1.newCall(request1);

        call1.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("okhttp: ", "连接失败"+e.getMessage());
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try{
                    Gson g = new Gson();

                    info = g.fromJson(response.body().string(), MD_INFO.class);
                    Log.e("获得ip和time","" + info.getT());

                    doAuth(flag, info.t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
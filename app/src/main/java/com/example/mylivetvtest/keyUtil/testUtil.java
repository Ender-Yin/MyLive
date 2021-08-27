package com.example.mylivetvtest.keyUtil;

import android.content.Context;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.mylivetvtest.BuildConfig;
import com.example.mylivetvtest.R;
import org.apache.commons.codec.binary.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class testUtil {
    Context context;

    //解密
    public static String Aes_decode(String aesstr) {
        String key = "qq395585991";
        String EndKey = MD5Util.getStringMD5_32(key);

        String EedJson = AESSecurity.decrypt(aesstr, EndKey);
        return EedJson;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getAllPrograms() {
        String macStr = MACUtils.getMac();
//        MyKey= RandomCharData.createRandomCharData(8);

        String srcData = String.format("%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s",
                "200",
                MACUtils.GetTK(),
                MACUtils.GetKEY(),
                macStr,
                Build.MODEL,
                Build.BOARD,
                Build.BRAND,
                Build.HARDWARE,
                Build.SERIAL,
                Build.CPU_ABI,
                Build.CPU_ABI2,
                Build.ID,
                MACUtils.getWifiMac());

        Log.i("TVLIST,srcData:" , srcData);
        String info[] = GetRsaPost(srcData);        //加密上传信息

        //FinalHttp fn = new FinalHttp();
        //AjaxParams param = new AjaxParams();
        //String Url = Configs.URL.getTVlistUrl();
        String HOST = "http://vodlist.maoq.pw";
        String Url =  HOST + "/Pvod/Vod/InitList.jsp?l=" + Locale.getDefault().getLanguage() + "&gj=" + Locale.getDefault().getCountry();
        Log.i("TVLIST,Url:" , Url);
        /*param.put("appid", "200");
        param.put("mac", macStr);
        param.put("gkey", info[0]);
        param.put("token", info[1]);*/
        //param.put("debug", "1");

        long startTime = System.currentTimeMillis();
        final long[] endTime = new long[1];

        //------------------使用OKHttp 请求！！！--------------------------------------
        OkHttpClient okHttpClient = new OkHttpClient();

        //MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        requestBody.addFormDataPart("appid", "200");
        requestBody.addFormDataPart("mac", macStr);
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
                Log.d("okhttp: ", "连接失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                endTime[0] = System.currentTimeMillis();
                Toast.makeText(context, "取列表成功耗时:" + ((endTime[0] - startTime) / 1000 + "秒 "), Toast.LENGTH_LONG).show();
                Log.i("onResponse","TVLIST,onSuccess:" + response.toString());
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String[] GetRsaPost(String srcData){
        String enStr = RandomCharData.createRandomCharData(8);
        String AESKEY = MD5Util.getStringMD5_16(enStr);     //随机生成数据

        String keyStr = RSACoder.loadKeyAssets("rsa_public_key.pem",context);

        String tokenStr;
        String RSAKEY = null;
        try {
            //RSA-KEY 将<一组随机生成数据P>，通过<一串 RSA-公钥K>（通过<RSA算法E>） 加密而成。
            RSAKEY = Base64.encodeToString(RSACoder.encrytByPublicKey(
                    AESKEY.getBytes("utf-8"), keyStr), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tokenStr = AESSecurity.encrypt(srcData, AESKEY);        //加密参数的数据 由<一组随机生成数据P>作为密钥（通过<AES算法E>）
        String [] re={RSAKEY,tokenStr};     //<一组随机生成数据P>通过RSA加密而成密钥  ，  通过以<一组随机生成数据P>为密钥加密过的 参数数据
        return re;
    }


}

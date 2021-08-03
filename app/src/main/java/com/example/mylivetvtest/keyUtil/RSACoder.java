package com.example.mylivetvtest.keyUtil;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * RSA安全编码组件
 *
 * @author 梁栋
 * @version 1.0
 */
public abstract class RSACoder extends Coder {

    private static final String TAG = RSACoder.class.getSimpleName();

    /**
     * 数字签名
     * 密钥算法
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 数字签名
     * 签名/验证算法
     */
    public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

    /**
     * 公钥
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 私钥
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * 模式
     * */
    private static final String MODE = "RSA/ECB/PKCS1Padding";

    /**
     * RSA密钥长度 默认1024位，
     *  密钥长度必须是64的倍数，
     *  范围在512至65536位之间。
     */
    private static final int KEY_SIZE = 1024;

    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 签名
     *
     * @param data
     *            待签名数据
     * @param privateKey
     *            私钥
     * @return byte[] 数字签名
     * @throws Exception
     */
    public static byte[] sign(byte[] data, byte[] privateKey) throws Exception {

        // 转换私钥材料
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);

        // 实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // 取私钥匙对象
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 实例化Signature
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

        // 初始化Signature
        signature.initSign(priKey);

        // 更新
        signature.update(data);

        // 签名
        return signature.sign();
    }

    /**
     * 校验
     *
     * @param data
     *            待校验数据
     * @param publicKey
     *            公钥
     * @param sign
     *            数字签名
     *
     * @return boolean 校验成功返回true 失败返回false
     * @throws Exception
     *
     */
    public static boolean verify(byte[] data, byte[] publicKey, byte[] sign)
            throws Exception {

        // 转换公钥材料
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);

        // 实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // 生成公钥
        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        // 实例化Signature
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

        // 初始化Signature
        signature.initVerify(pubKey);

        // 更新
        signature.update(data);

        // 验证
        return signature.verify(sign);
    }

    /**
     * 加密<br>
     * 用公钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] encrytByPublicKey(byte[] data, String key)
        throws Exception {
        //对公钥解密
        byte[] keyBytes = decryptBASE64(key);

        //取得公钥
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        //对数据加密
        Cipher cipher = Cipher.getInstance(MODE);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 解密<br>
     * 用公钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] decryptByPublicKey(byte[] data, String key) throws Exception {
        //对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        //取得公钥
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        //对数据解密
        Cipher cipher = Cipher.getInstance(MODE);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }

    /**
     * 加密<br>
     * 用私钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] encryptByPrivateKey(byte[] data, String key) throws Exception {
        //对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        //取得私钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        //对数据加密
        Cipher cipher = Cipher.getInstance(MODE);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 解密<br>
     * 用私钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] decryptByPrivateKey(byte[] data, String key) throws Exception {
        //对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        //取得私钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        //对数据解密
        Cipher cipher = Cipher.getInstance(MODE);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        int inputLen = data.length;

        int offset = 0;

        byte[] cache;

        int i = 0;

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(data, offset, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            Log.d(TAG, "parse i " + i);
            offset = i * MAX_DECRYPT_BLOCK;
        }

        byte[] deDataBytes = out.toByteArray();

        out.close();

        return deDataBytes;
    }

    /**
     * 取得私钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] getPrivateKey(Map<String, Object> keyMap)
            throws Exception {

        Key key = (Key) keyMap.get(PRIVATE_KEY);

        return encryptBASE64(key.getEncoded());
    }

    /**
     * 从文件中加载私钥
     * @param keyFileName 私钥文件名
     * @return String
     * @throws Exception
     */
    public static String loadKeyF(String keyFileName) throws Exception {

        File file = new File(keyFileName);
        if (!file.exists()) {
            return null;
        }

        BufferedReader br = new BufferedReader(new FileReader(keyFileName));

        try {

            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
//                    Log.d(TAG, sb.toString());
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            br.close();
        }
    }

    public static String loadKeyAssets(String keyFileName, Context context) {
        AssetManager assetManager = context.getAssets();

        InputStream is = null;
        BufferedReader br = null;
        String readLine = null;
        StringBuilder sb = new StringBuilder();
        try {
            is =context.getAssets().open(keyFileName);
            br = new BufferedReader(new InputStreamReader(is));
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) { }
            try {
                br.close();
            } catch (IOException e) { }
            readLine = sb.toString();
            sb.setLength(0);
        }
        return readLine;
    }

    /**
     * 取得公钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] getPublicKey(Map<String, Object> keyMap)
            throws Exception {

        Key key = (Key) keyMap.get(PUBLIC_KEY);

        return encryptBASE64(key.getEncoded());
    }

    /**
     * 初始化密钥
     *
     * @return Map 密钥对儿 Map
     * @throws Exception
     */
    public static Map<String, Object> initKey() throws Exception {

        // 实例化密钥对儿生成器
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance(KEY_ALGORITHM);

        // 初始化密钥对儿生成器
        keyPairGen.initialize(KEY_SIZE);

        // 生成密钥对儿
        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // 封装密钥
        Map<String, Object> keyMap = new HashMap<String, Object>(2);

        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);

        return keyMap;
    }
}
package com.example.baifan.myapplication.utils;

import android.util.Log;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 功能说明: AES256加密解密方法
 * Created by baifan on 2018/4/16.
 */

public class AES256Encryption {
    public static final String KEY_SEED = "SECONDHAND";
    public static final String KEY_ALGORITHM = "AES";
    public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS7Padding";

    /**
     * @Title: getKeyByPass
     * @Description: 使用指定的字符串生成秘钥
     * @param @return
     * @return byte[]
     * @throws
     */
    public static byte[] getKeyByPass() {
        // 生成秘钥
        /**
         * 生成密钥，java6只支持56位密钥，bouncycastle支持64位密钥
         * @return byte[] 二进制密钥
         * */
        SecretKey sk = null;
        try {
            KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM,"BC");
            //SecureRandom secureRandom=SecureRandom.getInstance("SHA1PRNG");
            SecureRandom secureRandom = null;
            if (android.os.Build.VERSION.SDK_INT >= 17) {
                secureRandom = SecureRandom.getInstance("SHA1PRNG","Crypto");
            }
            else{
                secureRandom = SecureRandom.getInstance("SHA1PRNG");
            }
            secureRandom.setSeed(KEY_SEED.getBytes());
            // kg.init(256);//要生成多少位，只需要修改这里即可128, 192或256
            // SecureRandom是生成安全随机数序列，password.getBytes()是种子，只要种子相同，序列就一样，所以生成的秘钥就一样。
            kg.init(256, secureRandom);
            sk = kg.generateKey();
        } catch (NoSuchProviderException e) {
            Log.w("无法实例化密钥生成器",e);
        } catch (NoSuchAlgorithmException e) {
            Log.w("没有此算法",e);
        }
        return sk.getEncoded();
    }

    /**
     * @Title: initRootKey
     * @Description: 初始化密钥
     * @param @return
     * @param @throws Exception
     * @return byte[]
     * @throws
     */
    public static byte[] initRootKey() throws Exception {
        return new byte[] { 0x08, 0x08, 0x04, 0x0b, 0x02, 0x0f, 0x0b, 0x0c, 0x01, 0x03, 0x09, 0x07, 0x0c, 0x03, 0x07,
                0x0a, 0x04, 0x0f, 0x06, 0x0f, 0x0e, 0x09, 0x05, 0x01, 0x0a, 0x0a, 0x01, 0x09, 0x06, 0x07, 0x09, 0x0d };
    }

    /**
     * 转换密钥
     * @param key 二进制密钥
     * @return Key 密钥
     * */
    public static Key toKey(byte[] key) throws Exception {
        // 实例化DES密钥
        // 生成密钥
        SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        return secretKey;
    }

    /**
     * @Title: encrypt
     * @Description: Description
     * @param @param data
     * @param @param AES256加密
     * @param @return
     * @param @throws Exception
     * @return byte[]
     * @throws
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, k);
        return cipher.doFinal(data);
    }

    /**
     * @Title: decrypt
     * @Description: AES256解密
     * @param @param data
     * @param @param key
     * @param @return
     * @param @throws Exception
     * @return byte[]
     * @throws
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
        cipher.init(Cipher.DECRYPT_MODE, k);
        return cipher.doFinal(data);
    }

}

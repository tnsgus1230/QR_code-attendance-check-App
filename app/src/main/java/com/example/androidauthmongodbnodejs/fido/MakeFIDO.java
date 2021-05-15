package com.example.androidauthmongodbnodejs.fido;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class MakeFIDO {

    public final static String alias = "ExploreFurther"; // KeyStore alias
    public static String publicKEY = "";
    public static String privateKEY = "";
    public static String storedKey = "";
    public static String hashedKey = "";
    public static byte[] iv;
    public static SecretKey secretKey;


    public static String tempData ="";


    @RequiresApi(api = Build.VERSION_CODES.M)
    public MakeFIDO() {

        try {

            /*----------Key Store 초기화 및 불러오기----------*/
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            if (!keyStore.containsAlias(alias)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                keyGenerator.init(
                        new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                                .setKeySize(256)
                                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                                .setRandomizedEncryptionRequired(false)
                                .build());
                secretKey = keyGenerator.generateKey();
            }
            secretKey = (SecretKey) keyStore.getKey(alias, null);


            /*------------RSA 키페어 생성-----------*/
            SecureRandom secureRandom = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048, secureRandom);
            KeyPair keyPair = keyPairGenerator.genKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            publicKEY = Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
            privateKEY = Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT);

            /*------------RSA Pri 키 저장------------*/
            Cipher cipher;
            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            iv = cipher.getIV();
            byte[] encypted = cipher.doFinal(privateKEY.getBytes("UTF-8"));

            /*------------키스토어 저장 값------------*/
            storedKey = Base64.encodeToString(encypted, Base64.DEFAULT);

            /*----------랜덤값 생성----------*/
            int randomNum = (int) (Math.random() * 100000);
            String randomNumString = Integer.toString(randomNum);
            Date date = new Date();
            long milli = date.getTime();
            String milliString = Long.toString(milli);
            String percentage = milliString + randomNumString;

            /*----------해시값 생성----------*/
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(percentage.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString(byteData[i] & 0xff + 0x100, 16).substring(1));
                hashedKey = sb.toString();
            }

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

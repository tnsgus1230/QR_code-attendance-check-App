package com.example.androidauthmongodbnodejs.fido;

import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class VerifyFIDO {

    public final static String alias = "ExploreFurther"; // KeyStore alias
    public static String signedOtp;
    public static SecretKey secretKey;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public VerifyFIDO(String otp, String storedKey, byte[] iv) {

        try {
            /*----------Key Store 키 불러오기----------*/
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            secretKey = (SecretKey) keyStore.getKey(alias, null);

            /*----------저장된 키로 PriK 복구----------*/
            Cipher cipher;
            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            byte[] byteStr = Base64.decode(storedKey.getBytes(), Base64.DEFAULT);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            String privateKEY = new String(cipher.doFinal(byteStr), "UTF-8");


            /*----------OTP 전자서명----------*/
            signedOtp = RSA.rsaEncryption(otp, privateKEY);


        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }


    }
}

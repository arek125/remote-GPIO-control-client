package com.rgc;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.util.Base64;
import android.util.Log;

@SuppressLint("NewApi")
public class Crypt {

private static final String tag = Crypt.class.getSimpleName();

private static final String characterEncoding = "UTF-8";
private static final String cipherTransformation = "AES/CBC/PKCS5Padding";
private static final String aesEncryptionAlgorithm = "AES";
private static  String key;
private static byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
private static byte[] keyBytes;

private static Crypt instance = null;


Crypt()
{
    SecureRandom random = new SecureRandom();
    Crypt.ivBytes = new byte[16];
    random.nextBytes(Crypt.ivBytes); 
}

public static Crypt getInstance() {
    if(instance == null){
        instance = new Crypt();
    }

    return instance;
}

public String encrypt_string(final String plain, final String enc_key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException
{
	key = enc_key;
    return Base64.encodeToString(encrypt(plain.getBytes()), Base64.DEFAULT);
}

public String decrypt_string(final String plain, final String enc_key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException
{
	key = enc_key;
	byte[] encryptedBytes = decrypt(Base64.decode(plain, 0));
    return new String(encryptedBytes);
}



public   byte[] encrypt(   byte[] mes)
        throws NoSuchAlgorithmException,
        NoSuchPaddingException,
        InvalidKeyException,
        InvalidAlgorithmParameterException,
        IllegalBlockSizeException,
        BadPaddingException, IOException {

    keyBytes = key.getBytes();
    AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
    SecretKeySpec newKey = new SecretKeySpec(keyBytes, aesEncryptionAlgorithm);
    Cipher cipher = null;
    cipher = Cipher.getInstance(cipherTransformation);

    SecureRandom random = new SecureRandom();   
    Crypt.ivBytes = new byte[16];               
    random.nextBytes(Crypt.ivBytes);            

    cipher.init(Cipher.ENCRYPT_MODE, newKey, random);
//    cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
    byte[] destination = new byte[ivBytes.length + mes.length];
    System.arraycopy(ivBytes, 0, destination, 0, ivBytes.length);
    System.arraycopy(mes, 0, destination, ivBytes.length, mes.length);
    return  cipher.doFinal(destination);

}

public   byte[] decrypt(   byte[] bytes)
        throws NoSuchAlgorithmException,
        NoSuchPaddingException,
        InvalidKeyException,
        InvalidAlgorithmParameterException,
        IllegalBlockSizeException,
        BadPaddingException, IOException, ClassNotFoundException {

    keyBytes = key.getBytes();
    byte[] ivB = Arrays.copyOfRange(bytes,0,16);
    //Log.d(tag, "IV: "+new String(ivB));
    byte[] codB = Arrays.copyOfRange(bytes,16,bytes.length);

    AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivB);
    SecretKeySpec newKey = new SecretKeySpec(keyBytes, aesEncryptionAlgorithm);
    Cipher cipher = Cipher.getInstance(cipherTransformation);
    cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
    byte[] res = cipher.doFinal(codB); 
    return  res;

}


}
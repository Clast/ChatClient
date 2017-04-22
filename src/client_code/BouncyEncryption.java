package client_code;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/*
 * Copyright (C) 2011 www.itcsolutions.eu
 *
 * This file is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1, or (at your
 * option) any later version.
 *
 * This file is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 *
 */

/**
 *
 * @author Catalin - www.itcsolutions.eu
 * @version 2011
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Implementation of AES
 * Bouncy Castle API installed as a JCA provider
 * CBC mode for encryption and decryption
 * @author Catalin Boja
 */

public class BouncyEncryption {

    // The default block size
    public static int blockSize = 16;

    Cipher encryptCipher = null;
    Cipher decryptCipher = null;
    
    // Buffer used to transport the bytes from one stream to another
    byte[] buf = new byte[blockSize];       //input buffer
    byte[] obuf = new byte[512];            //output buffer

    // The key
    byte[] key = null;
    // The initialization vector needed by the CBC mode
    byte[] IV = null;

    public BouncyEncryption(int rand, int SK) throws NoSuchAlgorithmException{
        //for a 192 key you must install the unrestricted policy files
        //  from the JCE/JDK downloads page
    	
    	//Hash the rand and SK using SHA-256 and trunctuate to 128 bytes. This is our A8
    	StringBuilder sb = new StringBuilder();
    	sb.append(rand);
		sb.append(SK);
		
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		key = digest.digest((sb.toString()).getBytes(StandardCharsets.UTF_8));
		//key = Arrays.copyOfRange(key, 0, 127);
		
        //default IV value initialized with 0
        IV = new byte[blockSize];
    }

    public BouncyEncryption(String pass, byte[] iv){
        //get the key and the IV
        key = pass.getBytes();
        IV = new byte[blockSize];
        System.arraycopy(iv, 0 , IV, 0, iv.length);
    }
    public BouncyEncryption(byte[] pass, byte[]iv){
        //get the key and the IV
        key = new byte[pass.length];
        System.arraycopy(pass, 0 , key, 0, pass.length);
        IV = new byte[blockSize];
        System.arraycopy(iv, 0 , IV, 0, iv.length);
    }

    public void InitCiphers()
            throws NoSuchAlgorithmException,
            NoSuchProviderException,
            NoSuchProviderException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException{
       //1. create the cipher using Bouncy Castle Provider
       encryptCipher =
               Cipher.getInstance("AES/CBC/NoPadding", "BC");
       //2. create the key
       SecretKey keyValue = new SecretKeySpec(key,"AES");
       //3. create the IV
       AlgorithmParameterSpec IVspec = new IvParameterSpec(IV);
       //4. init the cipher
       encryptCipher.init(Cipher.ENCRYPT_MODE, keyValue, IVspec);

       //1 create the cipher
       decryptCipher =
               Cipher.getInstance("AES/CBC/NoPadding", "BC");
       //2. the key is already created
       //3. the IV is already created
       //4. init the cipher
       decryptCipher.init(Cipher.DECRYPT_MODE, keyValue, IVspec);
    }

    public void ResetCiphers()
    {
        encryptCipher=null;
        decryptCipher=null;
    }

    public void CBCEncrypt(InputStream fis, OutputStream fos)
            throws IOException,
            ShortBufferException,
            IllegalBlockSizeException,
            BadPaddingException
    {
       //optionally put the IV at the beggining of the cipher file
       //fos.write(IV, 0, IV.length);

       byte[] buffer = new byte[blockSize];
       int noBytes = 0;
       byte[] cipherBlock =
               new byte[encryptCipher.getOutputSize(buffer.length)];
       int cipherBytes;
       while((noBytes = fis.read(buffer))!=-1)
       {
           cipherBytes =
                   encryptCipher.update(buffer, 0, noBytes, cipherBlock);
           fos.write(cipherBlock, 0, cipherBytes);
       }
       //always call doFinal
       cipherBytes = encryptCipher.doFinal(cipherBlock,0);
       fos.write(cipherBlock,0,cipherBytes);

       //close the files
       fos.close();
       fis.close();
    }
    public void CBCDecrypt(InputStream fis, OutputStream fos)
            throws IOException,
            ShortBufferException,
            IllegalBlockSizeException,
            BadPaddingException
    {
       // get the IV from the file
       // DO NOT FORGET TO reinit the cipher with the IV
       //fis.read(IV,0,IV.length);
       //this.InitCiphers();

       byte[] buffer = new byte[blockSize];
       int noBytes = 0;
       byte[] cipherBlock =
               new byte[decryptCipher.getOutputSize(buffer.length)];
       int cipherBytes;
       while((noBytes = fis.read(buffer))!=-1)
       {
           cipherBytes =
                   decryptCipher.update(buffer, 0, noBytes, cipherBlock);
           fos.write(cipherBlock, 0, cipherBytes);
       }
       //allways call doFinal
       cipherBytes = decryptCipher.doFinal(cipherBlock,0);
       fos.write(cipherBlock,0,cipherBytes);

       //close the files
       fos.close();
       fis.close();
    }
    
    public byte[] Encrypt(String s) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException, IOException
    {
    	String sn = s + "\u0003";
    	InputStream fis = new ByteArrayInputStream(sn.getBytes());
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	
    	CBCEncrypt(fis, bos);
    	byte[] buffer = bos.toByteArray();
    	return buffer;
    }
    
    public String Decrypt(byte[] encrypted) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException, IOException
    {
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	ByteArrayInputStream bis = new ByteArrayInputStream(encrypted);

    	
    	CBCDecrypt(bis,bos);
    	String unsplit = new String(bos.toByteArray());
    	String[] splitstrings = unsplit.split("\u0003");
    	
    	String strdecrypt = new String(splitstrings[0].getBytes());
    	return strdecrypt;
    }
    
}
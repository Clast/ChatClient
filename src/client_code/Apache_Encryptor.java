package client_code;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.cipher.CryptoCipherFactory;
import org.apache.commons.crypto.cipher.CryptoCipherFactory.CipherProvider;
import org.apache.commons.crypto.utils.Utils;
import org.jasypt.util.binary.BasicBinaryEncryptor;



/*
 * This class implements the functionality for our encryption and decryption operations. This encryptor works on byte arrays
 * Heavily based on the CipherByteArrayExample provided by Apache at : https://commons.apache.org/proper/commons-crypto/xref-test/org/apache/commons/crypto/examples/CipherByteArrayExample.html
 */
public class Apache_Encryptor 
{
	private SecretKeySpec 	key 		= null;
	private IvParameterSpec iv 			= null;
	private Properties 		properties 	= new 	Properties();
	final 	String 			transform 	= "AES/CBC/PKCS5Padding";
	private CryptoCipher 	encipher 	= null;
	private CryptoCipher 	decipher 	= null;
	BasicBinaryEncryptor binaryEncryptor = null;
	
	public Apache_Encryptor(int rand, int sk) throws NoSuchAlgorithmException, IOException
	{
		//Finish initializing needed variables for the encryptor
		properties.setProperty(CryptoCipherFactory.CLASSES_KEY, CipherProvider.OPENSSL.getClassName());
		encipher = Utils.getCipherInstance(transform, properties);
		decipher = Utils.getCipherInstance(transform, properties);
		
		//Hash the rand and SK using SHA-256. This is our A8
    	StringBuilder sb = new StringBuilder();
    	sb.append(rand);
		sb.append(sk);
		
		MessageDigest 	digest 	= MessageDigest	.getInstance("SHA-256");
		byte[] 			tempKey = digest		.digest((sb.toString()).getBytes(StandardCharsets.UTF_8));
		
		key = new SecretKeySpec(tempKey,"AES");
		iv 	= new IvParameterSpec(tempKey);
		try {encipher.init(Cipher.ENCRYPT_MODE, key, iv);} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {System.out.println("Could not initialize encipher object");e.printStackTrace();}
		try {decipher.init(Cipher.DECRYPT_MODE, key, iv);} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {System.out.println("Could not initialize decipher object");e.printStackTrace();}
	}
	
	public byte[] Encrypt(String data) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException
	{
		//initialize and fill the byte arrays needed
		byte[] input 	= data.getBytes(StandardCharsets.UTF_8);
		byte[] output 	= new byte[data.length() * 10];
		
		//Encrypt the data
		int updateBytes = encipher.update(input, 0, input.length, output, 0);
		int finalBytes 	= encipher.doFinal(input, 0, 0, output, updateBytes);
		
		return output;
	}
	
	public String Decrypt(byte[] data) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException
	{
		byte[] decoded = new byte[data.length * 2];
		decipher.doFinal(data, 0, data.length, decoded, 0);
		
		return new String(decoded,StandardCharsets.UTF_8 );
	}
	
	
}

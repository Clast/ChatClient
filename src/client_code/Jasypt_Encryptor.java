package client_code;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;


import org.jasypt.util.binary.BasicBinaryEncryptor;

public class Jasypt_Encryptor 
{
		BasicBinaryEncryptor encryptor = null;
		
		public Jasypt_Encryptor(int rand, int sk) throws NoSuchAlgorithmException, IOException
		{
			//Finish initializing needed variables for the encryptor
			//Hash the rand and SK using SHA-256. This is our A8
	    	StringBuilder sb = new StringBuilder();
	    	sb.append(rand);
			sb.append(sk);
			
			MessageDigest 	digest 	= MessageDigest	.getInstance("SHA-256");
			byte[] 			tempKey = digest		.digest((sb.toString()).getBytes(StandardCharsets.UTF_8));
			encryptor = new BasicBinaryEncryptor();
			encryptor.setPassword(new String(tempKey,StandardCharsets.UTF_8));
		}
		
		public byte[] Encrypt(String data) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException
		{
			//initialize and fill the byte arrays needed
			byte[] input 	= data.getBytes(StandardCharsets.UTF_8);
			
			byte[] output = encryptor.encrypt(input);
			
			return output;
		}
		
		public String Decrypt(byte[] data) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException
		{
			return new String(encryptor.decrypt(data),StandardCharsets.UTF_8 );
		}
}

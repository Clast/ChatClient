package client_code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class MessageParser extends Thread
{
	BlockingQueue<InternalMessage> 			outQueue 		= null;
	private BufferedReader 					in 				= null;
	private static Cipher					cipher			= null;
	private static Cipher					dcipher			= null;
	
	public MessageParser(BlockingQueue<InternalMessage> outQueue,BufferedReader in, Cipher cipher, Cipher dcipher)
	{
		this.outQueue 	= 	outQueue;
		this.in			= 	in;
		this.cipher		= 	cipher;
		this.dcipher	=	dcipher;
	}
	
	public void run()
	{
		while(true)
		{
			//String decryptMess = null;
			String mess = null;
			try {mess = decrypt(in.readLine());} catch (IOException | IllegalBlockSizeException | BadPaddingException e1) {System.out.println("In Message_Parser: Socket closed/Message failed to be recieved");return;}
			//try {decryptMess = encryptor.Decrypt(mess.getBytes());} catch (ShortBufferException | IllegalBlockSizeException | BadPaddingException | IOException e1) {e1.printStackTrace();}
			String[] splitMess = mess.split("\u001e");
			
			switch(splitMess[0])
			{
			case"CHAT_STARTED":	try {outQueue.put(new InternalMessage(splitMess[0],null,splitMess[1],splitMess[2], false));} catch (InterruptedException e) {e.printStackTrace();}
								break;
			case"UNREACHABLE": 	try {outQueue.put(new InternalMessage(splitMess[0], null,null,splitMess[1],false));} catch (InterruptedException e) {e.printStackTrace();}
								break;
			case"END_NOTIF":	try {outQueue.put(new InternalMessage(splitMess[0],null,splitMess[1],null,false));} catch (InterruptedException e) {e.printStackTrace();}
								break;
			case"CHAT":			try {outQueue.put(new InternalMessage(splitMess[0],splitMess[2],splitMess[1],null,false));} catch (InterruptedException e) {e.printStackTrace();}
								break;
			case"HISTORY_RESP":	try {outQueue.put(new InternalMessage(splitMess[0],splitMess[2],null,splitMess[1],false));} catch (InterruptedException e) {e.printStackTrace();}
								break;
			}
		}
	}
	
	public static String decrypt(String strToDecrypt) throws IllegalBlockSizeException, BadPaddingException
	{
		byte[] encryptedTextByte = Base64.decode(strToDecrypt);
		byte[] decryptedByte = dcipher.doFinal(encryptedTextByte);
		String decryptedText = new String(decryptedByte);
		return decryptedText;
	}
}

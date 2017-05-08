package client_code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

public class MessageParser extends Thread
{
	BlockingQueue<InternalMessage> 	outQueue 		= null;
	private BufferedReader 			in 				= null;
	
	public MessageParser(BlockingQueue<InternalMessage> outQueue,BufferedReader in)
	{
		this.outQueue 	= 	outQueue;
		this.in			= 	in;
	}
	
	public void run()
	{
		while(true)
		{
			//String decryptMess = null;
			String mess = null;
			try {mess = in.readLine();} catch (IOException e1) {e1.printStackTrace();}
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
}

package client_code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

public class MessageParser extends Thread
{
	BlockingQueue<InternalMessage> 	outQueue 		= null;
	private BouncyEncryption 		encryptor 		= null;
	private BufferedReader 			in 				= null;
	private InputStream 			inStream		= null;
	
	public MessageParser(BlockingQueue<InternalMessage> outQueue,BufferedReader in, BouncyEncryption encryptor, InputStream inStream)
	{
		this.encryptor 	= 	encryptor;
		this.outQueue 	= 	outQueue;
		this.in			= 	in;
		this.inStream	=	inStream;
	}
	
	public void run()
	{
		while(true)
		{
			String mess = null;
			byte[] message = null;
			message = readFromStream(inStream);
			//try {mess = in.readLine();} catch (IOException e1) {e1.printStackTrace();}
			try {mess = encryptor.Decrypt(message);} catch (ShortBufferException | IllegalBlockSizeException | BadPaddingException | IOException e1) {e1.printStackTrace();}
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
	
	private byte[] readFromStream(java.io.InputStream inputStream)
	{
		byte[] messLength = new byte[4];
		
		try {inputStream.read(messLength);} catch (IOException e) {System.out.println("In Message_Parser: Could not read Message length from input stream");e.printStackTrace();}
		
		byte[] message = new byte[ByteBuffer.wrap(messLength).getInt()];
		
		try {inputStream.read(message);} catch (IOException e) {System.out.println("In Message_Parser: Could not read Message from input stream");e.printStackTrace();}
		
		return message;
		
	}
}

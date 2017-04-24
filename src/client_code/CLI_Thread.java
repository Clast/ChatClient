package client_code;

import java.io.BufferedReader;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class CLI_Thread extends Thread
{
	private Scanner 			in 					= new Scanner(System.in);
	BlockingQueue<InternalMessage> 	outQueue 		= null;
	
	public CLI_Thread(BlockingQueue<InternalMessage> outQueue)
	{
		this.outQueue = outQueue;
	}

	public void run()
	{
		while(true)
		{
			String input = in.nextLine();
			input = input.toUpperCase();
			
			switch(input)
			{
			case"CHAT REQUEST":	System.out.println("Enter the user to connect to: ");
								String user = in.nextLine();
								try {outQueue.put(new InternalMessage("CHAT_REQUEST",null,null,user,true));} catch (InterruptedException e) {e.printStackTrace();}
								break;
			case"CHAT":			System.out.println("Enter message: ");
								String message = in.nextLine();
								try {outQueue.put(new InternalMessage(input,message,null,null,true));} catch (InterruptedException e) {e.printStackTrace();}
								break;
			case"HISTORY REQ":	System.out.println("Enter the desired user: ");
								String inputUser = in.nextLine();
								try {outQueue.put(new InternalMessage("HISTORY_REQ",null,null,inputUser,true));} catch (InterruptedException e) {e.printStackTrace();}
								break;
			case"END":			try {outQueue.put(new InternalMessage("END_REQUEST",null,null,null,true));} catch (InterruptedException e) {e.printStackTrace();}
								break;
			case"LOG OFF":		try {outQueue.put(new InternalMessage("LOG_OFF",null,null,null,true));} catch (InterruptedException e) {e.printStackTrace();}
								break;
			case"HELP":			System.out.println("Usage options as follows(Mixed case allowed):\n		Chat Request\n		Chat\n		History Req\n		end\n		Log Off\n		Help");
								break;
			default:			System.out.println("Command not recognized. Type \"help\" for usage options.");	
								break;
			}
		}
	}
}

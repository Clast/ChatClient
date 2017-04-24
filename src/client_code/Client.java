package client_code;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.math.BigInteger;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;


public class Client{

	private DatagramSocket datagramSocket ;
	private InetAddress address;
	private int port;
	private DatagramPacket packet;
	private byte[] buffer;
	private String username = null;
	private String clientID;
	private int secretkey = 123456;
	private BouncyEncryption encryptor = null;
	private int cookie;
	private boolean connected;
	private boolean serverConnect;
	private Scanner scanner;
	private Socket clientSocket;
	private BufferedReader 	in 				= null;
	private PrintWriter     out 			= null;
	private BlockingQueue<InternalMessage> actionQueue = new LinkedBlockingQueue<InternalMessage>();
	boolean inChat	=	false;

	Client ()throws SocketException, UnknownHostException{
		buffer = new byte[1024];
		datagramSocket = new DatagramSocket();
		address = InetAddress.getLocalHost();
		packet = new DatagramPacket(buffer, buffer.length, address, 8888);
		scanner = new Scanner (System.in);
	}

	public void sendLogin(String user)throws IOException, NoSuchAlgorithmException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException{
		// send "Hello" msg to SERVER
		System.out.println("Sending HELLO");
		byte[] buffer = user.getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
		// send packet
		datagramSocket.send(packet);

		// receive auth
		datagramSocket.receive(packet);
		String s = unpack(packet);
		// check to see if user exists on the server
    if (!s.contains("User")){
    	int rand = Integer.parseInt(s);
    	int key = rand + secretkey;

    	StringBuilder sb = new StringBuilder();
		sb.append(rand);
		sb.append(secretkey);
		key = Integer.parseInt(sb.toString());
		
		encryptor = new BouncyEncryption(rand,secretkey);
		encryptor.InitCiphers();
		
		
	
    
    	byte[] password = BigInteger.valueOf(key).toByteArray();
	
		MessageDigest md = MessageDigest.getInstance("MD5");
    	md.update(password);	
    	byte[] challenge = new byte[1024];
    	challenge = md.digest();
    
    	DatagramPacket cresponse = new DatagramPacket(
    	        challenge, challenge.length, address, 4445
    	        );
    
    	datagramSocket.send(cresponse);
    	buffer = new byte[1024];
    	packet = new DatagramPacket(
            buffer, buffer.length, address, 8888
            );
    	datagramSocket.receive(packet);
    
    	String strdecrypt = encryptor.Decrypt(packet.getData());
    	String [] str = strdecrypt.split(",");
   		cookie = Integer.parseInt(str[0]);
    	System.out.println(strdecrypt);

    	
    	// establish TCP connection
    	clientSocket = new Socket("localhost", Integer.parseInt(str[1]));
    	try {in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));} 	catch (IOException e) {System.out.println("In TCP_Welcome_Thread: Unable to create Buffered Reader");e.printStackTrace();}
		try {out = new PrintWriter(clientSocket.getOutputStream(), true);} 						catch (IOException e) {System.out.println("In TCP_Welcome_Thread: unable to create PrintWriter");e.printStackTrace();}
		out.println("CONNECT\u001e" + cookie);

		System.out.println(in.readLine());
    	serverConnect = true;
    }
    else{
    	/*System.out.println("User DNE");
    	// exit
    	datagramSocket.receive(packet);
    	String strdecrypt = encryptor.Decrypt(packet.getData());
    	System.out.println(strdecrypt);
    	System.exit(0);
    	*/
    }

	}
                 
	public void chatRequest()throws IOException{
		String second_client;
		//Create the input and output streams so that we can communicate with the client
		
		// request connection with 2nd chat client
		 do {
		 	second_client = scanner.nextLine();
		 	// use bouncy encryption???

		 	out.println("CHAT_REQUEST\u001e" + second_client);
		 	System.out.println("dsf1");
		 	String msgRcv = in.readLine();
		 	System.out.println(msgRcv);
		 	String [] msgSplit = msgRcv.split("\u001e");
		 	/*
		 	DatagramPacket packet = Packet_Helpers.stringToPacket(second_client, address, 4445);
		 	datagramSocket.send(packet);
		 	// receive something from server and decrypt
		 	datagramSocket.receive(packet);
		 	String a = unpack(packet);
		 	String [] msgSplit = a.split("\u001e");
		 	// CHAT_STARTED\u001e{SESSION_ID}\u001e{CLIENT_B}*/
		 	if (msgSplit[0].equals("CHAT_STARTED")){
		 		System.out.println(msgSplit[0] + " " + msgSplit[1] + " " + msgSplit[2]);
		 		connected = true;
		 	}	 
		 	else 
		 		System.out.println("User DNE, try again");

		 }while (!connected);
		
	}

	public void sendMSG() throws ShortBufferException, IllegalBlockSizeException, BadPaddingException,
	IOException{
		// send message to server
		String msg;
		 do {
			System.out.print("[You]:    ");
			msg = scanner.nextLine();
			System.out.println (msg);
			if (msg.equals("End Chat") == true)
				 break;
			
			else
				{
					// else send msg to server
					//encrpyton
					out.println(msg);
				}
			}
			 while (true);

		// close TCP connections
		//endChat();
		System.out.println("Chat ended");
	}

	public void rcvMSG(DatagramPacket msg) throws IOException{
		datagramSocket.receive(packet);
		// gets msg
		String rcv = unpack(msg);
		// switch (rcv)??
			//case 1: 

			//case 2: 

			//case 3: ENDMSG
				// endChat();

			//case 4: EXIT
				// sendQuit();
	}

	public void endChat(){
			// end chat with 2nd client, but still maintain connection with UDP.
	}

	public void sendQuit(){
		// disconnect from server when user types "Log off"
	}

	public static String unpack(DatagramPacket p1)
	{
		// upaacks datagram into readable format
		String str = new String(
		p1.getData(),
		p1.getOffset(),
		p1.getLength(),
		StandardCharsets.UTF_8 // or some other charset
	);
	return str;
	}
	
	private void control(Client a) throws UnknownHostException, SocketException, IOException, NoSuchAlgorithmException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException
	{
		while(true)
		{
			InternalMessage action = null;
			try {action = actionQueue.take();} catch (InterruptedException e) {e.printStackTrace();}
			
			switch(action.getAction())
			{
			case	"CHAT_REQUEST":	
									break;
			}
			
			
		}
	}
	
public static void main(String[] args) throws UnknownHostException, SocketException, IOException, NoSuchAlgorithmException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException  {

	Client a = new Client();
	a.sendLogin("UserA");
	a.chatRequest();
}

}
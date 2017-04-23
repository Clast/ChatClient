package client_code;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
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
	private DatagramPacket packet;
	private byte[] buffer;
	private String username = "UserA";
	private String clientID;
	private int secretkey = 123456;
	private BouncyEncryption encryptor = null;
	private boolean connected;
	private boolean serverConnect;
	private Scanner scanner;

	Client ()throws SocketException, UnknownHostException{
		buffer = new byte[1024];
		datagramSocket = new DatagramSocket();
		address = InetAddress.getLocalHost();
		packet = new DatagramPacket(buffer, buffer.length, address, 8888);
		scanner = new Scanner (System.in);
	}

	public void sendLogin()throws IOException, NoSuchAlgorithmException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException{
		// send "Hello" msg to SERVER
		System.out.println("Sending HELLO");
		byte[] buffer = username.getBytes();
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
   
    	System.out.println(strdecrypt);
    	serverConnect = true;
    }
    else{
    	System.out.println("User DNE");
    	// exit
    	System.exit(0);
    }

	}
                 
	public void chatRequest()throws IOException{
		String second_client;
		// request connection with 2nd chat client
		 do {
		 	second_client = scanner.nextLine();
		 	// use bouncy encryption???


		 	DatagramPacket packet = Packet_Helpers.stringToPacket(second_client, address, 4445);
		 	datagramSocket.send(packet);
		 	// receive something from server 

		 	//if 2nd client is avaliable
		 		// connected = true

		 	// else "User DNE, try again"
		 }while (!connected);



		

		System.out.println ("Chat Started with " + second_client);
		
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
					byte [] encrypteddata = null;
					encrypteddata = encryptor.Encrypt(msg);
					packet = Packet_Helpers.arrayToPacket(encrypteddata, address, 4445);

					try {
						datagramSocket.send(packet);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

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
public static void main(String[] args) throws UnknownHostException, SocketException, IOException, NoSuchAlgorithmException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException  {

	Client a = new Client();
	a.sendLogin();

}

}
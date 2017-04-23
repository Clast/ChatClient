package client_code;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Client{

	private DatagramSocket datagramSocket ;
	private InetAddress address;
	private DatagramPacket packet;
	private byte[] buffer;
	private String username = "UserA";
	private String clientID;
	private int secretkey = 123456;
	private BouncyEncryption encryptor = null;

	Client ()throws SocketException, UnknownHostException{
		buffer = new byte[1024];
		datagramSocket = new DatagramSocket();
		address = InetAddress.getLocalHost();
		packet = new DatagramPacket(buffer, buffer.length, address, 8888);
		Security.addProvider(new BouncyCastleProvider());

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
	}
                 
	public void chatRequest(String client) throws ShortBufferException, IllegalBlockSizeException,
	BadPaddingException, IOException{
		// request connection with 2nd chat client
		try{
		byte [] buffer = encryptor.Encrypt(client);		
		// send TCP packet

		// check to see if 2nd client in conencted to Server and avaliable
		// receive TCP packet response

		//String response = encryptor.Decrypt(packet.getData());

		// if response == online
		System.out.println ("Chat Started with " + client);
		// else
		System.out.println ("Client " + client + "is not avaliable!");
		}
		catch (ShortBufferException e) {
		}
		catch (IllegalBlockSizeException e){
		}
		catch (BadPaddingException e){
		}
		catch (IOException e){

		}
	}

	public void sendMSG(String msg){
		// send message to server
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
			// end chat with 2nd client, but still maintain UDP connection.
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
package client_code;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Client{

	private DatagramSocket datagramSocket ;
	private InetAddress address;
	private DatagramPacket packet;
	private byte[] buffer;
	private String username = "UserA";
	private String clientID;
	private String secretkey = "78116FAE48C81446C41E9A3062E6206F";

	Client ()throws SocketException, UnknownHostException{
		buffer = new byte[1024];
		datagramSocket = new DatagramSocket();
		address = InetAddress.getLocalHost();
		packet = new DatagramPacket(buffer, buffer.length, address, 8888);
	}

	public void sendLogin()throws IOException, NoSuchAlgorithmException{
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
    	
    	String key = rand + secretkey;

    	StringBuilder sb = new StringBuilder();
		sb.append(rand);
		sb.append(secretkey);

	
    
    	byte[] password = sb.toString().getBytes();
	
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
    
    	String cResponse = unpack(packet);

    	System.out.println(cResponse);
	}

	public void chatRequest(String client){
		// request connection with 2nd chat client

		System.out.println ("Chat Started with " + client);
	}

	public void sendMSG(String msg){
		// send message to
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
public static void main(String[] args) throws UnknownHostException, SocketException, IOException, NoSuchAlgorithmException  {

	Client a = new Client();

	a.sendLogin();

}

}
//Client submitting a UDP packet
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

public class TestClient 
{

public static void main(String[] args) throws UnknownHostException, SocketException, IOException, NoSuchAlgorithmException  {
    // TODO code application logic here
	DatagramSocket datagramSocket = new DatagramSocket();
    
	InetAddress address = InetAddress.getLocalHost();
	String username = new String ("UserA");
	int secretkey = 123456;
	
	byte[] buffer = new byte[1024];
	buffer = username.getBytes();
	
	DatagramPacket packet = new DatagramPacket(
            buffer, buffer.length, address, 8888
            );
	

    
    
    System.out.println(address);
    sendHello(address,username,datagramSocket);
    
    datagramSocket.receive(packet);
    String s = unpack(packet);
    
    int rand = Integer.parseInt(s);
    int key = rand + secretkey;

    
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
    
    String cResponse = unpack(packet);

    
    
    
    

    

}

public static String unpack(DatagramPacket p1)
{
	String str = new String(
		    p1.getData(),
		    p1.getOffset(),
		    p1.getLength(),
		    StandardCharsets.UTF_8 // or some other charset
		);
	return str;
}

public static void sendHello(InetAddress address, String username, DatagramSocket datagramSocket) throws IOException
{
	System.out.println("Sending HELLO");
	byte[] buffer = username.getBytes();
	DatagramPacket packet = new DatagramPacket(
            buffer, buffer.length, address, 4445
            );

	datagramSocket.send(packet);
	
}




}
package client_code;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class Packet_Helpers {



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

public static DatagramPacket stringToPacket (String s1, InetAddress address, int port)
{
	byte[] buffer = s1.getBytes();

	DatagramPacket packet = new DatagramPacket(
	        buffer, buffer.length, address, port
	        );

	return packet;
}

public static DatagramPacket streamToPacket (ByteArrayOutputStream bos, InetAddress address, int port)
{
	
	byte[] buffer = bos.toByteArray();
	DatagramPacket packet = new DatagramPacket(
	        buffer, buffer.length, address, port
	        );
	
	return packet;
	
}

public static DatagramPacket arrayToPacket (byte[] bytestuff, InetAddress address, int port)
{
	
	DatagramPacket packet = new DatagramPacket(
	        bytestuff, bytestuff.length, address, port
	        );
	
	return packet;
}


}
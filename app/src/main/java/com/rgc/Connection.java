package com.rgc;

import java.io.*;
import java.net.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Connection {
	Crypt c;
	String sendString(String ip, int port, String pass, String message, int receivesize, String enc_key) throws IOException,Exception{
		String modifiedSentence = "CONECTION ERROR";
	 DatagramSocket clientSocket = new DatagramSocket();
		 clientSocket.setSoTimeout(5000);
		 InetAddress IPAddress = InetAddress.getByName(ip);
		 byte[] sendData ;//= new byte[1024];
		 byte[] receiveData = new byte[receivesize];
		 c = new Crypt();
		 if (pass.isEmpty() == false)
			{try {
				message = c.encrypt_string(message, enc_key);
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}}
		 
		 sendData = (pass+";"+message+";").getBytes();
		 DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
		 clientSocket.send(sendPacket);
		 DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		 clientSocket.receive(receivePacket);
		 modifiedSentence = new String(receivePacket.getData());
		 List<String> list = new ArrayList<String>(Arrays.asList(modifiedSentence.split(";")));
		 String ifenc = list.get(0).toString();
		 String encSentence = modifiedSentence;
		 clientSocket.close();
		 if (ifenc.equals("1"))
			{try {
				modifiedSentence = c.decrypt_string(list.get(1), enc_key);
			} catch (InvalidKeyException e) {
				modifiedSentence = encSentence;
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				modifiedSentence = encSentence;
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				modifiedSentence = encSentence;
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				modifiedSentence = encSentence;
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				modifiedSentence = encSentence;
				e.printStackTrace();
			} catch (BadPaddingException e) {
				modifiedSentence = encSentence;
				e.printStackTrace();
			} catch (IOException e) {
				modifiedSentence = encSentence;
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				modifiedSentence = encSentence;
				e.printStackTrace();
			}}
		 //else if (ifenc.equals("0")) modifiedSentence = list.get(1);
		 
		 return modifiedSentence;
	 }
	
	
	
	
}
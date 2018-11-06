package com.rgc;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.webkit.URLUtil;
import java.io.*;
import java.net.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.InflaterInputStream;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Connection implements Parcelable {
    Crypt c;
    private DatagramSocket clientSocket;
    private Socket s;
    private InetAddress IPAddress;
    public boolean isConnecting = false,tcpOnly = false;
    public String ip,pass,enc_key;
    public int timeout = 20000;
    public int port;
    BufferedReader in;
    PrintWriter out;

    Connection(String ip, int port, String pass, String enc_key, boolean tcpOnly){
        this.ip = ip;
        this.port = port;
        this.pass = pass;
        this.enc_key = enc_key;
        this.tcpOnly = tcpOnly;
    }

    public void cancel(){
        if(clientSocket != null)
            if(!clientSocket.isClosed())
                clientSocket.close();
        if(s != null)
            if(!s.isClosed()){
            try{
                s.close();
            }catch (IOException e){}
        }
        if(out!=null){
            out.flush();
            out.close();
        }
        isConnecting = false;
    }

    public String sendString(String message, int receivesize) throws Exception {
        if(tcpOnly) return sendStringTCP(message,true);
        else return sendStringUDP(message,receivesize);
    }
    public void sendString(String message) throws Exception {
        if(tcpOnly) sendStringTCP(message,false);
        else sendStringUDP(message);
    }

    public String sendStringUDP(String message, int receivesize) throws Exception {
        //clientSocket.close();
        isConnecting = true;
        String modifiedSentence = "CONNECTION ERROR";
        clientSocket = new DatagramSocket();
        clientSocket.setSoTimeout(20000);
        if(URLUtil.isValidUrl(ip))
            IPAddress = InetAddress.getByName(new URL(ip).getHost());
        else
            IPAddress = InetAddress.getByName(ip);
        byte[] sendData;//= new byte[1024];
        byte[] receiveData = new byte[receivesize];
        message = encrypt(message);
        sendData = (pass + ";" + message + ";").getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        clientSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        modifiedSentence = new String(receivePacket.getData());
        if(receivesize > 60000)
            modifiedSentence =  decompress(modifiedSentence.getBytes());

        clientSocket.close();
        modifiedSentence = decrypt(modifiedSentence);
        isConnecting = false;
        return modifiedSentence;
    }

//    public static String decompress(byte[] compressed) throws IOException {
//        final int BUFFER_SIZE = 32;
//        ByteArrayInputStream is = new ByteArrayInputStream(Base64.decode(compressed, Base64.DEFAULT));
//        GZIPInputStream iis  = new GZIPInputStream(is);
//        StringBuilder string = new StringBuilder();
//        byte[] data = new byte[BUFFER_SIZE];
//        int bytesRead;
//        while ((bytesRead = iis.read(data)) != -1) {
//            string.append(new String(data, 0, bytesRead));
//        }
//        iis.close();
//        is.close();
//        return string.toString();
//    }
//    public static String decompress2(byte[] compressed) throws IOException {
//        final int BUFFER_SIZE = 32;
//        ByteArrayInputStream is = new ByteArrayInputStream(Base64.decode(compressed, Base64.DEFAULT));
//        BZip2CompressorInputStream iis  = new BZip2CompressorInputStream(is);
//        StringBuilder string = new StringBuilder();
//        byte[] data = new byte[BUFFER_SIZE];
//        int bytesRead;
//        while ((bytesRead = iis.read(data)) != -1) {
//            string.append(new String(data, 0, bytesRead));
//        }
//        iis.close();
//        is.close();
//        return string.toString();
//    }
    public static String decompress(byte[] compressed) throws IOException {
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(Base64.decode(compressed, Base64.DEFAULT));
        InflaterInputStream iis = new InflaterInputStream(is);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = iis.read(data)) != -1) {
            string.append(new String(data, 0, bytesRead));
        }
        iis.close();
        is.close();
        return string.toString();
    }

    public void sendStringUDP(String message) throws Exception {
        isConnecting = true;
        clientSocket = new DatagramSocket();
        clientSocket.setSoTimeout(timeout);
        if(URLUtil.isValidUrl(ip))
            IPAddress = InetAddress.getByName(new URL(ip).getHost());
        else
            IPAddress = InetAddress.getByName(ip);
        byte[] sendData;
        message = encrypt(message);
        sendData = (pass + ";" + message + ";").getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        clientSocket.send(sendPacket);
        isConnecting = false;
    }

    public String sendStringTCP(String message, boolean waitForResponse) throws Exception {
        isConnecting = true;
        if(URLUtil.isValidUrl(ip))
            IPAddress = InetAddress.getByName(new URL(ip).getHost());
        else
            IPAddress = InetAddress.getByName(ip);
        s = new Socket(IPAddress, port);
        s.setSoTimeout(timeout);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
        message = encrypt(message);
        out.println(pass + ";" + message + ";#EOF#");
        out.flush();
        String inMsg = "0";
        if(waitForResponse){
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            inMsg = sb.toString();
            inMsg = decompress(inMsg.getBytes());
            inMsg = decrypt(inMsg);
        }
        s.close();
        out.close();
        isConnecting = false;
        return inMsg;
    }


    //parcel part
    public Connection(Parcel in){
        String[] data= new String[4];
        in.readStringArray(data);
        this.ip = data[0];
        this.port = Integer.parseInt(data[1]);
        this.pass = data[2];
        this.enc_key = data[3];
    }
    @Override
    public int describeContents() {
        return 0;
    }
    private String encrypt(String msg){
        if (!pass.isEmpty()) {
            c = new Crypt();
            try {
                msg = c.encrypt_string(msg, enc_key);
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
            }
        }
        return msg;
    }
    private String decrypt(String modifiedSentence){
        List<String> list = new ArrayList<String>(Arrays.asList(modifiedSentence.split(";")));
        String ifenc = list.get(0);
        String encSentence = modifiedSentence;
        c = new Crypt();
        if (ifenc.equals("1")) {
            try {
                modifiedSentence = c.decrypt_string(list.get(1), enc_key)+" ";
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
            }
        }
        return modifiedSentence;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.ip,String.valueOf(this.port),this.pass,this.enc_key});
    }

    public static final Parcelable.Creator<Connection> CREATOR= new Parcelable.Creator<Connection>() {

        @Override
        public Connection createFromParcel(Parcel source) {
            return new Connection(source);  //using parcelable constructor
        }

        @Override
        public Connection[] newArray(int size) {
            return new Connection[size];
        }
    };


}
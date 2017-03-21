package cn.diaovision.omnicontrol.conn;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by liulingfeng on 2017/3/19.
 */

public class UdpClient {
    String ip;
    int port;

    public UdpClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public byte[] send(byte[] bytes, int len){
        byte[] recvs = new byte[1024];
        try {
            DatagramSocket udpSkt = new DatagramSocket();
            InetAddress addr = InetAddress.getByName(this.ip);
            DatagramPacket packet = new DatagramPacket(bytes, len, addr, port);
            udpSkt.send(packet);
            DatagramPacket packetRecv = new DatagramPacket(recvs, recvs.length);
            udpSkt.receive(packetRecv);
            byte[] dataRecv = packetRecv.getData();
            udpSkt.close();
            return dataRecv;
        } catch (UnknownHostException e) {
                e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            //if any error, return null
            return null;
        }
    }
}

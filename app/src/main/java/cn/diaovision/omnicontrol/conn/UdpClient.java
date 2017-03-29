package cn.diaovision.omnicontrol.conn;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

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

    public byte[] send(final byte[] bytes, final int len){
        final String ip = this.ip;
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] recvs = new byte[1024];
                try {
                    DatagramSocket udpSkt = new DatagramSocket();
                    udpSkt.setSoTimeout(5); //set timeout 5 sec
                    InetAddress addr = InetAddress.getByName(ip);
                    DatagramPacket packet = new DatagramPacket(bytes, len, addr, port);
                    udpSkt.send(packet);
                    DatagramPacket packetRecv = new DatagramPacket(recvs, recvs.length);
                    udpSkt.receive(packetRecv);
                    byte[] dataRecv = packetRecv.getData();
                    udpSkt.close();
//                    return dataRecv;
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    //if any error, return zero-size byte array
                    int a = 0;
//                    return new byte[0];
                }
            }
        }).start();
//        byte[] recvs = new byte[1024];
//        try {
//            DatagramSocket udpSkt = new DatagramSocket();
//            udpSkt.setSoTimeout(5); //set timeout 5 sec
//            InetAddress addr = InetAddress.getByName(this.ip);
//            DatagramPacket packet = new DatagramPacket(bytes, len, addr, port);
//            udpSkt.send(packet);
//            DatagramPacket packetRecv = new DatagramPacket(recvs, recvs.length);
//            udpSkt.receive(packetRecv);
//            byte[] dataRecv = packetRecv.getData();
//            udpSkt.close();
//            return dataRecv;
//        } catch (UnknownHostException e) {
//                e.printStackTrace();
//        } catch (SocketException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        finally {
//            //if any error, return zero-size byte array
//            return new byte[0];
//        }

        return new byte[1];
    }
}

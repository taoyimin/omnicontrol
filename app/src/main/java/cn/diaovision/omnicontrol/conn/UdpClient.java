package cn.diaovision.omnicontrol.conn;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import cn.diaovision.omnicontrol.util.ByteUtils;

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

    public byte[] send(final byte[] bytes, final int len) {
        final String ip = this.ip;
        byte[] recvBuff = new byte[1024];
        byte[] recv = null;
        try {
            DatagramSocket udpSkt = new DatagramSocket();
            udpSkt.setSoTimeout(5000); //set timeout 5 sec
            InetAddress addr = InetAddress.getByName(ip);
            DatagramPacket packet = new DatagramPacket(bytes, len, addr, port);
            udpSkt.send(packet);
            DatagramPacket packetRecv = new DatagramPacket(recvBuff, recvBuff.length);
            udpSkt.receive(packetRecv);
            byte[] data = packetRecv.getData();
            int ll = packetRecv.getLength();
            recv = new byte[ll];
            System.arraycopy(data, 0, recv, 0, ll);
            udpSkt.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recv;
    }

    public List<byte[]> send(final byte[] bytes, final int len, boolean moreRecv) {
        final String ip = this.ip;
        byte[] recvBuff = new byte[1024];
        byte[] recv;
        List<byte[]> recvList = new ArrayList<>();
        try {
            DatagramSocket udpSkt = new DatagramSocket();
            udpSkt.setSoTimeout(5000); //set timeout 5 sec
            InetAddress addr = InetAddress.getByName(ip);
            DatagramPacket packet = new DatagramPacket(bytes, len, addr, port);
            udpSkt.send(packet);
            DatagramPacket packetRecv = new DatagramPacket(recvBuff, recvBuff.length);
            while (true){
                udpSkt.receive(packetRecv);
                byte[] data = packetRecv.getData();
                int ll = packetRecv.getLength();
                recv = new byte[ll];
                System.arraycopy(data, 0, recv, 0, ll);
                String result= ByteUtils.bytes2ascii(recv);
                recvList.add(recv);
                if("<Err>".equals(result)||result.endsWith("OK>")){
                    break;
                }
            }
            udpSkt.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return recvList;
        }
    }

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
}

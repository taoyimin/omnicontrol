import time
import datetime
import struct
import socket
import threading
import Queue
import message

from thread_ex import ThreadPool

class UdpClient:

    def __init__(self, server_ip, server_port, msg):
        self.addr = (server_ip, server_port)
        self.skt = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.skt.settimeout(5)
        self.msg = msg

    def go(self):
        try:
            self.skt.connect(self.addr)
            self.skt.send(self.msg)
            print 'message sent'
            ack = self.skt.recv(1024)
            self.skt.close()
            return ack
        except socket.timeout:
            self.skt.close()
            return 'no data received, timeout'
        except socket.error:
            self.skt.close()
            return 'error'

class Task:
    def __init__(self, id):
        self.id = id

    def go(self):
        print "task " + str(self.id) + " performed"

def main():
    # serverIp = '192.168.1.103'
    serverIp = '192.168.10.11'
    serverPort = 5000
    threadPool = ThreadPool(10)
    threadPool.start()

    for m in range(1):

        #For channel switch control
        # head = '\x01'
        # # payload = '\x30\x31' + '\x57\x52' + '\x4d' + '\x30\x30\x30' + '\x30\x30\x30'
        # payload = '\x30\x31' + '\x57\x52' + '\x4d' + '001' + '001'
        # tail = '\x04'
        # checksum = message.MatrixMessage.addChecksumToMessage(payload)
        # msg = head + payload + checksum + tail
        # print msg

        # #For id setup control
        # head = '\x01'
        # payload = 'FS' + '\x49\x44' + 'FS'
        # tail = '\x04'
        # checksum = message.MatrixMessage.addChecksumToMessage(payload)
        # msg = head + payload + checksum + tail
        # print checksum
        # print msg

        #For port inquiry
        # head = '\x01'
        # payload = '\x31\x36' + '\x52\x44' + '\x4d' + '\x30\x30\x32'
        # tail = '\x04'
        # checksum = message.MatrixMessage.addChecksumToMessage(payload)
        # msg = head + payload + checksum + tail
        # print checksum
        # print msg

        #for port lock
        # head = '\x01'
        # payload = '\x30\x31' + '\x4c\x4b' + '\x4d' + '\x30\x30\x32'
        # tail = '\x04'
        # checksum = message.MatrixMessage.addChecksumToMessage(payload)
        # msg = head + payload + checksum + tail
        # print checksum
        # print msg

        #for port unlock
        # head = '\x01'
        # payload = '\x30\x31' + '\x55\x4b' + '\x4d' + '\x30\x30\x32'
        # tail = '\x04'
        # checksum = message.MatrixMessage.addChecksumToMessage(payload)
        # msg = head + payload + checksum + tail
        # print checksum
        # print msg

        #for 1 v n switch
        # head = '\x01'
        # payload = '\x30\x31' + '\x57\x4d' + '\x4d' + '\x30\x30\x31' + '\x30\x30\x31' + '\x30\x30\x32' + '\x30\x30\x33'
        # tail = '\x04'
        # checksum = message.MatrixMessage.addChecksumToMessage(payload)
        # msg = head + payload + checksum + tail
        # print checksum
        # print msg

        #for read port resolution
        # head = '\x01'
        # payload = '\x30\x31' + '\x4d\x53' + 'O' + '\x30\x30\x32' + 'A' + '\x30\x30'
        # tail = '\x04'
        # checksum = message.MatrixMessage.addChecksumToMessage(payload)
        # msg = head + payload + checksum + tail
        # print checksum
        # print msg

        #for set port resolution
        # head = '\x01'
        # payload = '\x30\x31' + '\x4d\x53' + 'O' + '\x30\x30\x32' + '3' + '0A'
        # tail = '\x04'
        # checksum = message.MatrixMessage.addChecksumToMessage(payload)
        # msg = head + payload + checksum + tail
        # print checksum
        # print msg

        #pin jie write
        #switch should be called prior to pin jie
        # head = '\x01'
        # payload = '\x30\x31' + 'SP' + 'P' + 'W' + '01' + '01' + '\x30\x30\x32' + '\x30\x30\x31' + '\x30\x30\x33' + '\x30\x30\x34'
        # tail = '\x04'
        # checksum = message.MatrixMessage.addChecksumToMessage(payload)
        # msg = head + payload + checksum + tail
        # print checksum
        # print msg

        # for matrix chain control (not used)

        # for subtitle (zi4mu4) set
        # head = '\x01'
        # payload = '\x30\x31' + '\x4F\x53' + '\x30\x30\x31' + '0' + '01' + '\x00\x31'
        # tail = '\x04'
        # checksum = message.MatrixMessage.addChecksumToMessage(payload)
        # msg = head + payload + checksum + tail
        # print checksum
        # print msg

        # for set subtitle position, color and size
        # head = '\x01'
        # payload = '\x30\x31' + '\x4F\x43' + '\x30\x30\x31' + '04' + '3' + '0' + '00' + '0'
        # tail = '\x04'
        # checksum = message.MatrixMessage.addChecksumToMessage(payload)
        # msg = head + payload + checksum + tail
        # print checksum
        # print msg

        #inquiry camera address
        # head = '\x01'
        # payload = '\x30\x31' + 'CA' + '001' + 'FS'
        # tail = '\x04'
        # checksum = message.MatrixMessage.addChecksumToMessage(payload)
        # msg = head + payload + checksum + tail
        # print checksum
        # print msg

        #set camera address
        # head = '\x01'
        # payload = '01' + 'CA' + '001' + '01'
        # tail = '\x04'
        # checksum = message.MatrixMessage.addChecksumToMessage(payload)
        # msg = head + payload + checksum + tail
        # print checksum
        # print msg

        # for camera control (start)
        head = '\x01'
        payload = '\x30\x31' + 'CD' + '4' + '3' + '001' + '20' + '63' + '80'
        tail = '\x04'
        checksum = message.MatrixMessage.addChecksumToMessage(payload)
        msg = head + payload + checksum + tail
        print checksum
        print msg

        # for camera preset
        # head = '\x01'
        # payload = '\x31\x30' + 'CP' + '2' + '1' + '000' + '\x32' + '01'
        # tail = '\x04'
        # checksum = message.MatrixMessage.addChecksumToMessage(payload)
        # msg = head + payload + checksum + tail
        # print checksum
        # print msg


        # threadPool.submit(UdpClient(serverIp, serverPort, msg ))

        # for camera control (stop)
        # head = '\x01'
        # payload = '\x30\x31' + 'CS' + '4' + '3' + '001'
        # tail = '\x04'
        # checksum = message.MatrixMessage.addChecksumToMessage(payload)
        # msg = head + payload + checksum + tail
        # print checksum
        # print msg

        time.sleep(1)
        threadPool.submit(UdpClient(serverIp, serverPort, msg ))

# macroscript
if __name__ == '__main__':
    main()


import socket
import threading
import time
from message import  MatrixMessage
from thread_ex import ThreadPool

class TcpClient:
    def __init__(self, server_ip, server_port, msg):
        self.addr = (server_ip, server_port)
        self.skt = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.skt.settimeout(5)
        self.msg = msg

    def go(self):
        try:
            self.skt.connect(self.addr)
            self.skt.send(self.msg)
            ack = self.skt.recv(1024)
            self.skt.close()
            return ack
        except socket.timeout:
            self.skt.close()
            return 'timeout'
        except socket.error:
            self.skt.close()
            return 'error'

    def connect(self):
        try:
            self.skt.connect(self.addr)
            return 0
        except socket.error:
            self.skt.close()
            return -1

    def send(self, msg):
        try:
            self.skt.send(msg)
            return len(msg)
        except socket.error:
            return -1

    def recv(self):
        try:
            data = self.skt.recv(1024)
            return data
        except socket.timeout:
            return 'timeout'
        except socket.error:
            return 'error'

    def disconnect(self):
        self.skt.close()


def main():
    serverIp = '127.0.0.1'
    serverPort = 5000
    # threadPool = ThreadPool(10)
    # threadPool.start()

    client = TcpClient(serverIp, serverPort, 'hello')
    if client.connect() >= 0:

        head = '\x01'
        payload = '\x31\x36' + '\x4F\x43' + '\x30\x30\x31' + '10' + '2' + '0' + '00' + '0'
        tail = '\x04'
        checksum = MatrixMessage.addChecksumToMessage(payload)
        msgLogin = head + payload + checksum + tail

        msgSet = msgLogin

        # send a data and received
        res = client.send(msgLogin)
        if res < 0:
            return
        data = client.recv()
        if data == 'timeout' or data =='error':
            return
        print str(data)

        # send a data and received
        res = client.send(msgSet)
        if res < 0:
            return
        data = client.recv()
        if data == 'timeout' or data == 'error':
            return
        print str(data)


    # for m in range(200):
    #     threadPool.submit(TcpClient(serverIp, serverPort, MatrixMessage.createLoginMessage('test').toByteArray()))

# macroscript
if __name__ == '__main__':
    main()

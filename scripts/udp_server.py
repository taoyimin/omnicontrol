import socket
import threading


class UdpServer(threading.Thread):

    def __init__(self, port):
        threading.Thread.__init__(self)
        self.addr = ('', port)
        self.thread = threading.Thread()
        self.server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.isWorking = True
        self.server.bind(self.addr)

    def run(self):
        while self.isWorking:
            data, addr = self.server.recvfrom(1024)
            print 'from ' + str(addr) + ' data = ' + data
            self.server.sendto('hello', addr)

    def startWork(self):
        self.start()

    def stopWork(self):
        self.isWorking = False

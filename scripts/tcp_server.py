import socket
import time
import threading

class TcpServer (threading.Thread):
    def __init__(self, port):
        threading.Thread.__init__(self)
        self.addr = ('', port)
        self.thread = threading.Thread()
        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server.settimeout(5)
        self.isWorking = True
        self.server.bind(self.addr)
        self.server.listen(5)
        self.connectList = []
        self.server.setblocking(False)

    def run(self):
        while self.isWorking:
            try:
                skt, addr = self.server.accept()
                data = skt.recv(1024)
                if len(data) > 0:
                    print 'from ' + str(addr) + ' data = ' + data
                    skt.send('from server')
                skt.close()
            except socket.error:
                pass

    def startWork(self):
        self.start()

        # time.sleep(10)
        # self.stopWork()

    def stopWork(self):
        self.isWorking = False



def main():
    server = TcpServer(5000)
    server.startWork()

if __name__ == '__main__':
    main()
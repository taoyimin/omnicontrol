from udp_server import UdpServer

def main():
    server = UdpServer(5000)
    server.startWork()


if __name__ == '__main__':
    main()

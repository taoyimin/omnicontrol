class MatrixMessage:
    header = '\x01\x00'
    tail = '\xff'

    def __init__(self, body):
        self.body = body

    def toByteArray(self):
        return self.header + self.body + self.tail

    #Login
    @staticmethod
    def createLoginMessage(id):
        body = id
        return MatrixMessage(body)

    #Video channel switch
    @staticmethod
    def createVChannelSwitchMessage(input, output):
        body = '\x00'
        return MatrixMessage(body)

    #Audio channel switch
    @staticmethod
    def createAChannelSwitchMessage(input, output):
        body = '\x00'
        return MatrixMessage(body)

    #Camera cntrol
    @staticmethod
    def createCameraControlMessage(input, cmd):
        body = '\x00'
        return MatrixMessage(body)

    @staticmethod
    def addChecksumToMessage(msg):
        ba = bytearray(msg)
        cs = 0x00
        for b in ba:
            cs ^= ( b & 0xff )

        checksum  = str(hex(cs) ).upper();

        if (cs <= 0xf):
            return '0' + checksum[2:]
        else:
            return checksum[2:]

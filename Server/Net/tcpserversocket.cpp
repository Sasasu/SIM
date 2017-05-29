#include "tcpserversocket.h"
#include <QByteArray>
TCPServerSocket::TCPServerSocket(qintptr id) {
    qDebug() << "[INFO]new socket id" << static_cast<int>(id);
    this->index = id;
    QObject::connect(this, &TCPServerSocket::readyRead, this,
                     &TCPServerSocket::ReadData);
    QObject::connect(this, &TCPServerSocket::disconnected, this,
                     &TCPServerSocket::DisConnect);
    QObject::connect(this, &TCPServerSocket::disconnected, this,
                     &TCPServerSocket::deleteLater);
}

TCPServerSocket::~TCPServerSocket() {
    qDebug() << "[INFO]delet socket id"
             << (this->UserID == -1 ? index : UserID);
}
void TCPServerSocket::ReadData() {
    QByteArray datatype = this->readdata(sizeof(int32_t));
    int32_t messagetype = bytesToInt(datatype);
    qDebug() << "[INFO]Receive message type:" << messagetype;
    switch (messagetype) {
    case 0: {
        // 登录
        int32_t usernameline = bytesToInt(this->readdata(sizeof(int32_t)));
        QString username = this->readdata(usernameline);
        int32_t passwordline = bytesToInt(this->readdata(sizeof(int32_t)));
        QString password = this->readdata(passwordline);
        emit hasLogInRequest(this->index, username, password);
        break;
    }
    case 1: {
        // 寻找用户好友
        int32_t IDline = bytesToInt(this->readdata(sizeof(int32_t)));
        int32_t ID = bytesToInt(this->readdata(IDline));
        emit hasFriendSearchRequest(ID);
        break;
    }
    case 2: {
        // 聊天
        int fromsize = bytesToInt(this->readdata(sizeof(int32_t)));
        int from = bytesToInt(this->readdata(fromsize)); // TODO 根据 SocketMap 判断 from
        int tosize = bytesToInt(this->readdata(sizeof(int32_t)));
        int to = bytesToInt(this->readdata(tosize));
        int strsize = bytesToInt(this->readdata(sizeof(int32_t)));
        QString str = this->readdata(strsize);
        emit hasChatRequest(from, to, str);
        break;
    }
    default: { break; }
    }
}
void TCPServerSocket::setUserID(int id) { this->UserID = id; }

void TCPServerSocket::sendLogInResponse(int32_t data) {
    int32_t type = 0; // LogInResponse
    writedate(intToBytes(type));
    writedate(intToBytes(data));
}

void TCPServerSocket::sendFriendSearchResponse(QVector<UserFriend> data)
{
    int32_t type = 1;
    writedate(intToBytes(type));
    int32_t length = data.length();
    writedate(intToBytes(length));
    for(UserFriend &i:data){
        writedate(intToBytes(i.UserID));
        writedate(intToBytes(i.FriendID));
    }
}

void TCPServerSocket::sendChatResponse(int from, int to, QString str) {
    int32_t type = 2; // ChatResponse
    writedate(intToBytes(type));
    writedate(intToBytes(from));
    writedate(intToBytes(to));
    writedate(intToBytes(str.toUtf8().length()));
    writedate(str.toUtf8());
}


void TCPServerSocket::DisConnect() {
    emit hasDisConnect(this->index, this->UserID);
}
void TCPServerSocket::NewConnect() {}
int32_t TCPServerSocket::bytesToInt(QByteArray bytes) {
    int32_t addr = bytes[0] & 0x000000FF;
    addr |= ((bytes[1] << 8) & 0x0000FF00);
    addr |= ((bytes[2] << 16) & 0x00FF0000);
    addr |= ((bytes[3] << 24) & 0xFF000000);
    return addr;
}
QByteArray TCPServerSocket::intToBytes(int value) {
    QByteArray src;
    src.resize(4);
    src[0] = (char)((value >> 24) & 0xFF);
    src[1] = (char)((value >> 16) & 0xFF);
    src[2] = (char)((value >> 8) & 0xFF);
    src[3] = (char)(value & 0xFF);
    return src;
}
QByteArray TCPServerSocket::readdata(qint64 maxlen) {
    QByteArray ans;
    int timeout = 100;
    while (ans.size() == 0 && timeout != 0) {
        // 服务器会 100 cpu ...
        waitForReadyRead(30);
        timeout--;
        ans = this->read(maxlen);
    }
    return ans;
}

void TCPServerSocket::writedate(QByteArray data) {
    int id = 0;
    if (this->UserID == -1)
        id = this->index;
    else
        id = this->UserID;
    qDebug() << "[DEBU]write" << data << "to solt" << id;
    this->write(data);
}

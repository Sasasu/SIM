#include "tcpserver.h"
#include "Logic/logic.h"
#include "tcpserversocket.h"
TCPServer::TCPServer(QObject *parent) : QTcpServer(parent) {
    qDebug() << "[INFO]bulid TCPServer";
}
void TCPServer::handleLogINRequest(qintptr id, QString username,
                                   QString password) {
    qDebug() << "[INFO]tmp userID" << static_cast<int>(id)
             << "tying to login whith" << username << "and" << password;
    int loginID = Logic::dologin(username, password);
    if (loginID == -1) {
        qDebug() << "[INFO]tmp userID" << static_cast<int>(id)
                 << "log failure, close connection";
        TmpSocketMap[id]->sendLogInResponse(-1); // failure
        TmpSocketMap[id]->close();
        TmpSocketMap.remove(id);
    } else {
        qDebug() << "[INFO]tmp userID" << static_cast<int>(id)
                 << "log success, move socket to slot" << loginID;
        TCPServerSocket *socket = TmpSocketMap[id];
        socket->setUserID(loginID);
        TmpSocketMap.remove(id);
        if (SocketMap[loginID] != nullptr) {
            qDebug() << "[INFO]slot" << loginID
                     << "alardy has user, colse socket";
            SocketMap[loginID]->close();
            SocketMap.remove(loginID);
        }
        SocketMap[loginID] = socket;
        SocketMap[loginID]->sendLogInResponse(loginID); // success
    }
}

void TCPServer::handleChatRequest(int from, int to, QString str) {
    qDebug() << "[INFO]user" << from << "sned" << to << "saying" << str;
    auto ToUser = SocketMap[to];
    ToUser->sendChatResponse(from, to, str);
}

void TCPServer::handleDisConnet(int index, int UserID) {
    qDebug() << "[INFO]solt" << (UserID == -1 ? index : UserID)
             << "dissconneted";
    if (UserID == -1) {
        TmpSocketMap.remove(index);
    } else {
        SocketMap.remove(UserID);
    }
}

void TCPServer::handleFriendSearchRequest(int ID) {
    qDebug() << "[INFO]User" << ID << "try to list friend";
    QVector<UserFriend> data = Logic::dosearchfriend(ID);
    SocketMap[ID]->sendFriendSearchResponse(data);
}
void TCPServer::incomingConnection(qintptr handle) {

    TCPServerSocket *tcpserversocket = new TCPServerSocket(handle);
    tcpserversocket->setSocketDescriptor(handle);

    this->TmpSocketMap[handle] = tcpserversocket;

    QObject::connect(tcpserversocket, &TCPServerSocket::hasLogInRequest, this,
                     &TCPServer::handleLogINRequest);
    QObject::connect(tcpserversocket, &TCPServerSocket::hasDisConnect, this,
                     &TCPServer::handleDisConnet);
    QObject::connect(tcpserversocket, &TCPServerSocket::hasChatRequest, this,
                     &TCPServer::handleChatRequest);
    QObject::connect(tcpserversocket, &TCPServerSocket::hasFriendSearchRequest,
                     this, &TCPServer::handleFriendSearchRequest);
}

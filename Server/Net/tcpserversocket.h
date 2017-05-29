#ifndef TCPSERVERSOCKET_H
#define TCPSERVERSOCKET_H
#include "Object/userfriend.h"
#include <QTcpSocket>

class TCPServerSocket : public QTcpSocket {
    Q_OBJECT
  public:
    TCPServerSocket(qintptr id);
    ~TCPServerSocket();
    void setUserID(int id);
    void sendLogInResponse(int32_t data);
    void sendChatResponse(int from, int to, QString str);
    void sendFriendSearchResponse(QVector<UserFriend> data);
  signals:
    void hasData(int fromUserID, int toUserID, int messagetype,
                 QByteArray data);
    void hasLogInRequest(qintptr id, QString username, QString password);
    void hasChatRequest(int from, int to, QString str);
    void hasDisConnect(qintptr index, int UserID);
    void hasFriendSearchRequest(int id);
  private slots:
    void ReadData();
    void DisConnect();
    void NewConnect();

  private:
    int UserID = 0;
    int bytesToInt(QByteArray bytes);
    QByteArray intToBytes(int value);
    qintptr index;
    QByteArray readdata(qint64 maxlen);
    void writedate(QByteArray data);
};

#endif // TCPSERVERSOCKET_H

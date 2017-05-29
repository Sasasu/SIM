#ifndef TCPSERVER_H
#define TCPSERVER_H
#include "Net/tcpserversocket.h"
#include <QMap>
#include <QObject>
#include <QTcpServer>
class TCPServer : public QTcpServer {
    Q_OBJECT
  public:
    explicit TCPServer(QObject *parent = 0);
    QMap<int, TCPServerSocket *> SocketMap; // 主键用户 ID 数据 Socket 链接
    QMap<qintptr, TCPServerSocket *> TmpSocketMap; // 完成登录的用户就会被移除
  protected:
    virtual void incomingConnection(qintptr handle);
  signals:
  public slots:
    void handleLogINRequest(qintptr id, QString username, QString password);
    void handleChatRequest(int from, int to, QString str);
    void handleDisConnet(int index, int UserID);
    void handleFriendSearchRequest(int ID);
};

#endif // TCPSERVER_H

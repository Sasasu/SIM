#include "Net/tcpserver.h"
#include "SQL/sqlmanager.h"
#include <QSharedPointer>
#include <QSqlQuery>
#include <QtCore>
int main(int argc, char *argv[]) {
    QCoreApplication a(argc, argv);
    TCPServer *server = new TCPServer();
    try {
        SQLManager::getInstance();
    } catch (std::exception e) {
        qDebug() << e.what();
        return 1;
    }
    server->listen(QHostAddress::Any, 8192);
    qDebug() << "[INFO]listen on"
             << "127.0.0.1"
             << "port" << 8192;
    return a.exec();
}

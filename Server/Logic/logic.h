#ifndef LOGIC_H
#define LOGIC_H

#include "Object/message.h"
#include "Object/user.h"

#include "SQL/sqlmanager.h"
#include "SQL/messagesqlexecutor.h"
#include "SQL/usersqlexecutor.h"
#include "SQL/userfriendsqlexecutor.h"

#include <QObject>


class Logic : public QObject {
    Q_OBJECT
  public:
    explicit Logic(QObject *parent = 0);
    static int dologin(QString username, QString password);
    static bool doregister(QString username, QString password);
    static QVector<UserFriend> dosearchfriend(int ID);

  signals:

  public slots:
};

#endif // LOGIC_H

#ifndef USERFRIENDSQLEXECUTOR_H
#define USERFRIENDSQLEXECUTOR_H
#include "Object/userfriend.h"
#include "SQL/sqlmanager.h"

#include <QObject>
#include <QSqlQuery>
#include <QVector>

class UserFriendSQLExecutor : public QObject {
    Q_OBJECT
  public:
    explicit UserFriendSQLExecutor(QObject *parent = nullptr);
    QVector<UserFriend> SelectAll();
    QVector<UserFriend> Select(QSqlQuery preparedsqlquery);
    bool insert(QSqlQuery preparedsqlquery);

  private:
    QString SelectAllUser = "SELECT * FROM UserInfo ";
    SQLManager *sqlmanager;
  signals:

  public slots:
};

#endif // USERFRIENDSQLEXECUTOR_H

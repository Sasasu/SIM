#ifndef USERSQLEXECUTOR_H
#define USERSQLEXECUTOR_H
#include "Object/user.h"
#include "sqlmanager.h"
#include <QObject>
#include <QSqlQuery>
#include <QVector>
class UserSQLExecutor : public QObject {
    Q_OBJECT
  public:
    explicit UserSQLExecutor(QObject *parent = 0);
    QVector<User> SelectAll();
    QVector<User> Select(QSqlQuery preparedsqlquery);
    bool insert(QSqlQuery preparedsqlquery);
    QString SelectAllUser = "SELECT * FROM User user ";
    QString InsertToDataBase =
        "INSERT INTO User(UserName,UserPassword,UserType)"
        "VALUES(?,?,?)";
  signals:

  public slots:
  private:
    SQLManager *sqlmanager;
};

#endif // USERSQLEXECUTOR_H

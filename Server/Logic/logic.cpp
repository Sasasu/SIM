#include "logic.h"
#include <QDebug>
#include <QVariant>
Logic::Logic(QObject *parent) : QObject(parent) {}

bool Logic::doregister(QString username, QString password) {
    QSqlQuery sqlquery;
    UserSQLExecutor executor;
    sqlquery.prepare(executor.InsertToDataBase);
    sqlquery.addBindValue(username);
    sqlquery.addBindValue(password);
    sqlquery.addBindValue(0);
    return executor.insert(sqlquery);
}

QVector<UserFriend> Logic::dosearchfriend(int ID)
{
    QSqlQuery sqlquery;
    UserFriendSQLExecutor executor;
    sqlquery.prepare("SELECT * FROM UserInfo WHERE UserID = ?");
    sqlquery.addBindValue(ID);
    return executor.Select(sqlquery);
}
int Logic::dologin(QString username, QString password) {
    QSqlQuery sqlquery;
    UserSQLExecutor executor;
    sqlquery.prepare("SELECT * FROM User user WHERE user.UserName = ? AND "
                     "user.UserPassword = ?");
    sqlquery.addBindValue(username);
    sqlquery.addBindValue(password);
    QVector<User> data = executor.Select(sqlquery);
    if (data.empty())
        return -1;
    else
        return data[0].UserID;
}

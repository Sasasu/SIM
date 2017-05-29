#include "usersqlexecutor.h"
#include <QDebug>
#include <QSqlError>
#include <QVariant>
UserSQLExecutor::UserSQLExecutor(QObject *parent) : QObject(parent) {
    this->sqlmanager = SQLManager::getInstance();
}

QVector<User> UserSQLExecutor::SelectAll() {
    QSqlQuery sqlquery;
    sqlquery.exec(SelectAllUser);
    QVector<User> ans;
    while (sqlquery.next()) {
        int id = sqlquery.value("UserID").toInt();
        QString username = sqlquery.value("UserName").toString();
        QString password = sqlquery.value("UserPassword").toString();
        int usertype = sqlquery.value("UserType").toInt();
        User user(id, usertype, username, password);
        ans.push_back(user);
    }
    return ans;
}

QVector<User> UserSQLExecutor::Select(QSqlQuery preparedsqlquery) {
    preparedsqlquery.exec();
    QVector<User> ans;
    while (preparedsqlquery.next()) {
        int id = preparedsqlquery.value("UserID").toInt();
        QString username = preparedsqlquery.value("UserName").toString();
        QString password = preparedsqlquery.value("UserPassword").toString();
        int usertype = preparedsqlquery.value("UserType").toInt();
        User user(id, usertype, username, password);
        ans.push_back(user);
    }
    return ans;
}

bool UserSQLExecutor::insert(QSqlQuery preparedsqlquery) {
    return preparedsqlquery.exec();
}

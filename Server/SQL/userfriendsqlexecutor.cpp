#include "userfriendsqlexecutor.h"
#include <QDebug>
#include <QSqlError>
#include <QVariant>
UserFriendSQLExecutor::UserFriendSQLExecutor(QObject *parent)
    : QObject(parent) {
    this->sqlmanager = SQLManager::getInstance();
}

QVector<UserFriend> UserFriendSQLExecutor::SelectAll() {
    QSqlQuery sqlquery;
    sqlquery.exec(SelectAllUser);
    QVector<UserFriend> ans;
    while (sqlquery.next()) {
        int id = sqlquery.value("ID").toInt();
        int userid = sqlquery.value("UserID").toInt();
        int friendid = sqlquery.value("FriendID").toInt();
        UserFriend userfriend(id, userid, friendid);
        ans.push_back(userfriend);
    }
    return ans;
}

QVector<UserFriend> UserFriendSQLExecutor::Select(QSqlQuery preparedsqlquery) {
    preparedsqlquery.exec();
    QVector<UserFriend> ans;
    while (preparedsqlquery.next()) {
        int id = preparedsqlquery.value("ID").toInt();
        int userid = preparedsqlquery.value("UserID").toInt();
        int friendid = preparedsqlquery.value("FriendID").toInt();
        UserFriend userfriend(id, userid, friendid);
        ans.push_back(userfriend);
    }
    return ans;
}

bool UserFriendSQLExecutor::insert(QSqlQuery preparedsqlquery) {
    return preparedsqlquery.exec();
}

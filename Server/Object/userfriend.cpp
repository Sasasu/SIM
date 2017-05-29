#include "userfriend.h"
#include <QDebug>
UserFriend::UserFriend(int id, int userid, int friendid, QObject *parent)
    : QObject(parent) {
    this->ID = id;
    this->UserID = userid;
    this->FriendID = friendid;
}

UserFriend::UserFriend(int userid, int friendid, QObject *parent)
    : QObject(parent) {
    this->UserID = userid;
    this->FriendID = friendid;
}

UserFriend::UserFriend(QObject *parent) : QObject(parent) {
    qDebug() << "[ERRO]not use UserFriend()";
}

UserFriend::UserFriend(const UserFriend &t) : QObject() {
    new (this) UserFriend(t.ID, t.UserID, t.FriendID);
}

const UserFriend &UserFriend::operator=(const UserFriend &t) {
    this->ID = t.ID;
    this->UserID = t.UserID;
    this->FriendID = t.FriendID;
    return t;
}

#include "user.h"
#include <QDebug>
User::User() { qDebug() << "[ERRO]not use User()"; }

User::User(int userid, int usertype, QString username, QString password,
           QObject *parent)
    : QObject(parent), UserID(userid), UserType(usertype), UserName(username),
      PassWord(password) {}

User::User(const User &t) : QObject() {
    new (this) User(t.UserID, t.UserType, t.UserName, t.PassWord);
}

User const &User::operator=(const User &t) {
    this->UserID = t.UserID;
    this->UserName = t.UserName;
    this->UserType = t.UserType;
    this->PassWord = t.PassWord;
    return t;
}

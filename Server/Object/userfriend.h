#ifndef USERFRIEND_H
#define USERFRIEND_H

#include <QObject>

class UserFriend : public QObject {
    Q_OBJECT
  public:
    explicit UserFriend(int id, int userid, int friendid,
                        QObject *parent = nullptr);
    explicit UserFriend(int userid, int friendid, QObject *parent = nullptr);
    explicit UserFriend(QObject *parent = nullptr);
    explicit UserFriend(const UserFriend &t);
    const UserFriend &operator=(const UserFriend &t);
    int ID;
    int UserID;
    int FriendID;
  signals:

  public slots:
};

#endif // USERFRIEND_H

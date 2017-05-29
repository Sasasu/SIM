#ifndef USER_H
#define USER_H
#include <QObject>

class User : public QObject {
    Q_OBJECT
  public:
    explicit User();
    explicit User(int userid, int usertype, QString username, QString password,
                  QObject *parent = 0);
    explicit User(const User &t);
    const User &operator=(const User &t);
    int UserID;
    int UserType;
    QString UserName;
    QString PassWord;

  signals:

  public slots:
  private:
};

#endif // USER_H

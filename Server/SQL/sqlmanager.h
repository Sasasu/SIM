#ifndef SQLMANAGER_H
#define SQLMANAGER_H
#include <QObject>
#include <QSharedPointer>
class QSqlDatabase;
class QSqlQuery;
class SQLManager : public QObject {
    Q_OBJECT
  public:
    static SQLManager *getInstance();
    class SQLCONNECTERROR {};

  private:
    explicit SQLManager(QObject * = nullptr);
    explicit SQLManager(const SQLManager &) = delete;
    explicit SQLManager(const SQLManager &&) = delete;

    QString HostName;
    QString PassWord;
    QString UserName;
    QSqlDatabase *db = nullptr;
  signals:

  public slots:
};

#endif // SQLMANAGER_H

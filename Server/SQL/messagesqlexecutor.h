#ifndef MESSAGESQLEXECUTOR_H
#define MESSAGESQLEXECUTOR_H

#include <QObject>
class MessageSQLExecutor : public QObject {
    Q_OBJECT
  public:
    explicit MessageSQLExecutor(QObject *parent = 0);

  signals:

  public slots:
};

#endif // MESSAGESQLEXECUTOR_H

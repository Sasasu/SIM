#include "sqlmanager.h"
#include <QDebug>
#include <QSqlDatabase>
#include <QSqlQuery>
#include <QString>
#include <QVariant>
SQLManager::SQLManager(QObject *) {
    if (this->db == nullptr)
        this->db = new QSqlDatabase(QSqlDatabase::addDatabase("QMYSQL"));
    db->setHostName("localhost");
    db->setDatabaseName("SIM");
    db->setPort(3306);

    bool ok = db->open("root", "19970123");

    if (!ok) {
        qDebug() << "[ERRO]Cannot open database";
        throw SQLCONNECTERROR();
    } else {
        qDebug() << "[INFO]DataBase connection successfully!";
    }
}
SQLManager *SQLManager::getInstance() {
    static SQLManager sqlmanager;
    return &sqlmanager;
}

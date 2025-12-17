package sk.upjs.ics.spendwise.factory;

import sk.upjs.ics.spendwise.dao.UserDao;

public interface DaoFactory {
    UserDao userDao();
}

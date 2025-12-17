package sk.upjs.ics.spendwise.factory;

import org.springframework.jdbc.core.JdbcTemplate;
import sk.upjs.ics.spendwise.config.AppConfig;
import sk.upjs.ics.spendwise.dao.jdbc.JdbcUserDao;

public class JdbcDaoFactory {
    private static JdbcDaoFactory instance;
    private JdbcUserDao userDao;

    private JdbcDaoFactory() {
    }

    public static synchronized JdbcDaoFactory getInstance() {
        if (instance == null) {
            instance = new JdbcDaoFactory();
        }
        return instance;
    }

    public synchronized JdbcUserDao getUserDao() {
        if (userDao == null) {
            JdbcTemplate jdbcTemplate = AppConfig.getJdbcTemplate();
            userDao = new JdbcUserDao(jdbcTemplate);
        }
        return userDao;
    }
}

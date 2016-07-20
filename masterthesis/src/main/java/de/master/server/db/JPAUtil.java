package de.master.server.db; /**
 * @author uucly on 22.02.2016.
 */

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory emFactory;
    private static final EntityManagerFactory mysqlFactory;

    static {
        try {
            emFactory = Persistence.createEntityManagerFactory("org.hibernate.stelle.jpa");
        } catch (Throwable ex) {
            System.err.println("Cannot create EntityManagerFactory.");
            throw new ExceptionInInitializerError(ex);
        }
    }

    static {
        try {
            mysqlFactory = Persistence.createEntityManagerFactory("org.hibernate.mysql.jpa");
        } catch (Throwable ex) {
            System.err.println("Cannot create EntityManagerFactory.");
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager createEntityManager() {
        return emFactory.createEntityManager();
    }

    public static EntityManager createSqlEntityManager() {
        return mysqlFactory.createEntityManager();
    }

    public static void close() {
        emFactory.close();
    }

    public static void closeSql() {
        mysqlFactory.close();
    }

}

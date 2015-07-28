package cz.cuni.mff.xrg.odcs.backend.auxiliaries;

import cz.cuni.mff.xrg.odcs.commons.app.properties.RuntimeProperty;
import cz.cuni.mff.xrg.odcs.commons.app.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class DatabaseInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private JpaTransactionManager jpaTransactionManager;

    /**
     * Initialization with ..well.. initial data. This is done here because it should be independent from the used
     * database backend.
     */
    public void initialize() {
        if (isInitialized()) {
            LOG.info("Database already initialized");
            return;
        }

        LOG.info("Initializing Database...");

        EmailAddress adminEmailAddress = new EmailAddress("admin@example.com");
        EmailAddress userEmailAddress = new EmailAddress("user@example.com");

        RoleEntity adminRole = new RoleEntity();
        RoleEntity userRole = new RoleEntity();

        User admin = new User();
        admin.setFullName("John Admin");
        admin.setPassword("test");
        admin.setTableRows(20);
        admin.setUsername("admin");
        admin.setEmail(adminEmailAddress);
        admin.getRoles().add(adminRole);

        User user = new User();
        user.setFullName("John User");
        user.setPassword("test");
        user.setTableRows(20);
        user.setUsername("user");
        user.setEmail(userEmailAddress);
        user.getRoles().add(userRole);

        UserNotificationRecord userNotificationRecordAdmin = new UserNotificationRecord();
        userNotificationRecordAdmin.setUser(admin);
        userNotificationRecordAdmin.setTypeError(NotificationRecordType.INSTANT);
        userNotificationRecordAdmin.setTypeSuccess(NotificationRecordType.INSTANT);
        userNotificationRecordAdmin.getEmails().add(adminEmailAddress);

        UserNotificationRecord userNotificationRecordUser = new UserNotificationRecord();
        userNotificationRecordUser.setUser(user);
        userNotificationRecordUser.setTypeError(NotificationRecordType.INSTANT);
        userNotificationRecordUser.setTypeSuccess(NotificationRecordType.INSTANT);
        userNotificationRecordUser.getEmails().add(userEmailAddress);

        RuntimeProperty prop1 = new RuntimeProperty();
        prop1.setName("backend.scheduledPipelines.limit");
        prop1.setValue("5");
        RuntimeProperty prop2 = new RuntimeProperty();
        prop2.setName("run.now.pipeline.priority");
        prop2.setValue("1");
        RuntimeProperty prop3 = new RuntimeProperty();
        prop3.setName("locale");
        prop3.setValue("en");

        EntityManager em = jpaTransactionManager.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        em.persist(adminEmailAddress);
        em.persist(userEmailAddress);
        em.persist(admin);
        em.persist(user);
        em.persist(userNotificationRecordUser);
        em.persist(userNotificationRecordAdmin);
        em.persist(prop1);
        em.persist(prop2);
        em.persist(prop3);
        em.persist(adminRole);
        em.persist(userRole);
        em.getTransaction().commit();
    }

    private boolean isInitialized() {
        EntityManager em = jpaTransactionManager.getEntityManagerFactory().createEntityManager();
        return !em.createQuery("SELECT x FROM User x").getResultList().isEmpty();
    }

}

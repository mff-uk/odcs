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
        adminRole.setName("Administrator");
        RoleEntity userRole = new RoleEntity();
        userRole.setName("User");

        User admin = new User();
        admin.setFullName("John Admin");
        admin.setPassword("test");
        admin.setTableRows(20);
        admin.setUsername("admin");
        admin.setEmail(adminEmailAddress);
        admin.addRole(adminRole);

        User user = new User();
        user.setFullName("John User");
        user.setPassword("test");
        user.setTableRows(20);
        user.setUsername("user");
        user.setEmail(userEmailAddress);
        user.addRole(userRole);

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

        addNewPermissionToRoles("Administrator", adminRole);
        addNewPermissionToRoles("pipeline.delete", adminRole, userRole);
        addNewPermissionToRoles("pipeline.edit", adminRole, userRole);
        addNewPermissionToRoles("pipeline.definePipelineDependencies", adminRole, userRole);
        addNewPermissionToRoles("pipeline.export", adminRole, userRole);
        addNewPermissionToRoles("pipeline.exportScheduleRules", adminRole, userRole);
        addNewPermissionToRoles("pipeline.import", adminRole, userRole);
        addNewPermissionToRoles("pipeline.importScheduleRules", adminRole, userRole);
        addNewPermissionToRoles("pipeline.importUserData", adminRole, userRole);
        addNewPermissionToRoles("pipeline.schedule", adminRole, userRole);
        addNewPermissionToRoles("pipeline.read", adminRole, userRole);
        addNewPermissionToRoles("pipeline.runDebug", adminRole, userRole);
        addNewPermissionToRoles("pipeline.exportDpuData", adminRole, userRole);
        addNewPermissionToRoles("pipeline.exportDpuJars", adminRole, userRole);
        addNewPermissionToRoles("pipeline.setVisibility", adminRole, userRole);
        addNewPermissionToRoles("pipeline.setVisibilityPublicRw", adminRole, userRole);
        addNewPermissionToRoles("pipelineExecution.delete", adminRole, userRole);
        addNewPermissionToRoles("pipelineExecution.stop", adminRole, userRole);
        addNewPermissionToRoles("pipeline.run", adminRole, userRole);
        addNewPermissionToRoles("pipelineExecution.read", adminRole, userRole);
        addNewPermissionToRoles("scheduleRule.create", adminRole, userRole);
        addNewPermissionToRoles("scheduleRule.delete", adminRole, userRole);
        addNewPermissionToRoles("scheduleRule.edit", adminRole, userRole);
        addNewPermissionToRoles("scheduleRule.read", adminRole, userRole);
        addNewPermissionToRoles("scheduleRule.setPriority", adminRole, userRole);
        addNewPermissionToRoles("dpuTemplate.create", adminRole);
        addNewPermissionToRoles("dpuTemplate.createFromInstance", adminRole, userRole);
        addNewPermissionToRoles("dpuTemplate.setVisibility", adminRole, userRole);
        addNewPermissionToRoles("dpuTemplate.delete", adminRole);
        addNewPermissionToRoles("dpuTemplate.edit", adminRole, userRole);
        addNewPermissionToRoles("dpuTemplate.export", adminRole, userRole);
        addNewPermissionToRoles("dpuTemplate.copy", adminRole, userRole);
        addNewPermissionToRoles("dpuTemplate.read", adminRole, userRole);
        addNewPermissionToRoles("dpuTemplate.showScreen", adminRole, userRole);
        addNewPermissionToRoles("user.management", adminRole);
        addNewPermissionToRoles("pipeline.create", adminRole, userRole);
        addNewPermissionToRoles("pipeline.copy", adminRole, userRole);
        addNewPermissionToRoles("runtimeProperties.edit", adminRole);
        addNewPermissionToRoles("userNotificationSettings.editEmailGlobal", adminRole, userRole);
        addNewPermissionToRoles("userNotificationSettings.editNotificationFrequency", adminRole, userRole);
        addNewPermissionToRoles("userNotificationSettings.createPipelineExecutionSettings", adminRole, userRole);

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

    private void addNewPermissionToRoles(String permissionName, RoleEntity... roles) {
        Permission permission = new Permission();
        permission.setName(permissionName);

        for (RoleEntity role : roles) {
            role.getPermissions().add(permission);
        }
    }

    private boolean isInitialized() {
        EntityManager em = jpaTransactionManager.getEntityManagerFactory().createEntityManager();
        return !em.createQuery("SELECT x FROM User x").getResultList().isEmpty();
    }

}

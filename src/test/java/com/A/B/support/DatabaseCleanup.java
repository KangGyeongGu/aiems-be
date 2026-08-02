package com.A.B.support;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import org.hibernate.Session;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@TestComponent
public class DatabaseCleanup {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void execute() {
        entityManager.flush();

        List<String> tables = entityManager.getMetamodel().getEntities().stream()
                .filter(entity -> entity.getJavaType().isAnnotationPresent(Entity.class))
                .map(this::tableName)
                .toList();

        if (tables.isEmpty()) {
            return;
        }

        String vendor = databaseProductName();
        switch (vendor) {
            case "PostgreSQL" -> truncatePostgresql(tables);
            case "MySQL" -> truncateMysql(tables);
            default -> throw new IllegalStateException("지원하지 않는 DB 벤더: " + vendor);
        }
    }

    private String databaseProductName() {
        return entityManager.unwrap(Session.class)
                .doReturningWork(connection -> connection.getMetaData().getDatabaseProductName());
    }

    private void truncatePostgresql(List<String> tables) {
        entityManager.createNativeQuery(
                "TRUNCATE TABLE " + String.join(", ", tables) + " RESTART IDENTITY CASCADE"
        ).executeUpdate();
    }

    private void truncateMysql(List<String> tables) {
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        for (String table : tables) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
        }
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }

    private String tableName(EntityType<?> entity) {
        Table table = entity.getJavaType().getAnnotation(Table.class);
        if (table != null && !table.name().isBlank()) {
            return table.name();
        }
        return entity.getName()
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }
}

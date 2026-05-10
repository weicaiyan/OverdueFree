package com.overduefree.module.health.service.impl;

import com.overduefree.module.health.dto.HealthResult;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HealthServiceImplTest {

    private static final String STATUS_UP = "UP";
    private static final String STATUS_DOWN = "DOWN";
    private static final String MYSQL_PRODUCT_NAME = "MySQL";

    @Test
    void getHealthShouldReturnUpWhenDatabaseIsValid() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(true);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(databaseMetaData.getDatabaseProductName()).thenReturn(MYSQL_PRODUCT_NAME);

        HealthResult result = new HealthServiceImpl(dataSource).getHealth();

        assertThat(result.getOverallStatus()).isEqualTo(STATUS_UP);
        assertThat(result.getApplicationStatus()).isEqualTo(STATUS_UP);
        assertThat(result.getDatabaseStatus()).isEqualTo(STATUS_UP);
        assertThat(result.getDatabaseProductName()).isEqualTo(MYSQL_PRODUCT_NAME);
        assertThat(result.getTimeZone()).isEqualTo("Asia/Shanghai");
        assertThat(result.getCheckedAt()).isNotNull();
    }

    @Test
    void getHealthShouldReturnDownWhenDatabaseFails() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenThrow(new SQLException("Access denied"));

        HealthResult result = new HealthServiceImpl(dataSource).getHealth();

        assertThat(result.getOverallStatus()).isEqualTo(STATUS_DOWN);
        assertThat(result.getApplicationStatus()).isEqualTo(STATUS_UP);
        assertThat(result.getDatabaseStatus()).isEqualTo(STATUS_DOWN);
        assertThat(result.getMessage()).contains("数据库连接失败");
        assertThat(result.getMessage()).doesNotContain("Access denied");
    }
}

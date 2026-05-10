package com.overduefree.module.health.service.impl;

import com.overduefree.module.health.dto.HealthResult;
import com.overduefree.module.health.service.HealthService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class HealthServiceImpl implements HealthService {

    private static final String STATUS_UP = "UP";
    private static final String STATUS_DOWN = "DOWN";
    private static final String BEIJING_TIME_ZONE = "Asia/Shanghai";
    private static final int DATABASE_VALID_TIMEOUT_SECONDS = 2;

    private final DataSource dataSource;

    public HealthServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public HealthResult getHealth() {
        HealthResult result = new HealthResult();
        result.setApplicationStatus(STATUS_UP);
        result.setTimeZone(BEIJING_TIME_ZONE);
        result.setCheckedAt(LocalDateTime.now(ZoneId.of(BEIJING_TIME_ZONE)));
        fillDatabaseStatus(result);
        result.setOverallStatus(resolveOverallStatus(result));
        return result;
    }

    private void fillDatabaseStatus(HealthResult result) {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(DATABASE_VALID_TIMEOUT_SECONDS)) {
                result.setDatabaseStatus(STATUS_UP);
                result.setDatabaseProductName(connection.getMetaData().getDatabaseProductName());
                result.setMessage("服务和数据库连接正常");
                return;
            }
            result.setDatabaseStatus(STATUS_DOWN);
            result.setMessage("数据库连接不可用，请检查 MySQL 是否启动");
        } catch (SQLException e) {
            result.setDatabaseStatus(STATUS_DOWN);
            result.setMessage("数据库连接失败，请检查 MySQL 是否启动和数据库配置是否正确");
        }
    }

    private String resolveOverallStatus(HealthResult result) {
        if (STATUS_UP.equals(result.getApplicationStatus()) && STATUS_UP.equals(result.getDatabaseStatus())) {
            return STATUS_UP;
        }
        return STATUS_DOWN;
    }
}

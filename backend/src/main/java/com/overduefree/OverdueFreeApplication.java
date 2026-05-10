package com.overduefree;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.TimeZone;

@MapperScan({
    "com.overduefree.module.admin.mapper",
    "com.overduefree.module.article.mapper",
    "com.overduefree.module.asset.mapper",
    "com.overduefree.module.caseitem.mapper",
    "com.overduefree.module.configitem.mapper",
    "com.overduefree.module.customer.mapper",
    "com.overduefree.module.lead.mapper",
    "com.overduefree.module.operationlog.mapper"
})
@SpringBootApplication
public class OverdueFreeApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(OverdueFreeApplication.class, args);
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}

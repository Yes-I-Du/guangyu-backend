package com.guangyu.guangyubackend.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 分页拦截器配置
 *
 * @author dmz xxx@163.com
 * @version 2025/5/19 23:26
 * @since JDK17
 */
@Configuration
@MapperScan("com.guangyu.guangyubackend.mapper")
public class MyBatisPlusConfig {

    /**
     * 分页拦截器配置
     *
     * @return {@link MybatisPlusInterceptor}
     */
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        //分页插件
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return mybatisPlusInterceptor;
    }
}


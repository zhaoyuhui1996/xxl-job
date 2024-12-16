package com.xxl.job.executor.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * <p>
 * 一体化平台设计器配置信息
 * </p>
 *
 * @author zhao.yuhui
 * @e-mail zhaoyuhui@taiji.com.cn
 * @date 2024/12/12
 */
@Data
@Configuration
public class DesignerServerConfig {

    @Value("${designer.server.ip:127.0.0.1}")
    private String ip;

    @Value("${designer.server.port:8084}")
    private int port;
}

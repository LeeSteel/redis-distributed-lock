package com.lg.redislock.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @version V1.0
 * @Title:
 * @Package
 * @Description:
 * @author: 李钢 2580704698@qq.com
 * @date: 2021/10/10 21:22
 * @Copyright: Copyright (c) 2019
 */
@Configuration
public class RedissonConfig {

    private String REDISSON_CLUSTER_PATH = "redisson-cluster.yaml";

    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() throws IOException {
        //1、创建配置
        /*  */
        URL url = this.getClass().getClassLoader().getResource(REDISSON_CLUSTER_PATH);

        File file = new File(url.getPath());


        Config config = Config.fromYAML(file);


        return Redisson.create(config);
    }
}

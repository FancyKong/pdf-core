package com.cafa.pdf.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务配置
 * Created by Cherish on 2017/1/4.
 */
@Slf4j
@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Scheduled(cron = "0 0/2 * * * ?") // 每2分钟执行一次
    public void scheduler() {
        log.debug(">>>>>>>>>>>>> 定时任务测试");
    }

}
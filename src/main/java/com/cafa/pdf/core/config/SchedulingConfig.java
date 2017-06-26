package com.cafa.pdf.core.config;

import com.cafa.pdf.core.commom.interceptor.VisitInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 定时任务配置
 * Created by Cherish on 2017/1/4.
 */
@Slf4j
@Configuration
@EnableScheduling
public class SchedulingConfig {

//    @Scheduled(cron = "0 0/2 * * * ?") // 每2分钟执行一次
//    public void scheduler() {
//        log.debug("【每2分钟执行一次】>>>>>>>>>>>>> 定时任务测试");
//    }

    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨清空访问量的ipMap
    public void clearIpMap() {
        log.info("【每天凌晨】 清空访问量的ipMap");
        VisitInterceptor.ipMap = new ConcurrentHashMap<>();
    }

}
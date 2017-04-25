package com.cafa.pdf.core.email;

import com.cafa.pdf.core.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/4/25 17:05
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class EmailTest {

    @Autowired
    private MailService mailService;

    private String to = "785427346@qq.com";

    @Test
    public void sendSimpleMail() {
        mailService.sendSimpleMail(to, "主题：简单邮件", "测试邮件内容");
    }

    @Test
    public void sendHtmlMail() {
        String content = "<h1>主题：html邮件</h1>";
        mailService.sendHtmlMail(to, "主题：html邮件", content);
    }

}

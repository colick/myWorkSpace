package cn.itcast.sms;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsController {

    @Autowired
    private SmsUtil smsUtil;

    @RequestMapping("/testSms")
    public void testSms() throws Exception {
        smsUtil.sendSms("13636544132", "SMS_171118446",
                "colick商城", "{\"number\":\"123456\"}");
    }

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @RequestMapping("/testTopic")
    public void testTopic() throws Exception {
        ActiveMQTopic destination = new ActiveMQTopic("test_aaa");
        jmsMessagingTemplate.convertAndSend(destination, "hello_topic");
    }
}

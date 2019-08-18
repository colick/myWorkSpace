package cn.itcast.sms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumerTest {

    /*@JmsListener(destination = "test_aaa")
    public void receive(String text) {
        System.out.println("接收到消息：" + text);
    }*/
}
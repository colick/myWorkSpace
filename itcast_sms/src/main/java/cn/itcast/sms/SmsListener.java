package cn.itcast.sms;

import com.aliyuncs.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmsListener {

    @Autowired
    private SmsUtil smsUtil;

    @JmsListener(destination = "sms")
    public void senSms(Map<String, String> map) {
        try {
            System.out.println("接收的参数:" + map.toString());
            CommonResponse response = smsUtil.sendSms(map.get("mobile"),
                    map.get("template_code"), map.get("sign_name"), map.get("param"));
            System.out.println("短信发送后返回:" + response.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
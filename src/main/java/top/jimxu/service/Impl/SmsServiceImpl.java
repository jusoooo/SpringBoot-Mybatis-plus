package top.jimxu.service.Impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.jimxu.service.SmsService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {



    /**
     * 企业名称
     */
    @Value("${sms.ecName}")
    private String ecName;
    /**
     * 账号
     */
    @Value("${sms.apId}")
    private String apId;
    /**
     * 密码
     */
    @Value("${sms.secretKey}")
    private String secretKey;
    /**
     * 签名编码
     */
    @Value("${sms.sign}")
    private String sign;
    /**
     * 地址
     */
    @Value("${sms.url}")
    private String url;

    /***
     * 调用中国移动接口发送短信
     * @param content
     * @param mobileNo
     * @return
     */
    @Override
    public String sendSms(String content, String mobileNo) {
        // 参数校验序列，生成方法：将ecName、apId、secretKey、mobiles、content、sign、addSerial 按序拼接（无间隔符），通过MD5（32位小写）计算得出值
        String addSerial = "";
        // 构建参数mac
        String mac = SecureUtil.md5(ecName + apId + secretKey + mobileNo + content + sign + addSerial);
        // 构建完整参数
        Map<String,String> params = new HashMap<>();
        params.put("ecName",ecName);
        params.put("apId",apId);
        params.put("mobiles",mobileNo);
        params.put("content",content);
        params.put("sign",sign);
        params.put("addSerial",addSerial);
        params.put("mac",mac);
        // 参数转json
        String jsonParams = JSON.toJSONString(params);
        // 参数转base64
        String encodeParams = Base64.encode(jsonParams);
        String res = HttpUtil.post(url,encodeParams);
        log.info(res);
        JSONObject resObject = JSON.parseObject(res);
        if (StrUtil.equals("success",resObject.getString("rspcod"))){
            return "success";
        }else {
            return "error";
        }
    }

}

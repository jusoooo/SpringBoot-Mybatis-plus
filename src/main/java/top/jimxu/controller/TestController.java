package top.jimxu.controller;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import top.jimxu.entity.domain.R;
import top.jimxu.entity.domain.SrAppointPoolDetail;
import top.jimxu.mapper.SrAppointPoolDetailMapper;

@RequestMapping("/testBug")
@RestController
@RequiredArgsConstructor
public class TestController {


    private final SrAppointPoolDetailMapper srAppointPoolDetailMapper;

    private final JmsTemplate jmsTemplate;


    @GetMapping("/getSrAppointPoolDetailById")
    public R getSrAppointPoolDetailById(@RequestParam String id){
        SrAppointPoolDetail srAppointPoolDetail = srAppointPoolDetailMapper.selectById(id);
        if (StrUtil.equals(srAppointPoolDetail.getIsCheck(),"1")){
            throw new RuntimeException("预约号状态不为核销，无法取消核销!");
        }
        return R.success(srAppointPoolDetail);
    }


    // 测试发送 Topic 消息
    @GetMapping("/test/topic")
    public String sendTestTopic() {
        // 模拟对方发送的 JSON 字符串，并带上 token
        String mockJson = "{\"orderId\":\"12345\",\"status\":\"PUSHED\",\"token\":\"76cfa4d25fb8b84fb6e396446d33a857\"}";

        jmsTemplate.convertAndSend("SRPM/PUSH/TIANMENG/FVqOT", mockJson, message -> {
            message.setStringProperty("token", "76cfa4d25fb8b84fb6e396446d33a857"); // 模拟带上 token
            return message;
        });
        return "Topic 测试消息已发送，请查看控制台日志！";
    }

    // 测试发送 Queue 消息
    @GetMapping("/test/queue")
    public String sendTestQueue() {
        jmsTemplate.convertAndSend("SRPM.QUEUE.TIANMENG.TASK", "这是一条测试队列消息");
        return "Queue 测试消息已发送，请查看控制台日志！";
    }


}

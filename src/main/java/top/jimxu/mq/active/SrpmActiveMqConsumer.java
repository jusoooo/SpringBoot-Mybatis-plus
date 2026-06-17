package top.jimxu.mq.active;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.TextMessage;

@Slf4j
@Component
public class SrpmActiveMqConsumer {

    private static final String EXPECTED_TOKEN = "76cfa4d25fb8b84fb6e396446d33a857";


    // ==========================================
    // 场景 A：消费 Topic (发布/订阅模式)
    // ==========================================
    @JmsListener(
            destination = "SRPM/PUSH/TIANMENG/FVqOT",
//            subscription = "srpm_tianmeng_sub_01", Topic 持久化订阅必须指定唯一的 subscription ID
            containerFactory = "topicFactory",      // 指定使用上面配置的 topicFactory
            id="srpmTopicListener"
    )
    public void consumeTopicMessage(Message jmsMessage) {
        try {
            String body = (jmsMessage instanceof TextMessage) ? ((TextMessage) jmsMessage).getText() : jmsMessage.toString();
            log.info("✅ [Topic] 收到 SRPM 推送消息: {}", body);

            // TODO: 处理 Topic 业务逻辑

        } catch (Exception e) {
            log.error("[Topic] 消息处理异常", e);
        }
    }


    // ==========================================
    // 场景 B：消费 Queue (点对点模式) - 为未来准备
    // ==========================================
    @JmsListener(
            destination = "SRPM.QUEUE.TIANMENG.TASK", // 假设未来有个队列叫这个名字
            containerFactory = "queueFactory"         // 指定使用上面配置的 queueFactory
    )
    public void consumeQueueMessage(String message) {
        log.info("✅ [Queue] 收到 SRPM 任务消息: {}", message);

        // TODO: 处理 Queue 业务逻辑
        // Queue 模式的特点是：消息被消费一次后就会从队列中删除，不会有其他消费者收到。
    }




}

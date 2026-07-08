package top.jimxu.mq.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Kafka 消费者监听器
 */
@Slf4j
@Component
public class SrisKafkaConsumer {

    /**
     * 监听 outquay_yw_to_sris 主题
     */

    // 监听器方法
    @KafkaListener(topics = "outquay_yw_to_sris1", groupId = "sris-outquay-sris-group",id = "sris-yiwu-outnode-consumer")
    public void listen(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        log.info("📥 收到监听消息! Topic: {}, Partition: {}, Offset: {}, Value: {}",
                record.topic(), record.partition(), record.offset(), record.value());

        try {
            // TODO: 你的业务逻辑
            System.out.println("处理业务: " + record.value());
            // 手动提交 ACK
            ack.acknowledge();
            log.info("✅ 消息处理完成，已 ACK");
        } catch (Exception e) {
            log.error("❌ 处理消息异常", e);
        }
    }

    /**
     * 模拟你的业务处理逻辑
     */
    private void processYourBusiness(String message) {
        log.info("🚀 正在执行业务逻辑，处理内容: {}", message);

        // 模拟耗时操作
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
    }

}
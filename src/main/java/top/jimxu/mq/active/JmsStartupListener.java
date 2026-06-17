package top.jimxu.mq.active;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class JmsStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private JmsListenerEndpointRegistry registry;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("🚀 应用已完全启动，准备手动激活 ActiveMQ 监听器...");

        // 根据我们在 @JmsListener 中指定的 id 找到对应的容器
        if (registry.isRunning()) {
            // 获取特定的监听器容器
            var container = registry.getListenerContainer("srpmTopicListener");
            if (container != null && !container.isRunning()) {
                try {
                    container.start();
                    log.info("✅ 成功手动启动 ActiveMQ 监听器: srpmTopicListener");
                } catch (Exception e) {
                    log.error("❌ 启动 ActiveMQ 监听器失败", e);
                }
            }
        }
    }
}
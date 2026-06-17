package top.jimxu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;

@Configuration
public class JmsConfig {

    /**
     * 1. 配置专门用于消费 Queue (队列) 的工厂
     * 注意：其实 Spring Boot 默认就提供了一个名为 "jmsListenerContainerFactory" 的 Queue 工厂，
     * 但为了代码对称和清晰，我们显式定义一个名为 "queueFactory" 的 Bean。
     */
    @Bean("queueFactory")
    public DefaultJmsListenerContainerFactory queueFactory(CachingConnectionFactory cachingConnectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory);
        factory.setPubSubDomain(false); // 明确指定为 Queue 模式 (点对点)
        factory.setConcurrency("3-10"); // 可选：配置消费线程数
        return factory;
    }

    /**
     * 2. 配置专门用于消费 Topic (主题) 的工厂
     */
    @Bean("topicFactory")
    public DefaultJmsListenerContainerFactory topicFactory(CachingConnectionFactory cachingConnectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory);
//        factory.setPubSubDomain(true);  // 明确指定为 Topic 模式 (发布/订阅)
        // 开启持久化订阅 (确保服务重启期间，Topic 消息不丢失)
//        factory.setSubscriptionDurable(true);
        factory.setConcurrency("1-5"); // Topic 通常并发不需要太高，视业务而定
        return factory;
    }



}

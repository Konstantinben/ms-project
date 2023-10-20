package otus.ms.social.configuration;

import otus.ms.social.service.PostsRedisMessageSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisPubSubConfig {
    @Value("${app.redis.topics.post-create-friends}")
    private String topicPosts;

    @Autowired
    PostsRedisMessageSubscriber redisMessageSubscriber;

    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(redisMessageSubscriber);
    }

    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container
                = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListener(), postsTopic());
        return container;
    }

    @Bean
    ChannelTopic postsTopic() {
        return new ChannelTopic(topicPosts);
    }
}

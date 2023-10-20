package otus.ms.social.configuration;

import otus.ms.social.model.entity.Post;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Post> postRedisTemplate(RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, Post.class);
    }

    private <T extends Object> RedisTemplate<String, T> createRedisTemplate(RedisConnectionFactory connectionFactory, Class<T> tClass) {
        final RedisTemplate<String, T> template = new RedisTemplate();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(tClass);
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(template.getStringSerializer());
        template.setHashKeySerializer(template.getStringSerializer());
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(template.getStringSerializer());
        return template;
    }
}

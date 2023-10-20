package otus.ms.social.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import otus.ms.social.mapper.PostMapper;
import otus.ms.social.model.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import otus.ms.social.configuration.WebSocketConfig;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostsRedisMessageSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    private final PostMapper postMapper;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Message received: " + message.toString());
        try {
            var post = objectMapper.readValue(message.getBody(), Post.class);

            if (CollectionUtils.isNotEmpty(post.getFriends())) {
                for (UUID friendUuid: post.getFriends()) {
                    // add config.setUserDestinationPrefix("/user");
                    simpMessagingTemplate.convertAndSendToUser(
                            friendUuid.toString(),
                            WebSocketConfig.DESTINATION_POSTFIX,
                            postMapper.toPostDto(post));
                }
            }
        } catch (Exception e) {
            log.error("Cannot process post " + message, e);
        }
    }
}

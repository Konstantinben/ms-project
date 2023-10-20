package otus.ms.social.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import otus.ms.social.model.dto.PostFriendsDto;
import otus.ms.social.model.entity.Post;
import otus.ms.social.model.entity.User;
import otus.ms.social.model.mapper.PostMapper;
import otus.ms.social.repository.UserReadOnlyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostsRedisMessageSubscriber implements MessageListener, MessagePublisher<PostFriendsDto> {

    @Autowired
    @Qualifier("postsFriendsTopic")
    private ChannelTopic postsFriendsTopic;

    private final RedisTemplate<String, Post> postRedisTemplate;

    private final PostMapper postMapper;

    private final ObjectMapper objectMapper;

    private final RedisService redisService;

    private final UserReadOnlyRepository userReadOnlyRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Message received: " + message.toString());
        try {
            Post post = objectMapper.readValue(message.getBody(), Post.class);
            //var userList = userReadRepository.findFriendsById(post.getUserId());
            var userList = userReadOnlyRepository.findFriendsById(post.getUserId());
            redisService.addPostToUsersFeeds(userList, post);
            var postFriendsDto = postMapper.toPostFriendsDto(post);
            postFriendsDto.getFriends().addAll(userList.stream().map(User::getUuid).collect(Collectors.toList()));
            publish(postFriendsDto);
        } catch (Exception e) {
            log.error("Cannot process post " + message, e);
        }
    }

    @Override
    public void publish(PostFriendsDto post) {
        postRedisTemplate.convertAndSend(postsFriendsTopic.getTopic(), post);
    }
}

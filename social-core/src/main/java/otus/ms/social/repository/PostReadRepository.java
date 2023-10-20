package otus.ms.social.repository;

import otus.ms.social.model.entity.Post;

import java.util.List;
import java.util.UUID;

public interface PostReadRepository extends EntityReadOnlyRepository<Post, Long>{

    List<Post> findPostsByUserUuidOrderByCreatedDateDesc(UUID uuid);
}

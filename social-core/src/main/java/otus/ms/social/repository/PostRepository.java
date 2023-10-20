package otus.ms.social.repository;

import otus.ms.social.model.entity.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository  extends CrudRepository<Post, Long> {
}

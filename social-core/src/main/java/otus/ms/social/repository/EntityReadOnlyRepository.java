package otus.ms.social.repository;

import otus.ms.social.configuration.jdbc.ReadOnlyRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import otus.ms.social.model.entity.Post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
@ReadOnlyRepository
public interface EntityReadOnlyRepository<T, ID> extends Repository<T, ID> {
    Optional<T> findById(ID uuid);
    Optional<T> findByUuid(UUID uuid);
}
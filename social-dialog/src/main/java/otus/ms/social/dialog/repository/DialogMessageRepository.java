package otus.ms.social.dialog.repository;

import otus.ms.social.dialog.model.entity.DialogMessage;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface DialogMessageRepository extends CrudRepository<DialogMessage, UUID> {

    List<DialogMessage> getByKeyHashOrderByCreatedDateDesc(int keyHash);

    @Modifying
    @Query("" +
            "insert into dialog_messages (uuid, key_hash, shard_id, from_user_id, to_user_id, message, created_date) " +
            "values (:uuid, :keyHash, :shardId, :fromUserId, :toUserId, :message, :createdDate)")
    void create(UUID uuid, int keyHash, int shardId, Integer fromUserId, Integer toUserId, String message, Date createdDate);
}

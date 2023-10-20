package otus.ms.social.model.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class PostFriendsDto {
    @Id
    private Long id;

    @NotNull
    private UUID uuid;

    @NotNull
    private Integer userId;

    @NotNull
    private UUID userUuid;

    private List<UUID> friends = new ArrayList<>();

    @NotBlank
    private String message;

    @NotBlank
    private String userFirstName;

    private String userLastName;

    @Past
    @NotNull
    private Date createdDate;

    @Past
    private Date updatedDate;
}

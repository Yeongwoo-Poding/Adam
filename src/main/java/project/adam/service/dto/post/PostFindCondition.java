package project.adam.service.dto.post;

import lombok.*;
import project.adam.entity.Privilege;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostFindCondition {
    private Privilege privilege;
    private UUID writerId;
    private String titleLike;

    @Override
    public String toString() {
        return "privilege = " + privilege + ", writerId = " + writerId + ", titleLike = " + titleLike;
    }
}

package project.adam.service.dto.post;

import lombok.*;
import project.adam.entity.Privilege;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostFindCondition {
    private Privilege privilege;
    private String writerId;
    private String titleLike;

    @Override
    public String toString() {
        return "privilege = " + privilege + ", writerId = " + writerId + ", titleLike = " + titleLike;
    }
}

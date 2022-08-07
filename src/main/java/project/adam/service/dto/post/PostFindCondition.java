package project.adam.service.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.adam.entity.Privilege;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostFindCondition {
    private Privilege privilege;
    private String writerId;
    private String titleLike;
}

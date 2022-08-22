package project.adam.service.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostFindCondition {

    private UUID writerId;
    private String title;

    @Override
    public String toString() {
        return "writerId = " + writerId + ", title = " + title;
    }
}

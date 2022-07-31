package project.adam.service.dto.post;

import lombok.Getter;
import project.adam.entity.Board;
import project.adam.entity.Comment;
import project.adam.entity.Member;
import project.adam.entity.Post;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PostFindResponse {

    private Member writer;

    private String boardName;

    private LocalDateTime createDate;

    private LocalDateTime lastModifiedDate;

    private String title;

    private String body;

    public PostFindResponse(Post post) {
        this.writer = post.getWriter();
        this.boardName = post.getBoard().toString();
        this.createDate = post.getCreateDate();
        this.lastModifiedDate = post.getLastModifiedDate();
        this.title = post.getTitle();
        this.body = post.getBody();
    }
}
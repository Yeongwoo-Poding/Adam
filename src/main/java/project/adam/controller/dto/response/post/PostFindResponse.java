package project.adam.controller.dto.response.post;

import lombok.Getter;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.entity.post.PostImage;
import project.adam.utils.DateUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostFindResponse {

    private final Long id;
    private final Board board;
    private final String writerName;
    private final String createdDate;
    private final boolean modified;
    private final String title;
    private final String body;
    private final int viewCount;
    private final int commentCount;
    private final List<String> images;

    public PostFindResponse(Post post) {
        this.id = post.getId();
        this.board = post.getBoard();
        this.title = post.getTitle();
        this.writerName = post.getWriter().getName();
        this.createdDate = DateUtils.getFormattedDateTime(post.getCreatedDate());
        this.modified = post.isModified();
        this.viewCount = post.getViewCount();
        this.body = post.getBody();
        this.commentCount = post.getComments().size();
        this.images = post.getImages().stream()
                .map(PostImage::getName)
                .collect(Collectors.toList());
    }
}

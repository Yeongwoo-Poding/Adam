package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.comment.Comment;
import project.adam.entity.comment.CommentReport;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.fcm.FcmService;
import project.adam.fcm.dto.FcmRequestBuilder;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.post.PostRepository;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentReportRequest;
import project.adam.service.dto.comment.CommentUpdateRequest;

import java.io.IOException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FcmService fcmService;

    @Value("${report.hiddenCount}")
    private int reportHiddenCount;

    @Transactional
    public Comment create(Member member, CommentCreateRequest commentDto) throws IOException {
        Comment createdComment = commentRepository.save(new Comment(
                member,
                postRepository.findById(commentDto.postId).orElseThrow(),
                commentDto.getBody()
        ));

        if (isOthers(member, createdComment)) {
            sendPushToPostWriter(createdComment);
        }

        return createdComment;
    }

    private void sendPushToPostWriter(Comment comment) throws IOException {
        FcmRequestBuilder request = FcmRequestBuilder.builder()
                .member(comment.getPost().getWriter())
                .title(comment.getPost().getTitle() + "에 댓글이 달렸어요!")
                .body(comment.getBody())
                .postId(comment.getPost().getId())
                .build();
        fcmService.sendMessageTo(request);
    }

    private boolean isOthers(Member member, Comment comment) {
        return member != comment.getPost().getWriter();
    }

    public Comment find(Long commentId) {
        validateCommentHidden(commentId);
        return commentRepository.findById(commentId).orElseThrow();
    }

    public Slice<Comment> findByPost(Long postId, Pageable pageable) {
        return commentRepository.findRootCommentByPost(postRepository.findById(postId).orElseThrow(), pageable);
    }

    @Transactional
    public void update(Comment comment, CommentUpdateRequest commentDto) {
        validateCommentHidden(comment.getId());
        comment.update(commentDto.getBody());
    }

    @Transactional
    public void remove(Comment comment) {
        validateCommentHidden(comment.getId());
        commentRepository.delete(comment);
    }

    @Transactional
    public void createCommentReport(Member member, Comment comment, CommentReportRequest request) {
        validateCommentHidden(comment.getId());

        boolean isReportExist = comment.getReports().stream()
                .anyMatch(commentReport -> commentReport.getMember().equals(member));

        if (isReportExist) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }
        new CommentReport(comment, member, ReportType.valueOf(request.getReportType()));
    }

    private void validateCommentHidden(Long commentId) {
        if (commentRepository.countCommentReportById(commentId) >= reportHiddenCount) {
            throw new ApiException(ExceptionEnum.AUTHORIZATION_FAILED);
        }
    }
}

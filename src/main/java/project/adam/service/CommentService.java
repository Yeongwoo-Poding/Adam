package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Comment;
import project.adam.entity.CommentReport;
import project.adam.entity.Member;
import project.adam.entity.ReportType;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.CommentRepository;
import project.adam.repository.MemberRepository;
import project.adam.repository.PostRepository;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentReportRequest;
import project.adam.service.dto.comment.CommentUpdateRequest;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Value("${report.hiddenCount}")
    private int reportHiddenCount;

    @Transactional
    public Comment create(UUID writerId, Long postId, Long parentCommentId, CommentCreateRequest commentDto) {
        if (parentCommentId == null) {
            return commentRepository.save(new Comment(
                    memberRepository.findById(writerId).orElseThrow(),
                    postRepository.findById(postId).orElseThrow(),
                    null,
                    commentDto.getBody()
            ));
        } else {
            Comment parent = validateParentRoot(parentCommentId);
            return commentRepository.save(new Comment(
                    memberRepository.findById(writerId).orElseThrow(),
                    postRepository.findById(postId).orElseThrow(),
                    parent,
                    commentDto.getBody()
            ));
        }
    }

    private Comment validateParentRoot(Long parentCommentId) {
        Comment findComment = commentRepository.findById(parentCommentId).orElseThrow();
        if (findComment.getParent() != null) {
            throw new ApiException(ExceptionEnum.INVALID_REPLY);
        }
        return findComment;
    }

    @Transactional
    public Comment update(Long commentId, CommentUpdateRequest commentDto) {
        validateCommentHidden(commentId);
        Comment findComment = commentRepository.findById(commentId).orElseThrow();
        findComment.update(commentDto.getBody());
        return findComment;
    }

    @Transactional
    public void remove(Long commentId) {
        validateCommentHidden(commentId);
        commentRepository.delete(commentRepository.findById(commentId).orElseThrow());
    }

    public Comment find(Long commentId) {
        validateCommentHidden(commentId);
        return commentRepository.findById(commentId).orElseThrow();
    }

    public Slice<Comment> findByPost(Long postId, Pageable pageable) {
        return commentRepository.findRootCommentByPost(postRepository.findById(postId).orElseThrow(), pageable);
    }

    @Transactional
    public void createCommentReport(Comment comment, Member member, CommentReportRequest request) {
        validateCommentHidden(comment.getId());

        boolean isReportExist = comment.getReports().stream()
                .anyMatch(commentReport -> commentReport.getMember().equals(member));

        if (isReportExist) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }
        new CommentReport(comment, member, ReportType.valueOf(request.getReportType()));
    }

    @Transactional
    public void deleteCommentReport(Comment comment, Member member) {
        validateCommentHidden(comment.getId());

        CommentReport report = comment.getReports().stream()
                .filter(commentReport -> commentReport.getMember().equals(member))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.INVALID_REPORT));

        commentRepository.deleteCommentReportById(report.getId());
    }

    private void validateCommentHidden(Long commentId) {
        if (commentRepository.countCommentReportById(commentId) >= reportHiddenCount) {
            throw new ApiException(ExceptionEnum.AUTHORIZATION_FAILED);
        }
    }
}

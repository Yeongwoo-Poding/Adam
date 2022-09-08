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
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.member.MemberRepository;
import project.adam.repository.post.PostRepository;
import project.adam.security.SecurityUtil;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentReportRequest;
import project.adam.service.dto.comment.CommentUpdateRequest;

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
    public Comment create(Long postId, CommentCreateRequest commentDto) {
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentMemberEmail()).orElseThrow();

        return commentRepository.save(new Comment(
                member,
                postRepository.findById(postId).orElseThrow(),
                commentDto.getBody()
        ));
    }

    @Transactional
    public void update(Long commentId, CommentUpdateRequest commentDto) {
        validateCommentHidden(commentId);
        Comment findComment = commentRepository.findById(commentId).orElseThrow();
        findComment.update(commentDto.getBody());
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
    public void createCommentReport(Long commentId, CommentReportRequest request) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentMemberEmail()).orElseThrow();

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

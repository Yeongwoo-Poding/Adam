package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.entity.reply.Reply;
import project.adam.entity.reply.ReplyReport;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.member.MemberRepository;
import project.adam.repository.reply.ReplyRepository;
import project.adam.security.SecurityUtil;
import project.adam.service.dto.reply.ReplyCreateRequest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReplyService {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    @Value("${report.hiddenCount}")
    private int reportHiddenCount;

    @Transactional
    public Reply create(Member member, Long commentId, ReplyCreateRequest replyDto) {
        return replyRepository.save(
                new Reply(
                        member,
                        commentRepository.findById(commentId).orElseThrow(),
                        replyDto.getBody()));
    }

    public Reply find(Long replyId) {
        validateReplyHidden(replyId);
        return replyRepository.findById(replyId).orElseThrow();
    }

    public Slice<Reply> findAllByComment(Long commentId, Pageable pageable) {
        return replyRepository.findAllByCommentId(commentId, pageable);
    }

    @Transactional
    public void update(Long replyId, String body) {
        validateReplyHidden(replyId);
        Reply findReply = replyRepository.findById(replyId).orElseThrow();
        findReply.update(body);
    }

    @Transactional
    public void delete(Long replyId) {
        validateReplyHidden(replyId);
        replyRepository.delete(replyRepository.findById(replyId).orElseThrow());
    }

    @Transactional
    public void report(Member member, Long replyId, ReportType reportType) {
        Reply findReply = replyRepository.findById(replyId).orElseThrow();

        boolean isReportExist = findReply.getReports().stream()
                .anyMatch(replyReport -> replyReport.getMember().getId() == member.getId());

        if (isReportExist) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }

        new ReplyReport(findReply, member, reportType);
    }

    private void validateReplyHidden(Long replyId) {
        if (replyRepository.countReplyReportById(replyId) >= reportHiddenCount) {
            throw new ApiException(ExceptionEnum.AUTHORIZATION_FAILED);
        }
    }
}

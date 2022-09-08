package project.adam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.controller.dto.reply.ReplyCreateResponse;
import project.adam.controller.dto.reply.ReplyFindResponse;
import project.adam.controller.dto.reply.ReplyListFindResponse;
import project.adam.entity.common.ReportType;
import project.adam.entity.reply.Reply;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.service.ReplyService;
import project.adam.service.dto.reply.ReplyCreateRequest;
import project.adam.service.dto.reply.ReplyReportRequest;
import project.adam.service.dto.reply.ReplyUpdateRequest;

import java.util.Objects;

@RestController
@RequestMapping("/posts/{postId}/comments/{commentId}/replies")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @Secured("ROLE_USER")
    @PostMapping
    public ReplyCreateResponse createReply(@PathVariable Long commentId,
                                           @Validated @RequestBody ReplyCreateRequest createDto) {
        return new ReplyCreateResponse(replyService.create(commentId, createDto));
    }

    @GetMapping("/{replyId}")
    public ReplyFindResponse findReply(@PathVariable Long commentId,
                                       @PathVariable Long replyId) {
        validate(commentId, replyId);
        return new ReplyFindResponse(replyService.find(replyId));
    }

    @GetMapping
    public ReplyListFindResponse findReplies(@PathVariable Long commentId, Pageable pageable) {
        return new ReplyListFindResponse(replyService.findAllByComment(commentId, pageable));
    }

    @Secured("ROLE_USER")
    @PutMapping("/{replyId}")
    public void updateReply(@PathVariable Long commentId,
                                           @PathVariable Long replyId,
                                           @Validated @RequestBody ReplyUpdateRequest updateDto) {
        validate(commentId, replyId);
        Reply findReply = replyService.find(replyId);
        findReply.update(updateDto.getBody());
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{replyId}")
    public void deleteReply(@PathVariable Long commentId,
                            @PathVariable Long replyId) {
        validate(commentId, replyId);
        replyService.delete(replyId);
    }

    @Secured("ROLE_USER")
    @PostMapping("/{replyId}/report")
    public void reportReply(@PathVariable Long commentId,
                            @PathVariable Long replyId,
                            @Validated @RequestBody ReplyReportRequest reportDto) {
        validate(commentId, replyId);
        replyService.report(replyId, ReportType.valueOf(reportDto.getReportType()));
    }

    private void validate(Long commentId, Long replyId) {
        Reply reply = replyService.find(replyId);
        if (!Objects.equals(reply.getComment().getId(), commentId)) {
            throw new ApiException(ExceptionEnum.NO_DATA);
        }
    }
}

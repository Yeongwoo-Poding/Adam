package project.adam.utils.push;

import project.adam.entity.member.Member;
import project.adam.utils.push.dto.PushRequest;

public interface PushUtils {
    
    void pushAll(PushRequest request);
    void pushTo(Member member, PushRequest request);
}

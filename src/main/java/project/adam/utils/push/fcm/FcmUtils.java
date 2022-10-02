package project.adam.utils.push.fcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import project.adam.entity.member.Member;
import project.adam.entity.member.MemberStatus;
import project.adam.service.MemberService;
import project.adam.utils.push.PushUtils;
import project.adam.utils.push.dto.PushRequest;
import project.adam.utils.push.dto.PushResponse;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmUtils implements PushUtils {

    private static final String FIREBASE_CONFIG_PATH = "firebase/firebase_service_key.json";
    private static final String GOOGLE_API_URL = "https://www.googleapis.com/auth/cloud-platform";

    @Value("${application.id.client}")
    private String clientAppId;
    private String clientPushAPI;

    @PostConstruct
    public void init() {
        this.clientPushAPI = "https://fcm.googleapis.com/v1/projects/" + clientAppId + "/messages:send";
    }

    private final ObjectMapper objectMapper;
    private final MemberService memberService;

    @Async
    public void pushAll(PushRequest request) {
        int success = 0;
        int fail = 0;

        for (Member user : memberService.findLoginUsers()) {
            if (sendMessageTo(user, request)) {
                success += 1;
            } else {
                fail += 1;
            }
        }

        log.info("[FCM] Result: (Success: {}, Fail: {})", success, fail);
    }

    @Async
    public void pushTo(Member member, PushRequest request) {
        sendMessageTo(member, request);
    }

    private boolean sendMessageTo(Member member, PushRequest pushRequest) {
        if (!member.getStatus().equals(MemberStatus.LOGIN)) {
            return false;
        }

        try {
            String message = makeMessage(
                    member.getDeviceToken(),
                    pushRequest.getTitle(),
                    pushRequest.getBody(),
                    pushRequest.getPostId());
            log.info("[FCM] Send message to {}", member.getEmail());

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(clientPushAPI)
                    .post(requestBody)
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                    .build();

            Response response = client.newCall(request).execute();
            response.close();
            return response.code() == 200;
        } catch (IOException e) {
            throw new RuntimeException("Push Message 전송 오류");
        }
    }

    private String makeMessage(String targetToken, String title, String body, Long postId) throws JsonProcessingException {
        PushResponse.Notification notification = new PushResponse.Notification(title, body, null);
        PushResponse.Data data = new PushResponse.Data(String.valueOf(postId));
        PushResponse.Message message = new PushResponse.Message(targetToken, notification, data);
        PushResponse pushRequest = new PushResponse(false, message);

        return objectMapper.writeValueAsString(pushRequest);
    }

    private String getAccessToken() {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
                    new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream()).createScoped(List.of(GOOGLE_API_URL));
            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            throw new RuntimeException("Google 인증 오류");
        }
    }
}

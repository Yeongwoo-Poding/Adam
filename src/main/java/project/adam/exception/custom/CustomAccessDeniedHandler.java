package project.adam.exception.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import project.adam.exception.ApiExceptionEntity;
import project.adam.exception.ExceptionEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper om;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = om.writeValueAsString(new ApiExceptionEntity(ExceptionEnum.AUTHORIZATION_FAILED, UUID.randomUUID().toString().substring(0, 8)));
        response.getWriter().print(json);
    }
}

package project.adam.exception.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import project.adam.exception.ApiExceptionEntity;
import project.adam.exception.ExceptionEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class CustomMethodException extends AbstractHandlerExceptionResolver {

    private final ObjectMapper om;

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (!(ex instanceof HttpRequestMethodNotSupportedException)) {
            return null;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String json = om.writeValueAsString(new ApiExceptionEntity(ExceptionEnum.INVALID_METHOD, UUID.randomUUID().toString().substring(0, 8)));
            response.getWriter().print(json);
        } catch (Exception e) {
            String id = createId();
            log.warn("[{}] INVALID_METHOD at {}", id, request.getRequestURL(), e);
        }

        return new ModelAndView();
    }

    private String createId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}

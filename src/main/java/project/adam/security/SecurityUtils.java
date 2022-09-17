package project.adam.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;

public class SecurityUtils {
    private static final String ANONYMOUS = "anonymousUser";

    private SecurityUtils() {}

    public static String getCurrentMemberEmail() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null || authentication.getName().equals(ANONYMOUS)) {
            throw new ApiException(ExceptionEnum.AUTHENTICATION_FAILED);
        }

        return authentication.getName();
    }
}

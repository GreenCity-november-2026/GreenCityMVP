package greencity.converters;

import greencity.annotations.CurrentUser;
import greencity.dto.user.UserVO;
import greencity.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import java.security.Principal;

@Component
@AllArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    UserService userService;
    ModelMapper modelMapper;

    /**
     * Method checks if parameter is {@link UserVO} and is annotated with
     * {@link CurrentUser}.
     *
     * @param parameter method parameter
     * @return boolean
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null
            && parameter.getParameterType().equals(UserVO.class);
    }

    /**
     * Method returns {@link UserVO} by principal.
     *
     * @return {@link UserVO}
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String email = currentUserEmail(webRequest);
        return email != null ? userService.findByEmail(email) : null;
    }

    /**
     * Email of the authenticated user. The JWT filter stores it on the security
     * context, so a missing servlet principal must not be treated as anonymous.
     *
     * @param webRequest current request.
     * @return user email, or {@code null} when nobody is authenticated.
     */
    private String currentUserEmail(NativeWebRequest webRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isNamedUser(authentication)) {
            return authentication.getName();
        }
        Principal principal = webRequest.getUserPrincipal();
        if (principal == null || !isNamedUser(principal)) {
            return null;
        }
        return principal.getName();
    }

    private boolean isNamedUser(Principal principal) {
        if (principal == null || principal instanceof AnonymousAuthenticationToken) {
            return false;
        }
        if (principal instanceof Authentication authentication && !authentication.isAuthenticated()) {
            return false;
        }
        String name = principal.getName();
        return name != null && !name.isBlank();
    }
}

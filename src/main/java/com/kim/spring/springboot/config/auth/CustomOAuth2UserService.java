package com.kim.spring.springboot.config.auth;

import com.kim.spring.springboot.config.auth.dto.OAuthAttributes;
import com.kim.spring.springboot.config.auth.dto.SessionUser;
import com.kim.spring.springboot.domain.user.User;
import com.kim.spring.springboot.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.
                                        loadUser(userRequest);

        String registrationId        = userRequest
                                                .getClientRegistration().getRegistrationId();    //1. 현재 로그인 진행중인 서비스를 구분하는 코드.
        String userNameAttributeName = userRequest
                                                .getClientRegistration().getProviderDetails()
                                                .getUserInfoEndpoint().getUserNameAttributeName();  //2. Oauth2 로그인 진행시 키가 되는 필드값. PK와 같은 의미.

        OAuthAttributes attributes   = OAuthAttributes
                                                .of(registrationId, userNameAttributeName, oAuth2User.getAttributes()); //3. Oauth2UserService를 통해 가져온 Oauth2User의 attribute를 담을 클래스

        User user = saveOrUpdate(attributes);

        httpSession.setAttribute("user", new SessionUser(user)); // 4. 세션에 사용자 정보를 저장하기 위한 Dto클래스

        return new DefaultOAuth2User(
                                    Collections.singleton(
                                                        new SimpleGrantedAuthority(user.getRoleKey())),
                                                        attributes.getAttributes(),
                                                        attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                                                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                                                .orElse(attributes.toEntity());
        return userRepository.save(user);
    }
}

package com.kim.spring.springboot.config.auth.dto;

import com.kim.spring.springboot.domain.user.Role;
import com.kim.spring.springboot.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    // OAuth2User에서 반환하는 사용자정보가 MAP이기 때문에 값 하나하나 변환해야함.
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }
        if ("kakao".equals(registrationId)) {
            return ofKakao("id", attributes);
        }
        if ("facebook".equals(registrationId)) {
            return ofFacebook("id", attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {

        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();

    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");
        String name = (String) properties.get("nickname");
        String email = (String) kakao_account.get("email");
        String picture = (String) properties.get("profile_image");

        return OAuthAttributes.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofFacebook(String userNameAttributeName, Map<String, Object> attributes) {
        // 아래의 경우, 이메일을 반환하지 않는다.
        // No Email address on account
        // No confirmed email address on account
        // No verified email address on account
        // https://stackoverflow.com/questions/17532476/facebook-email-field-return-null-even-if-the-email-permission-is-set-and-acce
        String email = (String) attributes.get("email");
        if (email == null) {
            email = ((String) attributes.get("name")) + "@facebook.com";
        }

        Map<String, Object> picture = (Map<String, Object>) attributes.get("picture");
        Map<String, Object> picture_data = (Map<String, Object>) picture.get("data");
        String picture_url = (String) picture_data.get("url");

        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email(email)
                .picture(picture_url)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // User 엔티티 생성. 처음 가입할때 생성됨. 가입 기본권한을 USER로 줌.
    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .role(Role.USER)
                .build();
    }
}

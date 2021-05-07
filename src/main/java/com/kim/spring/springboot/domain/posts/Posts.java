package com.kim.spring.springboot.domain.posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor // 기본 생성자 자동 추가
@Entity // 테이블과 링크될 클래스임을 나타냄, 기본값으로 클래스의 카멜케이스 이름을 언더스코어 네이밍으로 테이블 이름을 매칭함.
public class Posts {

    @Id // 해당 테이블의 PK필드를 나타냄
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK의 생성 규칙을 나타냄. boot2.0부터는 IDENTITY옵션을 추가해야 auto_increment가 됨.
    private Long id;

    @Column(length = 500, nullable = false) // 테이블의 컬럼을 나타냄. 선언하지 않더라도, 클래스의 필드는 모두 컬럼이 됨. 기본값 외에 추가로 변경이 필요한 옵션이 있다면 사용. 사이즈나 타입등.
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String author;

    @Builder // 해당클래스의 빌더 패턴클래스 생성, 생성자 상단에 선언시 생성자에 포함된 필드만 빌더에 포함
    public Posts(String title, String content, String author){
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }

}

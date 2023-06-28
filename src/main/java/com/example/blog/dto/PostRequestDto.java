package com.example.blog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {
    //제목, 작성자명, 비밀번호,  내용
    //클라이언트가 이 내용을 보내며 요청
    private String title;
    private String writer;
    private String password;
    private String contents;

    public PostRequestDto(String title, String writer, String password, String content) {
        this.title = title;
        this.writer = writer;
        this.password = password;
        this.contents = content;
    }

}

package com.example.blog.entity;

import com.example.blog.dto.PostRequestDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Post {
    private Long id;
    private String title;
    private String writer;
    private String contents;
    private String password;
    private LocalDateTime createdAt;

    public Post(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.writer = postRequestDto.getWriter();
        this.contents = postRequestDto.getContent();
        this.password = postRequestDto.getPassword();
        this.createdAt = LocalDateTime.now();
    }
}

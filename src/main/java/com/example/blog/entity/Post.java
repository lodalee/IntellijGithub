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
    private String content;
    private String password;
    private LocalDateTime createdAt;

    public Post(PostRequestDto postRequestDTO) {
        this.title = postRequestDTO.getTitle();
        this.writer = postRequestDTO.getWriter();
        this.content = postRequestDTO.getContent();
        this.password = postRequestDTO.getPassword();
        this.createdAt = LocalDateTime.now();
    }
}

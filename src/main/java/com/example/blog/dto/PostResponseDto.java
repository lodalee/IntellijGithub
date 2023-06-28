package com.example.blog.dto;

import com.example.blog.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    //제목, 작성자명, 작성 내용, 작성 날짜를 내보내줌
    private String title;
    private String writer;
    private String content;
    private LocalDateTime createdAt;

    public PostResponseDto(Post post) {
        this.title = post.getTitle();
        this.writer = post.getWriter();
        this.content = post.getContents();
        this.createdAt = post.getCreatedAt();
    }

    public PostResponseDto(String title, String writer, String password, String content) {
    }

    public PostResponseDto(String title, String writer, String content, LocalDateTime createdAt ) {
        this.title = title;
        this.writer = writer;
        this.content = content;
       this.createdAt = createdAt;
    }

}

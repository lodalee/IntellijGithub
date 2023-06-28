package com.example.blog.controller;

import com.example.blog.dto.PostRequestDto;
import com.example.blog.dto.PostResponseDto;
import com.example.blog.entity.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BlogController {

    private final JdbcTemplate jdbcTemplate;

    public BlogController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @PostMapping("/post")
    //접근제어자 반환값 메서드명 (입력값...){}
    public PostResponseDto writePost(@RequestBody PostRequestDto postRequestDto){
        //1. 클라이언트가 요청한 데이터를 받아온다.[postRequestDTO]
        //2. 받아온 데이터를 DB에 저장할 수 있는 데이터로 변환을 해줘야 한다.[postRequestDto -> post]
        Post post = new Post(postRequestDto);

        //3. DB에 post를 저장한다.
        KeyHolder keyHolder = new GeneratedKeyHolder(); // 기본 키를 반환받기 위한 객체

        String sql = "INSERT INTO post (title, contents, writer, password, createdAt) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update( con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

                    preparedStatement.setString(1, post.getTitle());
                    preparedStatement.setString(2, post.getContents());
                    preparedStatement.setString(3, post.getWriter());
                    preparedStatement.setString(4, post.getPassword());
                    preparedStatement.setTimestamp(5, Timestamp.valueOf(post.getCreatedAt()));
                    return preparedStatement;
                },
                keyHolder);

        // DB Insert 후 받아온 기본키 확인
        Long id = keyHolder.getKey().longValue();
        post.setId(id);


        //4. 저장한 post를 반환해줄 수 있는 PostResponseDTO 변환을 해야 한다.[post -> PostResponseDTO ]
        // Entity -> ResponseDto
        PostResponseDto postResponseDto = new PostResponseDto(post);
        //5. PostResponseDTO 반환해준다.
        return postResponseDto;
    }

    @GetMapping("/post")
    public List<PostResponseDto> getPosts() {
        // DB 조회
        String sql = "SELECT * FROM post";

        return jdbcTemplate.query(sql, new RowMapper<PostResponseDto>() {
            @Override
            public PostResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                // SQL 의 결과로 받아온 Post 데이터들을 PostResponseDto 타입으로 변환해줄 메서드
                String title = rs.getString("title");
                String writer = rs.getString("writer");
                String content = rs.getString("contents");  //컬럼 명이 달라서 오류 -> 관련 이름 다 바꿈
                //timestamp를 localdatetime
                LocalDateTime createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
                return new PostResponseDto(title, writer, content, createdAt);
            }
        });
    }
}
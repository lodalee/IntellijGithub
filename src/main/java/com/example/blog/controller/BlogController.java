package com.example.blog.controller;

import com.example.blog.dto.PostRequestDto;
import com.example.blog.dto.PostResponseDto;
import com.example.blog.entity.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api")
public class BlogController {

    private final JdbcTemplate jdbcTemplate;

    public BlogController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @PostMapping("/post")
    //접근제어자 반환값 메서드명 (입력값...){}
    public PostResponseDto writePost(@RequestBody PostRequestDto postRequestDTO){
        //1. 클라이언트가 요청한 데이터를 받아온다.[postRequestDTO]
        //2. 받아온 데이터를 DB에 저장할 수 있는 데이터로 변환을 해줘야 한다.[postRequestDTO -> post]
        Post post = new Post(postRequestDTO);

        //3. DB에 post를 저장한다.
        KeyHolder keyHolder = new GeneratedKeyHolder(); // 기본 키를 반환받기 위한 객체

        String sql = "INSERT INTO post (title, contents, writer, password, createdAt) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update( con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

                    preparedStatement.setString(1, post.getTitle());
                    preparedStatement.setString(2, post.getContent());
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
}

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
import java.util.List;

@RestController
@RequestMapping("/api")
public class BlogController {

    private final JdbcTemplate jdbcTemplate;

    public BlogController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //게시글 작성
    @PostMapping("/post")
    //접근제어자 반환값 메서드명 (입력값...){}
    public PostResponseDto writePost(@RequestBody PostRequestDto postRequestDto) {
        //1. 클라이언트가 요청한 데이터를 받아온다.[postRequestDTO]
        //2. 받아온 데이터를 DB에 저장할 수 있는 데이터로 변환을 해줘야 한다.[postRequestDto -> post]
        Post post = new Post(postRequestDto);

        //3. DB에 post를 저장한다.
        KeyHolder keyHolder = new GeneratedKeyHolder(); // 기본 키를 반환받기 위한 객체

        String sql = "INSERT INTO post (title, contents, writer, password, createdAt) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
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

    //전체 게시글 조회
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

    //선택한 게시글 조회 API
    //무엇을 받아서 어떤 행동을 할거야? 클라이언트 --(요청)--> 서버 [ 무언가를 보내서 어떤 처리를 한 후에 무언가를 받고 싶어 ]
    //게시글 Id
    //접근제어자 반환값 메서드명 (입력값...매개변수){}
    @GetMapping("/post/{id}")
    public PostResponseDto selectedPost(@PathVariable Long id) {
        Post post = findById(id);

        if (post != null) {
            System.out.println(post);
            return new PostResponseDto(post);
        } else {
            throw new IllegalArgumentException("선택한 메모는 존재하지 않습니다.");
        }
    }


    //선택한 게시글 수정 API
    //무엇을 받아서 어떤 행동을 할거야?
    //게시글 id, 수정할 게시글 내용
    @PutMapping("/post/{id}")
    public PostResponseDto updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        // 1. 바꿀 얘를 찾아야 한다.
        Post post = findById(id);

        // 2. 바꿔준다.
        if (post != null) {
            String password = requestDto.getPassword();
            String dbPassword = post.getPassword();
            if (password.equals(dbPassword)) {
                // post 내용 수정
                String sql = "UPDATE post SET title = ?, writer = ?, contents = ? WHERE id = ?";

                jdbcTemplate.update(sql, requestDto.getTitle(), requestDto.getWriter(), requestDto.getContents(), id);

                // 1. requestDTO 변경할 내용을 담아왔잖아요. => 이걸 그대로 보내줘도 된다.
                // 2. DB post 테이블에 저장이 되어있죠. => DB에서 다시 가져오는 방법이 있어요.

                // 3. 바꾼 내용을 반환해준다.
                PostResponseDto postResponseDto =
                        new PostResponseDto(requestDto.getTitle(), requestDto.getWriter(), requestDto.getContents(), post.getCreatedAt());

                return postResponseDto;
            } else {
                throw new IllegalArgumentException("비밀 번호가 틀립니다.");
            }
        } else {
            throw new IllegalArgumentException("선택한 메모는 존재하지 않습니다.");
        }
    }

        // 해당 메모가 DB에 존재하는지 확인


    //선택한 게시글 삭제 API
    @DeleteMapping("/post/{id}")
    public String deletePost(@PathVariable Long id) {
        // 해당 메모가 DB에 존재하는지 확인
        Post post = findById(id);
        if(post != null) {
            // post 삭제
            String sql = "DELETE FROM post WHERE id = ?";
            jdbcTemplate.update(sql, id);

            return "삭제 하였습니다.";
        } else {
            throw new IllegalArgumentException("선택한 메모는 존재하지 않습니다.");
        }
    }

    private Post findById(Long id) {
        // DB 조회
        String sql = "SELECT * FROM post WHERE id = ?";

        return jdbcTemplate.query(sql, resultSet -> {
            if(resultSet.next()) {
                Post post = new Post();  // 기본생성자 생성
                post.setPassword(resultSet.getString("password"));
                post.setTitle(resultSet.getString("title"));
                post.setWriter(resultSet.getString("writer"));
                post.setContents(resultSet.getString("contents"));
                post.setCreatedAt(resultSet.getTimestamp("createdAt").toLocalDateTime());
                return post;
            } else {
                return null;
            }
        }, id);
    }


}
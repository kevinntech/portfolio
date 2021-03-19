package com.kevinntech.modules.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevinntech.modules.account.Account;
import com.kevinntech.modules.account.AccountRepository;
import com.kevinntech.modules.account.WithAccount;
import com.kevinntech.modules.board.domain.Board;
import com.kevinntech.modules.board.domain.Comment;
import com.kevinntech.modules.board.form.CommentForm;
import com.kevinntech.modules.board.repository.BoardRepository;
import com.kevinntech.modules.board.repository.CommentRepository;
import com.kevinntech.modules.board.service.BoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired BoardFactory boardFactory;
    @Autowired ModelMapper modelMapper;
    @Autowired ObjectMapper objectMapper;
    @Autowired CommentRepository commentRepository;

    @WithAccount("kevin")
    @DisplayName("댓글 가져오기")
    @Test
    void getComments() throws Exception{
        Account kevin = accountRepository.findByNickname("kevin");
        Board board = boardFactory.createPost(kevin);

        mockMvc.perform(get("/comment/" + board.getId()))
                .andExpect(status().isOk());
    }

    @WithAccount("kevin")
    @DisplayName("댓글 등록")
    @Test
    void createComments() throws Exception{
        Account kevin = accountRepository.findByNickname("kevin");
        Board board = boardFactory.createPost(kevin);

        CommentForm commentForm = new CommentForm();
        commentForm.setContent("new Comment");
        commentForm.setUpdatedDate(LocalDateTime.now());
        commentForm.setUpdatedByNickname(kevin.getNickname());
        commentForm.setProfileImage(kevin.getProfileImage());

        mockMvc.perform(post("/comment/" + board.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentForm))
                .with(csrf()))
                .andExpect(status().isCreated());

        List<Comment> commentsOfBoard = commentRepository.getCommentsOfBoard(board);
        assertTrue(commentsOfBoard.size() > 0);
    }

    @WithAccount("kevin")
    @DisplayName("댓글 수정")
    @Test
    void modifyComments() throws Exception{
        Account kevin = accountRepository.findByNickname("kevin");
        Board board = boardFactory.createPost(kevin);

        Comment newComment = commentRepository.save(Comment.builder()
                .board(board)
                .content("new Comment")
                .build());

        CommentForm commentForm = new CommentForm();
        commentForm.setContent("Modified Comment");
        commentForm.setUpdatedDate(LocalDateTime.now());
        commentForm.setUpdatedByNickname(kevin.getNickname());
        commentForm.setProfileImage(kevin.getProfileImage());

        mockMvc.perform(put("/comment/" + board.getId() + "/" + newComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentForm))
                .with(csrf()))
                .andExpect(status().isOk());

        List<Comment> commentsOfBoard = commentRepository.getCommentsOfBoard(board);
        assertTrue(commentsOfBoard.size() > 0);

        Comment savedComment = commentsOfBoard.get(0);
        assertTrue(savedComment.getContent().equals(commentForm.getContent()));
    }

    @WithAccount("kevin")
    @DisplayName("댓글 삭제")
    @Test
    void removeComments() throws Exception{
        Account kevin = accountRepository.findByNickname("kevin");
        Board board = boardFactory.createPost(kevin);

        Comment newComment = commentRepository.save(Comment.builder()
                .board(board)
                .content("new Comment")
                .build());

        mockMvc.perform(delete("/comment/" + board.getId() + "/" + newComment.getId())
                    .with(csrf()))
                .andExpect(status().isOk());

        List<Comment> commentsOfBoard = commentRepository.getCommentsOfBoard(board);

        assertFalse(commentsOfBoard.contains(newComment));
    }

}
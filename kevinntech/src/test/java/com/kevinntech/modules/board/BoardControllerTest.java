package com.kevinntech.modules.board;

import com.kevinntech.modules.account.Account;
import com.kevinntech.modules.account.AccountRepository;
import com.kevinntech.modules.account.WithAccount;
import com.kevinntech.modules.board.domain.Board;
import com.kevinntech.modules.board.repository.BoardRepository;
import com.kevinntech.modules.board.service.BoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class BoardControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired BoardService boardService;
    @Autowired BoardRepository boardRepository;
    @Autowired BoardFactory boardFactory;
    @Autowired ModelMapper modelMapper;

    @Test
    @WithAccount("kevin")
    @DisplayName("게시글 작성 폼 조회")
    void createPostForm() throws Exception {
        mockMvc.perform(get("/new-board"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("boardForm"));
    }

    @Test
    @WithAccount("kevin")
    @DisplayName("게시글 작성하기")
    void createPost() throws Exception {
        mockMvc.perform(post("/new-board")
                    .param("title", "테스트 제목 입니다.")
                    .param("content", "테스트 내용 입니다.")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithAccount("kevin")
    @DisplayName("게시글 조회")
    void viewPost() throws Exception {
        Account kevin = accountRepository.findByNickname("kevin");
        Board board = boardFactory.createPost(kevin);

        mockMvc.perform(get("/board/" + board.getId()))
                .andExpect(view().name("board/view"))
                .andExpect(model().attributeExists("postAccount"))
                .andExpect(model().attributeExists("board"))
                .andExpect(model().attributeExists("isOwner"));
    }

    @WithAccount("kevin")
    @DisplayName("게시글 수정 폼")
    @Test
    void updatePostForm() throws Exception{
        Account kevin = accountRepository.findByNickname("kevin");
        Board board = boardFactory.createPost(kevin);

        mockMvc.perform(get("/board/" + board.getId() + "/modify"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("boardForm"));
    }

    @WithAccount("kevin")
    @DisplayName("게시글 수정 - 성공")
    @Test
    void updatePost_success() throws Exception{
        Account kevin = accountRepository.findByNickname("kevin");
        Board board = boardFactory.createPost(kevin);

        mockMvc.perform(post("/board/" + board.getId() + "/modify")
                .param("title", "게시글 제목을 수정 합니다.")
                .param("content", "게시글 내용을 수정 합니다.")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + board.getId()))
                .andExpect(flash().attributeExists("message"));
    }

    @WithAccount("kevin")
    @DisplayName("게시글 수정 - 실패")
    @Test
    void updatePost_fail() throws Exception{
        Account kevin = accountRepository.findByNickname("kevin");
        Board board = boardFactory.createPost(kevin);

        mockMvc.perform(post("/board/" + board.getId() + "/modify")
                .param("title", "")
                .param("content", "게시글 내용을 수정 합니다.")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());
    }

    @Test
    @WithAccount("kevin")
    @DisplayName("게시글 삭제")
    void removePost() throws Exception {
        Account kevin = accountRepository.findByNickname("kevin");
        Board board = boardFactory.createPost(kevin);

        mockMvc.perform(post("/board/" + board.getId() + "/delete")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));
    }

}
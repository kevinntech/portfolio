package com.kevinntech.modules.board.service;

import com.kevinntech.modules.account.Account;
import com.kevinntech.modules.account.AccountRepository;
import com.kevinntech.modules.board.domain.Board;
import com.kevinntech.modules.board.domain.Comment;
import com.kevinntech.modules.board.form.BoardForm;
import com.kevinntech.modules.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final AccountRepository accountRepository;

    private final ModelMapper modelMapper;

    public Board createNewBoard(Board board, Account account) {
        Board newBoard = boardRepository.save(board);
        newBoard.initAfterCreateBoard(account);

        return newBoard;
    }

    public Board getPost(Long id) {
        Optional<Board> byId = this.boardRepository.findById(id);

        if(!byId.isPresent()) {
            throw new IllegalArgumentException("요청한 게시글이 없습니다.");
        }

        return byId.get();
    }

    /* 이메일에 해당하는 닉네임 가져오기 */
    public String getNicknameByEmail(String email) {
        Account byEmail = accountRepository.findByEmail(email);

        if(byEmail == null){
            throw new IllegalArgumentException("해당 회원은 존재하지 않습니다.");
        }

        return byEmail.getNickname();
    }

    /* 게시글 수정하기 */
    public void updateBoard(Board board, BoardForm boardForm) {
        modelMapper.map(boardForm, board);
        board.updateBoardDate();

        boardRepository.save(board);
    }

    public void processDeleteBoard(Long id) {
        // 파라미터로 받은 id와 일치하는 게시글을 찾는다.
        Board board = getPost(id);

        if(board != null){
            boardRepository.deleteById(id);
        }
    }

}

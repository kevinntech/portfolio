package com.kevinntech.modules.board;

import com.kevinntech.modules.account.Account;
import com.kevinntech.modules.board.domain.Board;
import com.kevinntech.modules.board.repository.BoardRepository;
import com.kevinntech.modules.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardFactory {

    @Autowired BoardService boardService;

    public Board createPost(Account account) {
        Board board = new Board();
        board.setTitle("테스트 제목 입니다.");
        board.setContent("테스트 내용 입니다.");
        boardService.createNewBoard(board, account);

        return board;
    }

}

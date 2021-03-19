package com.kevinntech.modules.board.repository;

import com.kevinntech.modules.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface BoardRepositoryExtension {

    Page<Board> search(String type, String keyword, Pageable pageable);

}

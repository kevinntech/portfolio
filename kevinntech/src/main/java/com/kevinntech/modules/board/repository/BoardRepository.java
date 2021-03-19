package com.kevinntech.modules.board.repository;

import com.kevinntech.modules.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryExtension {
}






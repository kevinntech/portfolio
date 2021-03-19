package com.kevinntech.modules.board.repository;

import com.kevinntech.modules.board.domain.Board;
import com.kevinntech.modules.board.domain.Comment;
import com.kevinntech.modules.board.form.CommentForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.board = ?1 " +
            " AND c.id > 0 ORDER BY c.id ASC")
    public List<Comment> getCommentsOfBoard(Board board);

    @Query("SELECT new com.kevinntech.modules.board.form.CommentForm(c.id, c.content, c.updatedDate, a.nickname, a.profileImage) " +
           "FROM Comment c INNER JOIN Account a ON c.updatedBy = a.email " +
           "WHERE c.board = ?1 " +
           "AND c.id > 0 ORDER BY c.id ASC")
    public List<CommentForm> getCommentsAndAccountOfBoard(Board board);
}

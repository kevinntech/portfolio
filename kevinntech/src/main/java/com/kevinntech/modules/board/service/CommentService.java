package com.kevinntech.modules.board.service;

import com.kevinntech.modules.account.Account;
import com.kevinntech.modules.board.domain.Board;
import com.kevinntech.modules.board.domain.Comment;
import com.kevinntech.modules.board.form.CommentForm;
import com.kevinntech.modules.board.repository.BoardRepository;
import com.kevinntech.modules.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final BoardRepository boardRepository;

    private final ModelMapper modelMapper;

    public Board processNewComment(Account account, Long bid, CommentForm commentForm) {
        Board board = getBoardById(bid);

        Comment comment = modelMapper.map(commentForm, Comment.class);

        comment.createRelation(board);

        comment.initAfterCreateComment(account);

        commentRepository.save(comment);

        return board;
    }

    public List<CommentForm> getListByBoard(Board board){
        /* 참조변수명 대신에 클래스명으로 작성하지 않았는지 확인하자. */
        return commentRepository.getCommentsAndAccountOfBoard(board);
    }

    public Board getBoardById(Long bid) {
        Optional<Board> boardById = boardRepository.findById(bid);

        if(!boardById.isPresent()) {
            throw new IllegalArgumentException("요청한 게시글이 없습니다.");
        }

        Board board = boardById.get();

        return board;
    }

    public Comment getCommentById(Long cid) {
        Optional<Comment> commentById = commentRepository.findById(cid);

        if(!commentById.isPresent()) {
            throw new IllegalArgumentException("요청한 댓글이 없습니다.");
        }

        Comment comment = commentById.get();

        return comment;
    }

    public Board processUpdateComment(Long bid, Long cid, CommentForm commentForm) {
        Board board = getBoardById(bid);

        // 댓글 수정
        Comment comment = getCommentById(cid);
        comment.setContent(commentForm.getContent());
        comment.updateCommentDate();

        commentRepository.save(comment);

        return board;
    }

    public Board processDeleteComment(Long bid, Long cid) {
        // 해당 게시판과 댓글 id에 해당하는 데이터를 찾는다.
        Board board = getBoardById(bid);
        Comment comment = getCommentById(cid);

        // 연관관계를 끊고 댓글을 삭제한다.
        comment.deleteRelation(board);
        commentRepository.deleteById(cid);

        return board;
    }
}

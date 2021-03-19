package com.kevinntech.modules.board.repository;

import com.kevinntech.modules.board.domain.Board;
import com.kevinntech.modules.board.domain.QBoard;
import com.kevinntech.modules.board.domain.QComment;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class BoardRepositoryExtensionImpl extends QuerydslRepositorySupport implements BoardRepositoryExtension{

    public BoardRepositoryExtensionImpl() {
        super(Board.class);
    }

    @Override
    public Page<Board> search(String type, String keyword, Pageable pageable) {

        QBoard board = QBoard.board;
        QComment comment = QComment.comment;

        JPQLQuery<Board> query = null;

        if(type != null){

            switch (type.toUpperCase()) {
                case "TITLE":
                    query = from(board)
                            .where(board.title.like("%" + keyword +"%").and(board.id.gt(0L)));
                    break;
//                case "CONTENT":
//                    query = from(board)
//                            .where(board.content.contains(keyword).and(board.id.gt(0L)))
//                            .leftJoin(comment)
//                            .on(board.id.eq(comment.board.id));
//                    break;
                case "WRITER":
                    query = from(board)
                            .where(board.updatedByNickname.like("%" + keyword +"%").and(board.id.gt(0L)));
                    break;
                case "ALL":
                    query = from(board).fetchAll();
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        List<Board> boards = getQuerydsl().applyPagination(pageable, query).fetch();
        return new PageImpl<>(boards, pageable, query.fetchCount());
    }
}

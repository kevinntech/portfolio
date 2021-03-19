package com.kevinntech.modules.board.controller;

import com.kevinntech.modules.account.Account;
import com.kevinntech.modules.account.CurrentAccount;
import com.kevinntech.modules.board.domain.Board;
import com.kevinntech.modules.board.domain.Comment;
import com.kevinntech.modules.board.form.CommentForm;
import com.kevinntech.modules.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	/* 댓글 가져오기 */
	@GetMapping("/{bid}")
	public ResponseEntity<List<CommentForm>> getComments(@PathVariable Long bid){
		Board board = new Board();
		board.setId(bid);

		return new ResponseEntity<>(commentService.getListByBoard(board), HttpStatus.OK);
	}

	/* 댓글 등록 */
	@PostMapping("/{bid}")
	public ResponseEntity<List<CommentForm>> createComments(@CurrentAccount Account account, @PathVariable Long bid, @RequestBody CommentForm commentForm){
		Board board = commentService.processNewComment(account, bid, commentForm);

		return new ResponseEntity<>(commentService.getListByBoard(board), HttpStatus.CREATED);
	}

	/* 댓글 수정 */
	@PutMapping("/{bid}/{cid}")
	public ResponseEntity<List<CommentForm>> modify(@PathVariable Long bid, @PathVariable Long cid, @RequestBody CommentForm commentForm){
		Board board = commentService.processUpdateComment(bid, cid, commentForm);

		return new ResponseEntity<>(commentService.getListByBoard(board), HttpStatus.OK);
	}

	/* 댓글 삭제 */
	@DeleteMapping("/{bid}/{cid}")
	public ResponseEntity<List<CommentForm>> remove(@PathVariable Long bid, @PathVariable Long cid){
		Board board = commentService.processDeleteComment(bid, cid);

		return new ResponseEntity<>(commentService.getListByBoard(board), HttpStatus.OK);
	}
}




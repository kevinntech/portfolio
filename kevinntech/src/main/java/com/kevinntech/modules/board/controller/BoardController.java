package com.kevinntech.modules.board.controller;

import com.kevinntech.modules.account.Account;
import com.kevinntech.modules.account.AccountService;
import com.kevinntech.modules.account.CurrentAccount;
import com.kevinntech.modules.board.domain.Board;
import com.kevinntech.modules.board.form.BoardForm;
import com.kevinntech.modules.board.repository.BoardRepository;
import com.kevinntech.modules.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class BoardController {

	private final AccountService accountService;

	private final BoardService boardService;

	private final ModelMapper modelMapper;

	private final BoardRepository boardRepository;

	/* 게시글 작성 폼 View로 이동 */
	@GetMapping("/board/list")
	public String boardList(@CurrentAccount Account account, String type, String keyword, Model model,
							@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
									Pageable pageable) {
		if(type == null)
			type = "ALL";

		if(keyword == null)
			keyword = "";

		if(account != null)
			model.addAttribute(account); // 현재 사용자

		Page<Board> boardPage = boardRepository.search(type, keyword, pageable);

		model.addAttribute("boardPage", boardPage);
		model.addAttribute("type", type);
		model.addAttribute("keyword", keyword);
		model.addAttribute("sortProperty",
				pageable.getSort().toString().contains("id") ? "id" : "updatedDate");

		return "board/list";
	}

	/* 게시글 작성 폼 View로 이동 */
	@GetMapping("/new-board")
	public String newBoardForm(@CurrentAccount Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(new BoardForm());

		return "board/form";
	}

	/* 게시글 작성 폼 데이터를 등록 */
	@PostMapping("/new-board")
	public String newBoardSubmit(@CurrentAccount Account account, @Valid BoardForm boardForm, Errors errors, Model model) {
		if (errors.hasErrors()) {
			model.addAttribute(account);
			return "board/form";
		}

		Board newBoard = boardService.createNewBoard(modelMapper.map(boardForm, Board.class), account);

		return "redirect:/board/" + newBoard.getId();
	}

	/* 게시글 보기 */
	@GetMapping("/board/{id}")
	public String viewBoard(@CurrentAccount Account account, @PathVariable Long id, Model model) {
		Board board = boardService.getPost(id);
		String postNickname = boardService.getNicknameByEmail(board.getCreatedBy());
		Account postAccount = accountService.getAccount(postNickname);
		boolean isOwner;

		if(account != null) {
			isOwner = postAccount.equals(account);
			model.addAttribute(account); // 현재 사용자
			model.addAttribute("currentUser", account.getNickname()); // 현재 사용자 닉네임
		}else{
			isOwner = false;
			model.addAttribute("currentUser", "");
		}

		model.addAttribute("postAccount", postAccount);  // 게시글 작성자
		model.addAttribute(board);
		model.addAttribute("isOwner", isOwner); // 게시글 작성자와 현재 사용자가 같은지 비교
		model.addAttribute(modelMapper.map(board, BoardForm.class));

		return "board/view";
	}

	/* 게시글 수정 폼*/
	@GetMapping("/board/{id}/modify")
	public String updateBoardForm(@CurrentAccount Account account, @PathVariable Long id, Model model) {
		Board board = boardService.getPost(id);

		model.addAttribute(account);
		model.addAttribute(modelMapper.map(board, BoardForm.class));

		return "board/modify"; // modify 폼 보여주기
	}

	/* 게시글 수정하기 */
	@PostMapping("/board/{id}/modify")
	public String updateBoard(@CurrentAccount Account account, @PathVariable Long id, @Valid BoardForm boardForm,
							  Errors errors, Model model, RedirectAttributes attributes) {
		if (errors.hasErrors()) {
			model.addAttribute(account);

			return "board/modify";
		}

		Board board = boardService.getPost(id);

		boardService.updateBoard(board, boardForm);

		attributes.addFlashAttribute("message", "게시글을 수정했습니다.");

		return "redirect:/board/" + id;
	}

	/* 게시글 삭제 */
	@PostMapping("/board/{id}/delete")
	public String removeBoard(@CurrentAccount Account account, @PathVariable Long id, Model model, RedirectAttributes attributes){
		boardService.processDeleteBoard(id);

		attributes.addFlashAttribute("message", "게시글을 수정했습니다.");

		return "redirect:/board/list";
	}
}




package com.kevinntech.modules.board.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kevinntech.modules.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;


@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor @NoArgsConstructor
@ToString(exclude = "board")
public class Comment {

	@Id
	@GeneratedValue
	private Long id;
	
	private String content;

	private LocalDateTime createdDate;	// 생성일자

	private String createdBy;			// 생성자

	private LocalDateTime updatedDate;	// 수정일자

	private String updatedBy;			// 수정자

	/* @JsonIgnore는 특정 속성이 JSON으로 변환되지 않도록 한다. */
	@JsonIgnore
	@ManyToOne(fetch = LAZY)
	private Board board;

    public void createRelation(Board board) {
    	this.board = board;
    	board.getComments().add(this);
    }

	public void deleteRelation(Board board) {
		this.board = null;
		board.getComments().remove(this);
	}

	/* 게시글 작성 후 기본적인 초기화 */
	public void initAfterCreateComment(Account account) {
		this.createdDate = LocalDateTime.now();
		this.updatedDate = LocalDateTime.now();
		this.createdBy = account.getEmail();
		this.updatedBy = account.getEmail();
	}

	/* 게시글 작성 후 기본적인 초기화 */
	public void updateCommentDate() {
		this.updatedDate = LocalDateTime.now();
	}
}

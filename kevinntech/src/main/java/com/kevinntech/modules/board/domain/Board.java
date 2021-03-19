package com.kevinntech.modules.board.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kevinntech.modules.account.Account;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor @NoArgsConstructor
@ToString(exclude = "comments")
public class Board {

	@Id
	@GeneratedValue
	private Long id;

	private String title;

	@Lob @Basic(fetch = FetchType.EAGER)
	private String content;

	private LocalDateTime createdDate;	// 생성일자

	private String createdBy;			// 생성자

	private LocalDateTime updatedDate;	// 수정일자

	private String updatedBy;			// 수정자

	private String updatedByNickname;

	/* @JsonIgnore는 특정 속성이 JSON으로 변환되지 않도록 한다. */
	@JsonIgnore
	@OneToMany(mappedBy = "board", fetch = LAZY, cascade = CascadeType.ALL)
	private List<Comment> comments = new ArrayList<>();

	/* 게시글 작성 후 기본적인 초기화 */
	public void initAfterCreateBoard(Account account) {
		this.createdDate = LocalDateTime.now();
		this.updatedDate = LocalDateTime.now();
		this.createdBy = account.getEmail();
		this.updatedBy = account.getEmail();
		this.updatedByNickname = account.getNickname();
	}

	/* 게시글 작성 후 기본적인 초기화 */
	public void updateBoardDate() {
		this.updatedDate = LocalDateTime.now();
	}
}

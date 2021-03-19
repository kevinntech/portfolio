package com.kevinntech.modules.board.form;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentForm {

    private Long id;

    private String content;

    private LocalDateTime updatedDate;	// 수정일자

    private String updatedByNickname;   // 수정자

    private String profileImage; // 수정자 프로필 이미지

    public CommentForm(Long id, String content, LocalDateTime updatedDate, String updatedByNickname, String profileImage) {
        this.id = id;
        this.content = content;
        this.updatedDate = updatedDate;
        this.updatedByNickname = updatedByNickname;
        this.profileImage = profileImage;
    }

}

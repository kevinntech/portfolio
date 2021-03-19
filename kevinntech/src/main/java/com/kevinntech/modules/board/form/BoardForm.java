package com.kevinntech.modules.board.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
public class BoardForm {

    private Long id;

    @NotBlank
    @Length(min = 1, max = 50)
    private String title;

    @NotBlank
    private String content;

}

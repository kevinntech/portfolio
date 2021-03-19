package com.kevinntech.modules.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class Profile {

    @Length(max = 35)
    private String bio;

    private String profileImage;
}


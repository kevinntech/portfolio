package com.kevinntech.modules.main;

import com.kevinntech.modules.account.CurrentAccount;
import com.kevinntech.modules.account.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }

        return "redirect:/board/list";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

}

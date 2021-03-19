package com.kevinntech.modules.account;

import com.kevinntech.infra.mail.EmailMessage;
import com.kevinntech.infra.mail.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    // Mocking를 함
    @MockBean EmailService emailService;

    @DisplayName("인증 메일 확인 - 입력 값 오류")
    @Test
    void checkEmailToken_with_wrong_input() throws Exception {
        mockMvc.perform(get("/check-email-token")
                    .param("token", "asdasdd")
                    .param("email", "email@email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated()); // 로그인 테스트 코드 (로그인 X)

        // 스프링 시큐리티 의존성이 추가 되어 있다면 스프링 부트의 자동 설정으로 인해 MockMvc에 스프링 시큐리티를 지원하는 기능이 추가된다.
    }

    @DisplayName("인증 메일 확인 - 입력 값 정상")
    @Test
    void checkEmailToken() throws Exception {
        Account account = Account.builder()
                .email("test@email.com")
                .password("12345678")
                .nickname("kevin")
                .build();
        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken(); // 이메일 토큰 만들기

        mockMvc.perform(get("/check-email-token")
                .param("token", newAccount.getEmailCheckToken())
                .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated()); // 로그인 테스트 코드 (로그인 O)
    }

    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원 가입 처리 - 입력 값 오류")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                    .param("nickname", "kevin")
                    .param("email", "email..")
                    .param("password", "12345")
                    .with(csrf()))  // .with(csrf()) : csrf 토큰 생성. 폼 Submit 테스트코드 작성시 필요
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원 가입 처리 - 입력 값 정상")
    @Test
    void signUpSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "kevin")
                .param("email", "kevinntech@nate.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("kevin"));

        Account account = accountRepository.findByEmail("kevinntech@nate.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "12345678"); // 패스워드가 인코딩 되었는지 확인
        assertNotNull(account.getEmailCheckToken()); // 토큰이 null인지 확인

        // 어떤 SimpleMailMessage 타입이든 send()로 호출 되었는지 확인한다.
        then(emailService).should().sendEmail(any(EmailMessage.class));
    }

}
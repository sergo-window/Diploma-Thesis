package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.model.Role;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private final String USER_PASSWORD = "password123";
    private final String ADMIN_PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        UserEntity user = new UserEntity();
        user.setUsername("1user@gmail.com");
        user.setPassword(passwordEncoder.encode(USER_PASSWORD));
        user.setFirstName("Иван");
        user.setLastName("Иванов");
        user.setPhone("+7 (911) 123-45-67");
        user.setRole(Role.USER);
        user.setEnabled(true);
        userRepository.save(user);

        UserEntity admin = new UserEntity();
        admin.setUsername("1admin@gmail.com");
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setFirstName("Админ");
        admin.setLastName("Админов");
        admin.setPhone("+7 (912) 987-65-43");
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);
        userRepository.save(admin);
    }

    @Test
    void shouldReturnUserInfoWithHttpBasic() throws Exception {
        mockMvc.perform(get("/users/me")
                        .with(httpBasic("1user@gmail.com", USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("1user@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Иван"))
                .andExpect(jsonPath("$.lastName").value("Иванов"));
    }

    @Test
    void shouldUpdatePasswordWithHttpBasic() throws Exception {
        NewPassword newPassword = new NewPassword();
        newPassword.setCurrentPassword(USER_PASSWORD);
        newPassword.setNewPassword("newpassword123");

        mockMvc.perform(post("/users/set_password")
                        .with(httpBasic("1user@gmail.com", USER_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPassword)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedWhenCurrentPasswordIsWrong() throws Exception {
        NewPassword newPassword = new NewPassword();
        newPassword.setCurrentPassword("wrongpassword");
        newPassword.setNewPassword("newpassword123");

        mockMvc.perform(post("/users/set_password")
                        .with(httpBasic("1user@gmail.com", USER_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPassword)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequestWhenNewPasswordIsTooShort() throws Exception {
        NewPassword newPassword = new NewPassword();
        newPassword.setCurrentPassword(USER_PASSWORD);
        newPassword.setNewPassword("123");

        mockMvc.perform(post("/users/set_password")
                        .with(httpBasic("1user@gmail.com", USER_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPassword)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAuthenticateWithValidCredentials() throws Exception {
        mockMvc.perform(get("/users/me")
                        .with(httpBasic("1user@gmail.com", USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedWithInvalidCredentials() throws Exception {
        mockMvc.perform(get("/users/me")
                        .with(httpBasic("1user@gmail.com", "wrongpassword")))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUserInfoForAdmin() throws Exception {
        mockMvc.perform(get("/users/me")
                        .with(httpBasic("1admin@gmail.com", ADMIN_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("1admin@gmail.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void shouldUpdateUserInfo() throws Exception {
        String updateJson = """
                {
                    "firstName": "Петр",
                    "lastName": "Петров",
                    "phone": "+7 (999) 888-77-66"
                }
                """;

        mockMvc.perform(patch("/users/me")
                        .with(httpBasic("1user@gmail.com", USER_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Петр"))
                .andExpect(jsonPath("$.lastName").value("Петров"))
                .andExpect(jsonPath("$.phone").value("+7 (999) 888-77-66"));
    }
}
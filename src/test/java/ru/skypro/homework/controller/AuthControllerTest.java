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
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.model.Role;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        userRepository.deleteAll();

        UserEntity existingUser = new UserEntity();
        existingUser.setUsername("existing@test.com");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");
        existingUser.setPhone("+7 (911) 111-11-11");
        existingUser.setRole(Role.USER);
        existingUser.setEnabled(true);
        userRepository.save(existingUser);
    }

    @Test
    void shouldRegisterNewUser() throws Exception {
        Register register = new Register();
        register.setUsername("newuser@test.com");
        register.setPassword("password123");
        register.setFirstName("Новый");
        register.setLastName("Пользователь");
        register.setPhone("+7 (911) 111-11-11");
        register.setRole(Role.USER);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequestWhenRegisterWithExistingUsername() throws Exception {

        UserEntity existingUser = new UserEntity();
        existingUser.setUsername("duplicate@test.com");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");
        existingUser.setPhone("+7 (911) 111-11-11");
        existingUser.setRole(Role.USER);
        existingUser.setEnabled(true);
        userRepository.save(existingUser);

        Register register = new Register();
        register.setUsername("duplicate@test.com");
        register.setPassword("password123");
        register.setFirstName("Another");
        register.setLastName("User");
        register.setPhone("+7 (922) 222-22-22");
        register.setRole(Role.USER);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginWithValidCredentials() throws Exception {

        UserEntity user = new UserEntity();
        user.setUsername("login@test.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName("Login");
        user.setLastName("User");
        user.setPhone("+7 (933) 333-33-33");
        user.setRole(Role.USER);
        user.setEnabled(true);
        userRepository.save(user);

        String requestBody = """
                {
                    "username": "login@test.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedWithInvalidCredentials() throws Exception {

        UserEntity user = new UserEntity();
        user.setUsername("unauthorized@test.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName("Unauthorized");
        user.setLastName("User");
        user.setPhone("+7 (944) 444-44-44");
        user.setRole(Role.USER);
        user.setEnabled(true);
        userRepository.save(user);

        String requestBody = """
                {
                    "username": "unauthorized@test.com",
                    "password": "wrongpassword"
                }
                """;

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequestWhenRegisterWithInvalidData() throws Exception {

        Register register = new Register();
        register.setUsername("invalid@test.com");
        register.setPassword("123");
        register.setFirstName("Invalid");
        register.setLastName("User");
        register.setPhone("invalid-phone");
        register.setRole(Role.USER);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}

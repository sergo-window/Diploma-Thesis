package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ImageService imageService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name()))
        );
    }

    public Optional<UserEntity> getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .map(this::toUserDto)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private User toUserDto(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setEmail(entity.getUsername());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setPhone(entity.getPhone());
        user.setRole(entity.getRole());
        user.setImage("/images/" + entity.getImagePath());
        return user;
    }

    @Transactional
    public boolean updatePassword(String username, NewPassword newPassword) {
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }

        UserEntity user = userOpt.get();

        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), user.getPassword())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    @Transactional
    public Optional<User> updateUser(String username, UpdateUser updateUser) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    if (updateUser.getFirstName() != null) {
                        user.setFirstName(updateUser.getFirstName());
                    }
                    if (updateUser.getLastName() != null) {
                        user.setLastName(updateUser.getLastName());
                    }
                    if (updateUser.getPhone() != null) {
                        user.setPhone(updateUser.getPhone());
                    }
                    UserEntity saved = userRepository.save(user);
                    return toUserDto(saved);
                });
    }

    @Transactional
    public Optional<User> updateUserImage(String username, String imageUrl) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setImage(imageUrl);
                    UserEntity saved = userRepository.save(user);
                    return toUserDto(saved);
                });
    }

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public User createUser(ru.skypro.homework.dto.Register register) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(register.getUsername());
        userEntity.setPassword(passwordEncoder.encode(register.getPassword()));
        userEntity.setFirstName(register.getFirstName());
        userEntity.setLastName(register.getLastName());
        userEntity.setPhone(register.getPhone());
        userEntity.setRole(register.getRole());
        userEntity.setEnabled(true);

        UserEntity saved = userRepository.save(userEntity);
        return toUserDto(saved);
    }

    @Transactional
    public User updateUserImage(String username, MultipartFile image) throws IOException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userEntity.getImagePath() != null) {
            try {
                imageService.deleteImage(userEntity.getImagePath());
            } catch (IOException e) {

                System.err.println("Failed to delete old image: " + e.getMessage());
            }
        }

        String newImageFilename = imageService.saveImage(image);
        userEntity.setImagePath(newImageFilename);

        UserEntity savedEntity = userRepository.save(userEntity);
        return userMapper.toUserDto(savedEntity);
    }

    public User getUser(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User user = userMapper.toUserDto(userEntity);

        if (userEntity.getImagePath() != null) {
            String imageUrl = imageService.getImageUrl(userEntity.getImagePath());
        }

        return user;
    }
}

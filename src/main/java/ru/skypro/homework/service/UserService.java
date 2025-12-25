package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) userRepository.findActiveByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::toUserDto);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toUserDto);
    }

    public User getCurrentUser(UserDetails userDetails) {
        UserEntity userEntity = (UserEntity) userDetails;
        return userMapper.toUserDto(userEntity);
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
                    userMapper.updateEntityFromDto(updateUser, user);
                    UserEntity saved = userRepository.save(user);
                    return userMapper.toUserDto(saved);
                });
    }

    @Transactional
    public Optional<User> updateUserImage(String username, String imageUrl) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setImage(imageUrl);
                    UserEntity saved = userRepository.save(user);
                    return userMapper.toUserDto(saved);
                });
    }

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public User createUser(UserEntity userEntity) {
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        UserEntity saved = userRepository.save(userEntity);
        return userMapper.toUserDto(saved);
    }
}

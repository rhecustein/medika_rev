package com.medika.spk.service;

import com.medika.spk.dto.request.UserRequest;
import com.medika.spk.entity.User;
import com.medika.spk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public User findById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
    }

    public User create(UserRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username sudah digunakan");
        }
        User user = User.builder()
                .nama(req.getNama())
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .role(User.Role.valueOf(req.getRole()))
                .status(req.getStatus() != null
                        ? User.StatusUser.valueOf(req.getStatus())
                        : User.StatusUser.AKTIF)
                .build();
        return userRepo.save(user);
    }

    public User update(Long id, UserRequest req) {
        User user = findById(id);
        user.setNama(req.getNama());
        user.setEmail(req.getEmail());
        user.setRole(User.Role.valueOf(req.getRole()));
        if (req.getStatus() != null) {
            user.setStatus(User.StatusUser.valueOf(req.getStatus()));
        }
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        return userRepo.save(user);
    }

    public void delete(Long id) {
        userRepo.deleteById(id);
    }
}

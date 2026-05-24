package com.medika.spk.service;

import com.medika.spk.dto.request.LoginRequest;
import com.medika.spk.dto.response.JwtResponse;
import com.medika.spk.entity.User;
import com.medika.spk.repository.UserRepository;
import com.medika.spk.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;

    public JwtResponse login(LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return JwtResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .nama(user.getNama())
                .role(user.getRole().name())
                .build();
    }
}

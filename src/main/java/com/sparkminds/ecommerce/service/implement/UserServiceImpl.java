package com.sparkminds.ecommerce.service.implement;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sparkminds.ecommerce.entity.User;
import com.sparkminds.ecommerce.repository.UserRepository;
import com.sparkminds.ecommerce.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(
                () -> new AccessDeniedException("Access denied: You do not have permission"));
    }
}

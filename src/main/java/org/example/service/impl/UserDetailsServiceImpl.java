package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.repository.UserRepository;
import org.example.repository.entity.UserEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        return User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPasswordHash())
                .disabled(!userEntity.getEnabled())
                .authorities(
                        userEntity.getRoles().stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toSet())
                )
                .build();
    }

}

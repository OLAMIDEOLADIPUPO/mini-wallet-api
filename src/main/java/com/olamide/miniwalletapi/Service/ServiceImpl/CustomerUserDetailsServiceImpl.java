package com.olamide.miniwalletapi.Service.ServiceImpl;

import com.olamide.miniwalletapi.Repository.UserRepository;
import com.olamide.miniwalletapi.Service.CustomerUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsServiceImpl implements CustomerUserDetailsService {
    private final UserRepository userRepository;

    public CustomerUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User with email "+email+ "not found"));
    }
}

package br.com.cardmanager.service;

import br.com.cardmanager.model.dto.RegisterDTO;
import br.com.cardmanager.model.entity.User;
import br.com.cardmanager.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean register(RegisterDTO register) {
        if(Objects.nonNull(this.userRepository.findByLogin(register.login()))) return false;

        String encryptedPassword = passwordEncoder.encode(register.password());
        User newUser = User.builder().login(register.login()).password(encryptedPassword).build();

        this.userRepository.save(newUser);

        return true;
    }
}

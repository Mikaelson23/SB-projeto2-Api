package com.SpringBoot.repository;
import com.SpringBoot.domain.UsuarioP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<UsuarioP, Long> {
    UsuarioP findByUserName(String userName);
}

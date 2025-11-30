package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.model.Usuario;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

/**
 * Repository consolidado para User que herda diretamente de JpaRepository.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    public Optional<Usuario> findByEmail(String email);

	@Query("SELECT u.isAdmin FROM Usuario u WHERE u.email = :email")
	 public boolean getIsAdminByEmail(@Param("email") String email);
}
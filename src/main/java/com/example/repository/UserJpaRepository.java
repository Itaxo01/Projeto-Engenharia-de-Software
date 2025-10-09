package com.example.repository;

import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/* O JpaRepository já provém a maioria das funções a depender dos atributos da tabela, por reflexão. Poderia ser setada diretamente no model do User, mas preferi optar por um menor aclopamento.*/
@Repository
interface UserJpaRepository extends JpaRepository<User, String> {
    boolean existsByMatricula(String matricula);
}
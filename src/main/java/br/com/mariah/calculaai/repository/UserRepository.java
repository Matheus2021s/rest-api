package br.com.mariah.calculaai.repository;

import br.com.mariah.calculaai.domain.UserEmbeddedId;
import br.com.mariah.calculaai.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, UserEmbeddedId> { 

}
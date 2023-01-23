package br.com.mariah.calculaai.service;

import br.com.mariah.calculaai.domain.UserEntity;
import br.com.mariah.calculaai.exception.ResourceNotFoundException;
import br.com.mariah.calculaai.repository.UserRepository;
import java.lang.String;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService { 

	private final UserRepository repository; 

	public Page<UserEntity> list(
		Pageable pageable
	) {
		return this.repository.findAll(pageable); 
	}
	
	public UserEntity findById(
		String id
	) {
		return this.repository.findById(id) 
		.orElseThrow(()-> new ResourceNotFoundException(" User não encontrado!")); 
	}
	
	public UserEntity create(
		UserEntity entity
	) {
		return this.repository.save(entity); 
	}
	
	public UserEntity Update(
		UserEntity entity,
		String id
	) {
		UserEntity entityFound = findById(id); 
		entityFound.setLogin(entity.getLogin()); 
		entityFound.setPassword(entity.getPassword()); 
		entityFound.setEmail(entity.getEmail()); 
		return entityFound; 
	}
	
	public void delete(
		String id
	) {
		this.repository.delete(findById(id)); 
	} 

}
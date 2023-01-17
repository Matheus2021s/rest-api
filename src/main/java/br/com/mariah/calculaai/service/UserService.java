package br.com.mariah.calculaai.service;

import br.com.mariah.calculaai.domain.UserEmbeddedId;
import br.com.mariah.calculaai.domain.UserEntity;
import br.com.mariah.calculaai.exception.ResourceNotFoundException;
import br.com.mariah.calculaai.repository.UserRepository;
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
		UserEmbeddedId id
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
		UserEmbeddedId id
	) {
		UserEntity entityFound = findById(id); 
		entityFound.getId().setLogin(entity.getId().getLogin()); 
		entityFound.getId().setEmail(entity.getId().getEmail()); 
		entityFound.setPassword(entity.getPassword()); 
		return entityFound; 
	}
	
	public void delete(
		UserEmbeddedId id
	) {
		this.repository.delete(findById(id)); 
	} 

}
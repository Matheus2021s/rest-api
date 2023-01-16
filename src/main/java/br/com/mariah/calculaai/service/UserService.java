package br.com.mariah.calculaai.service;

import br.com.mariah.calculaai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService { 

	private final UserRepository repository; 

}
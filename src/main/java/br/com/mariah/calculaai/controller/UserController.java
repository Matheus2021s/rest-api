package br.com.mariah.calculaai.controller;

import br.com.mariah.calculaai.domain.UserEntity;
import br.com.mariah.calculaai.dto.create.UserCreateDTO;
import br.com.mariah.calculaai.dto.response.UserResponseDTO;
import br.com.mariah.calculaai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity list(Pageable pageable) {
        return ResponseEntity.ok(this.userService.list(pageable));
    }

    @GetMapping(name = "{login}")
    public ResponseEntity getById(@PathVariable String login) {
        return ResponseEntity.ok(UserResponseDTO.of(this.userService.findById(login)));
    }

    @PostMapping
    public ResponseEntity create(@RequestBody UserCreateDTO userCreateDTO) {
        UserEntity entity = this.userService.create(userCreateDTO.toEntity());
        return ResponseEntity.created(URI.create("/user")).body(UserResponseDTO.of(entity));
    }

}
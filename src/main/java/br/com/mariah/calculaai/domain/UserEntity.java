package br.com.mariah.calculaai.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.lang.String;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table (
	name = "USER" 
)
public class UserEntity implements Serializable { 

	@Id
	@GeneratedValue (
		strategy = GenerationType.IDENTITY 
	)
	@Column (
		name = "LOGIN" 
	)
	private String login;
	@Column (
		name = "PASSWORD" 
	)
	private String password;
	@Column (
		name = "EMAIL" 
	)
	private String email; 

}
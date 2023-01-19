package br.com.mariah.calculaai.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.lang.String;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEmbeddedId implements Serializable { 

	@Column (
		name = "LOGIN" 
	)
	private String login;
	@Column (
		name = "EMAIL" 
	)
	private String email; 

}
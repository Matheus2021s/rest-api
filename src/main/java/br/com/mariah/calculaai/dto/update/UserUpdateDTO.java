package br.com.mariah.calculaai.dto.update;

import br.com.mariah.calculaai.domain.UserEmbeddedId;
import br.com.mariah.calculaai.domain.UserEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.String;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude (
	value = JsonInclude.Include.NON_ABSENT 
)
public class UserUpdateDTO { 

	@JsonProperty (
		value = "LOGIN" 
	)
	private String login;
	@JsonProperty (
		value = "PASSWORD" 
	)
	private String password;
	@JsonProperty (
		value = "EMAIL" 
	)
	private String email; 

	public UserEntity toEntity() {
		return UserEntity.builder() 
			.id( 
				UserEmbeddedId.builder() 
				.login(this.login) 
				.email(this.email) 
			.build() 
			) 
			.password(this.password) 
			.build(); 
	} 

}
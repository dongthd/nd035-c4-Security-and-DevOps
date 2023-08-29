package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class CreateUserRequest {

	@JsonProperty
	@NotEmpty(message = "Username must not be null or blank")
	private String username;

	@JsonProperty
	@NotEmpty(message = "Password must not be null or blank")
	private String password;

	@JsonProperty
	@NotEmpty(message = "Confirm password must not be null or blank")
	private String confirmPassword;
}

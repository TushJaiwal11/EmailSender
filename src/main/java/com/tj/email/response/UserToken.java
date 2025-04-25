package com.tj.email.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserToken {

	private String jwt;
	private String message;
	private LocalDateTime createAt;
	private LocalDateTime expiresAt;
	private Boolean success;

}

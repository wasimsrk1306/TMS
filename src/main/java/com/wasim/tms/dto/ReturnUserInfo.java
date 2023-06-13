package com.wasim.tms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnUserInfo {
	
	private int id;
	private String name;
	private String role;
	private String email;
}

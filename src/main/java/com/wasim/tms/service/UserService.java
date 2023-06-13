package com.wasim.tms.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wasim.tms.dto.ReturnUserInfo;
import com.wasim.tms.entity.UserInfo;
import com.wasim.tms.repository.UserInfoRepository;



@Service
public class UserService {
	
	@Autowired
	private UserInfoRepository userInfoRepository; 
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	 
	@Async("AsyncExecution")
	public CompletableFuture<Boolean> isUserUnique(UserInfo user) {
		
		try {
			
			UserInfo userByName = userInfoRepository.findByName(user.getName()).get();
			UserInfo userByEmail = userInfoRepository.findByName(user.getEmail()).get();
		
		if(userByName == null && userByEmail == null) {
			
			throw new NoSuchElementException("");
		}
		} catch (NoSuchElementException ex) {
			
			return CompletableFuture.completedFuture(true); 
		}
		
		return CompletableFuture.completedFuture(false); 
	}
	
	
	
	@Async("AsyncExecution")
	public CompletableFuture<UserInfo> getLoggedInUser(String name) {
		
		return CompletableFuture.completedFuture(userInfoRepository.findByName(name).get());
	}


	
	@Async("AsyncExecution")
	public CompletableFuture<List<ReturnUserInfo>> getAllUsers() {
		
		return CompletableFuture.completedFuture(userInfoRepository.findAllBySpecificFileds()); 
	}
	
	
	@Async("AsyncExecution")
	public CompletableFuture<UserInfo> getUserById(int userId) {
		
		UserInfo user = null;
		
		try { 
			
			user = userInfoRepository.findById(userId).get(); 
			
		} catch (NoSuchElementException ex){ 
			return null; 
		}
		
		return CompletableFuture.completedFuture(user); 
	}
	
	
	
	@Async("AsyncExecution")
	public CompletableFuture<ResponseEntity<String>> addUser(UserInfo userInfo) {
		 
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        userInfo.setRole("ROLE_USER"); 
		userInfoRepository.save(userInfo);
        return CompletableFuture.completedFuture(ResponseEntity.ok("New user added to system!"));
	}

}

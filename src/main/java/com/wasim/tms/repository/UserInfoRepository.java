package com.wasim.tms.repository;

import com.wasim.tms.dto.ReturnUserInfo;
import com.wasim.tms.entity.UserInfo;

import jakarta.persistence.OrderBy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    Optional<UserInfo> findByName(String username);
    
    Optional<UserInfo> findByEmail(String email);
    
    @OrderBy("id ASC") 
    @Query("SELECT new com.wasim.tms.dto.ReturnUserInfo(e.id, e.name, e.role, e.email) FROM UserInfo e")
    List<ReturnUserInfo> findAllBySpecificFileds();
}
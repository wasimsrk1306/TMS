package com.wasim.tms.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String email;
    
    @NotNull
    private String password; 
    
    private String role; 
    
    @OneToMany(mappedBy = "user_id", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true) 
    private List<Ticket> tList = new ArrayList<>();
}
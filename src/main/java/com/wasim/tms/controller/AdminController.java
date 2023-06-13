package com.wasim.tms.controller;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wasim.tms.dto.AuthRequest;
import com.wasim.tms.dto.ReturnTicketInfo;
import com.wasim.tms.dto.ReturnUserInfo;
import com.wasim.tms.entity.Ticket;
import com.wasim.tms.entity.TicketStatus;
import com.wasim.tms.entity.UserInfo;
import com.wasim.tms.repository.TicketRepository;
import com.wasim.tms.repository.UserInfoRepository;
import com.wasim.tms.service.JwtService;
import com.wasim.tms.service.TicketService;
import com.wasim.tms.service.UserService;

import jakarta.validation.Valid;



@RestController
@RequestMapping("tms/admin")
public class AdminController {
	
	 @Autowired
	  private UserService uService;
	 
	 @Autowired
	  private TicketService tService; 
	    
	 @Autowired
	  private JwtService jwtService;
	 
	 @Autowired
	 private TicketRepository ticketRepository;
	 
	 @Autowired
	 private UserInfoRepository userInfoRepository;

	 @Autowired
	 private AuthenticationManager authenticationManager;

	 
	 
	@PostMapping("/new")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<String> addNewUser(@Valid @RequestBody UserInfo user, BindingResult bindingResult) throws InterruptedException, ExecutionException {
		 
		if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation failed");
        } 
		
		if(!uService.isUserUnique(user).get()) { 
			return ResponseEntity.badRequest().body("Username or Email already registered..");
		}
		
		return uService.addUser(user).get(); 
	}
	
	
	
	@GetMapping("/all-users")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public List<ReturnUserInfo> getUsers() throws InterruptedException, ExecutionException{
		
		return uService.getAllUsers().get();
	}
	
	
	
	@GetMapping("/all-tickets")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public List<ReturnTicketInfo> getTickets() throws InterruptedException, ExecutionException{
		
		return tService.getAllTickets().get(); 
	}
	
	
	
	@GetMapping("/get-ticket/{ticketId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ReturnTicketInfo getTicket(@PathVariable int ticketId) throws InterruptedException, ExecutionException{
		
		return tService.getTicketByIdToDisplay(ticketId).get();  
	}
	
	
	
	@GetMapping("/all-tickets/hold-ticket/{ticketId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<String> holdTicket(@PathVariable int ticketId) throws InterruptedException, ExecutionException{
		
		Ticket ticket = tService.getTicketById(ticketId).get();
		
		if (ticket == null) {
			
			return ResponseEntity.notFound().build(); 
		} else if (ticket.getStatus().equals(TicketStatus.HOLD)) {
			return ResponseEntity.badRequest().body("Ticket " +  ticketId + " is already on hold..");
		} else if (!tService.checkForProgressAndCompleteness(ticket)) {
			return ResponseEntity.badRequest().body("Cannot put ticket " +  ticketId + " on hold..");
		}
		
		ticket.setStatus(TicketStatus.HOLD);
		ticketRepository.save(ticket);
		
		return ResponseEntity.ok("Ticket " + ticketId + " is put on Hold.");
	}
	
	
	
	@GetMapping("/all-tickets/approve/{ticketId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<String> approveTicket(@PathVariable int ticketId) throws InterruptedException, ExecutionException{
		
		Ticket ticket = tService.getTicketById(ticketId).get();
		
		if (ticket == null) { 
			
			return ResponseEntity.notFound().build(); 
		} else if (ticket.getStatus().equals(TicketStatus.APPROVED)) {
			return ResponseEntity.badRequest().body("Ticket " +  ticketId + " is already on approved..");
		} else if (!tService.checkForProgressAndCompleteness(ticket)) {
			return ResponseEntity.badRequest().body("Ticket " +  ticketId + " is already processed..");
		}
		
		ticket.setStatus(TicketStatus.APPROVED); 
		ticketRepository.save(ticket);
		
		return ResponseEntity.ok("Ticket " + ticketId + " is now approved.");
	}
	
	
	
	@GetMapping("/all-tickets/put-in-progress/{ticketId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<String> putInProgressTickets(@PathVariable int ticketId) throws InterruptedException, ExecutionException{
		
		Ticket ticket = tService.getTicketById(ticketId).get();
		
		if (ticket == null) {
			
			return ResponseEntity.notFound().build(); 
		} else if (!tService.checkForProgressAndCompleteness(ticket)) { 
			return ResponseEntity.badRequest().body("Cannot put ticket " +  ticketId + " on hold..");
		}
		
		ticket.setStatus(TicketStatus.IN_PROGRESS);
		ticketRepository.save(ticket);
		
		return ResponseEntity.ok("Ticket " + ticketId + " is put in progress.");
	}
	
	
	
	@GetMapping("/all-tickets/complete/{ticketId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<String> completeTicket(@PathVariable int ticketId) throws InterruptedException, ExecutionException{
		
		Ticket ticket = tService.getTicketById(ticketId).get();
		
		if (ticket == null) {
			
			return ResponseEntity.notFound().build();  
		} else if (ticket.getStatus().equals(TicketStatus.COMPLETED)) { 
			return ResponseEntity.badRequest().body("Ticket " +  ticketId + " is already completed..");
		}
		
		ticket.setStatus(TicketStatus.COMPLETED);
		ticketRepository.save(ticket);
		
		return ResponseEntity.ok("Ticket " + ticketId + " is completed.");
	}
	
	
	
	@GetMapping("/make-admin/{userId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<String> makeAdmin(@PathVariable int userId) throws InterruptedException, ExecutionException{
		
		UserInfo user = uService.getUserById(userId).get();
		
		if(user == null) {
			
			return ResponseEntity.notFound().build();
		} else if (user.getRole().equals("ROLE_ADMIN")) {
			
			return ResponseEntity.badRequest().body("User " + userId + " is already an Admin"); 
		}
		
		user.setRole("ROLE_ADMIN");
		userInfoRepository.save(user);
		
		return ResponseEntity.ok("User " + userId + " is now an Admin"); 
	}
	
	
	
	
	@GetMapping("/remove-admin/{userId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<String> removeAdmin(@PathVariable int userId, Principal principal) throws InterruptedException, ExecutionException{
		
		UserInfo user = uService.getUserById(userId).get();
		
		if(user == null) {
			
			return ResponseEntity.notFound().build();
		} else if (user.getName().equals(principal.getName())) {
			
			return ResponseEntity.badRequest().body("Cannot remove themselves as admin");
		} else if (user.getRole().equals("ROLE_USER")) {
			
			return ResponseEntity.badRequest().body("User " + userId + " is already an user"); 
		}
		
		user.setRole("ROLE_USER");
		userInfoRepository.save(user);
		
		return ResponseEntity.ok("User " + userId + " is no longer an Admin"); 
	}
	
	
	
	@PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
		
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        
        if (authentication.isAuthenticated()) {
        	
            return jwtService.generateToken(authRequest.getUsername());
        } else {
        	
            throw new UsernameNotFoundException("invalid user request!");
        }
    }
}

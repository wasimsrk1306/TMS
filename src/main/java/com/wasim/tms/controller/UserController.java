package com.wasim.tms.controller;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wasim.tms.dto.UserTicket;
import com.wasim.tms.entity.Ticket;
import com.wasim.tms.entity.TicketStatus;
import com.wasim.tms.entity.UserInfo;
import com.wasim.tms.repository.TicketRepository;
import com.wasim.tms.repository.UserInfoRepository;
import com.wasim.tms.service.TicketService;
import com.wasim.tms.service.UserService;

import jakarta.validation.Valid;



@RestController 
@RequestMapping("tms/user")
public class UserController {
	
	 @Autowired
	 private UserService uService;
	 
	 @Autowired
	 private TicketService tService; 
	 
	 @Autowired
	 private UserInfoRepository userInfoRepository;
	 
	 @Autowired 
	 private TicketRepository ticketRepository;
	 
	
	@PostMapping("/raise-ticket")
	@PreAuthorize("hasAuthority('ROLE_USER')") 
	public ResponseEntity<String> raiseTicket(@Valid @RequestBody Ticket ticket, BindingResult bindingResult, Principal principal) throws InterruptedException, ExecutionException {
		
		if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation failed"); 
        }
		
		UserInfo user = uService.getLoggedInUser(principal.getName()).get();
		
		ticket.setStatus(TicketStatus.PENDING); 
		ticket.setUser_id(user.getId());
		
		ticketRepository.save(ticket);
		
		user.getTList().add(ticket);
		
		userInfoRepository.save(user);
		
		return ResponseEntity.ok(principal.getName() + ", Your ticket has been raised successfully!");
	}
	
	
	
	@PostMapping("/update-ticket/{ticketId}")
	@PreAuthorize("hasAuthority('ROLE_USER')") 
	public ResponseEntity<String> updateTicket(@Valid @RequestBody Ticket t, @PathVariable int ticketId, BindingResult bindingResult, Principal principal) throws InterruptedException, ExecutionException {
		
		if (bindingResult.hasErrors()) {
			
            return ResponseEntity.badRequest().body("Validation failed");
        } 
		
		Ticket ticket = tService.getTicketById(ticketId).get();
		UserInfo user = uService.getLoggedInUser(principal.getName()).get();
		
		if (tService.checkForNullAndAuthority(ticket, user) == true) {
			return ResponseEntity.badRequest().body("Enter a valid ticket Id.."); 
        }
		
		ticket.setTitle(t.getTitle());
		ticket.setDescription(t.getDescription());
		
		ticketRepository.save(ticket);
		userInfoRepository.save(user); 
		
		return ResponseEntity.ok("Ticket " + ticketId + " updated!");
	}
	
	
	
	@GetMapping("/delete-ticket/{ticketId}")
	@PreAuthorize("hasAuthority('ROLE_USER')") 
	public ResponseEntity<String> deleteTicket(@PathVariable int ticketId, Principal principal) throws InterruptedException, ExecutionException {
		
		UserInfo user = uService.getLoggedInUser(principal.getName()).get();
		Ticket ticket = tService.getTicketById(ticketId).get();
		
		if (tService.checkForNullAndAuthority(ticket, user) == true) {
			return ResponseEntity.badRequest().body("Enter a valid ticket Id.."); 
        }
		 
		user.getTList().remove(ticket);
		userInfoRepository.save(user); 
		
		return ResponseEntity.ok("Ticket " + ticketId + " deleted successfully.."); 
	}
	
	
	
	@GetMapping("/my-tickets")
	@PreAuthorize("hasAuthority('ROLE_USER')") 
	public List<UserTicket> getTickets(Principal principal) throws InterruptedException, ExecutionException {
		
		UserInfo user = uService.getLoggedInUser(principal.getName()).get();
		List<Ticket> userTicketList = user.getTList();
		
		return tService.getUserTickets(userTicketList); 	
	}
	
	
	
	@GetMapping("/check-status/{ticketId}") 
	@PreAuthorize("hasAuthority('ROLE_USER')") 
	public ResponseEntity<String> checkStatus(@PathVariable int ticketId, Principal principal) throws InterruptedException, ExecutionException {
	
		UserInfo user = uService.getLoggedInUser(principal.getName()).get();
		Ticket ticket = tService.getTicketById(ticketId).get();
		
		if (tService.checkForNullAndAuthority(ticket, user) == true) {
			return ResponseEntity.badRequest().body("Enter a valid ticket Id.."); 
        }
		
		return ResponseEntity.ok("Ticket " + ticketId + " status: " + ticket.getStatus()); 
	}

}

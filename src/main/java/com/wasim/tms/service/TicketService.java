package com.wasim.tms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.wasim.tms.dto.ReturnTicketInfo;
import com.wasim.tms.dto.UserTicket;
import com.wasim.tms.entity.Ticket;
import com.wasim.tms.entity.TicketStatus;
import com.wasim.tms.entity.UserInfo;
import com.wasim.tms.repository.TicketRepository;



@Service
public class TicketService {
	
	@Autowired 
	private TicketRepository ticketRepository; 

	
	
	@Async("AsyncExecution")
	public CompletableFuture<List<ReturnTicketInfo>> getAllTickets() {
		
		return CompletableFuture.completedFuture(ticketRepository.findAllTicketsBySpecificFileds());
	}
	
	
	
	public List<UserTicket> getUserTickets(List<Ticket> userTicketList) {
		
		List<UserTicket> userTickets = new ArrayList<>();
		
		for (Ticket ticket : userTicketList) {
			
			userTickets.add(new UserTicket(ticket.getId(), ticket.getTitle(), ticket.getStatus()));
		}
		
		return userTickets; 
	}

	
	
	@Async("AsyncExecution")
	public CompletableFuture<Ticket> getTicketById(int ticketId) {
		
		Ticket ticket = null;
		
		try {
			
			ticket = ticketRepository.findById(ticketId).get(); 
			
		} catch (NoSuchElementException ex){ 
			return null; 
		}
		
		return CompletableFuture.completedFuture(ticket);
	}
 
	
	
	@Async("AsyncExecution")
	public CompletableFuture<ReturnTicketInfo> getTicketByIdToDisplay(int ticketId) {
		
		Ticket ticket = null;
		
		try {
			
			ticket = ticketRepository.findById(ticketId).get();  
			
		} catch (NoSuchElementException ex){
			return null;
		}
		
		return CompletableFuture.completedFuture(new ReturnTicketInfo(ticket.getId(), ticket.getUser_id(), ticket.getStatus()));
	}
	
	
	
	public boolean checkForProgressAndCompleteness(Ticket ticket) { 
		
		return (ticket.getStatus() != TicketStatus.IN_PROGRESS && ticket.getStatus() != TicketStatus.COMPLETED);
	}



	public boolean checkForNullAndAuthority(Ticket ticket, UserInfo user) {
		
		return (ticket == null || user.getTList().contains(ticket) == false); 
	}

}

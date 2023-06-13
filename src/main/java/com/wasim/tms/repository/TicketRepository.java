package com.wasim.tms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wasim.tms.dto.ReturnTicketInfo;
import com.wasim.tms.entity.Ticket;

import jakarta.persistence.OrderBy;

public interface TicketRepository  extends JpaRepository<Ticket, Integer> {
	
	Optional<Ticket> findById(int id);
	
	@OrderBy("id ASC") 
    @Query("SELECT new com.wasim.tms.dto.ReturnTicketInfo(e.id, e.user_id, e.status) FROM Ticket e")
    List<ReturnTicketInfo> findAllTicketsBySpecificFileds();  
}

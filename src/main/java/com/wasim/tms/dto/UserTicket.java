package com.wasim.tms.dto;

import com.wasim.tms.entity.TicketStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTicket {
	
	private int id;
	private String title;
	private TicketStatus status;
}

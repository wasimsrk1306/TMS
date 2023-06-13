package com.wasim.tms.dto;

import com.wasim.tms.entity.TicketStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnTicketInfo {

	private int id;
	private int user_id;
	private TicketStatus status; 
}

package com.unbcs.ecom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {

	@Id
	private String id;

	private String description;

	private String email;

	private String name;
	
	private String date;

}

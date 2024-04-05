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

public class Product {

	@Id
	private String id;

	private String name;

	private String category;

	private Float price;

	private String description;

	private String imageUrl;

}

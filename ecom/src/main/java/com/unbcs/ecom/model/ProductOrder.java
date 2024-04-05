package com.unbcs.ecom.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductOrder {

	@Id
	private String id;

	private String userId;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_order_products", joinColumns = @JoinColumn(name = "product_order_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
	private List<Product> products;

	private Float cartTotal;

}

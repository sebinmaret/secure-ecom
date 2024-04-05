package com.unbcs.ecom.misc;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unbcs.ecom.dto.OrderDTO;
import com.unbcs.ecom.dto.ProductDTO;
import com.unbcs.ecom.model.Product;
import com.unbcs.ecom.model.ProductOrder;

@Service
public class OrderConverter {

	@Autowired
	ProductConverter converter;

	public ProductOrder toOrder(OrderDTO orderDTO) {
		ProductOrder order = new ProductOrder();
		order.setCartTotal(orderDTO.getCartTotal());
		order.setProducts(new ArrayList<Product>());

		for (ProductDTO p : orderDTO.getProducts()) {
			order.getProducts().add(converter.toProduct(p));
		}
		order.setUserId(orderDTO.getUserId());
		order.setId(orderDTO.getId());
		return order;
	}

	public OrderDTO toOrderDTO(ProductOrder order) {
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setCartTotal(order.getCartTotal());
		orderDTO.setProducts(new ArrayList<ProductDTO>());
		for (Product p : order.getProducts()) {
			orderDTO.getProducts().add(converter.toProductDTO(p));
		}
		orderDTO.setUserId(order.getUserId());
		orderDTO.setId(order.getId());
		return orderDTO;
	}
}

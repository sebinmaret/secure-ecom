package com.unbcs.ecom.misc;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unbcs.ecom.dto.ProductDTO;
import com.unbcs.ecom.dto.UserCartDTO;
import com.unbcs.ecom.model.Product;
import com.unbcs.ecom.model.UserCart;

@Service
public class CartConverter {

	@Autowired
	ProductConverter converter;

	public UserCart toUserCart(UserCartDTO cartDTO) {
		UserCart userCart = new UserCart();
		userCart.setCartTotal(cartDTO.getCartTotal());
		userCart.setProducts(new ArrayList<Product>());

		for (ProductDTO p : cartDTO.getProducts()) {
			userCart.getProducts().add(converter.toProduct(p));
		}
		userCart.setUserId(cartDTO.getUserId());
		return userCart;
	}

	public UserCartDTO toUserCartDTO(UserCart userCart) {
		UserCartDTO userCartDTO = new UserCartDTO();
		userCartDTO.setCartTotal(userCart.getCartTotal());
		userCartDTO.setProducts(new ArrayList<ProductDTO>());
		for (Product p : userCart.getProducts()) {
			userCartDTO.getProducts().add(converter.toProductDTO(p));
		}
		userCartDTO.setUserId(userCart.getUserId());
		return userCartDTO;
	}
}

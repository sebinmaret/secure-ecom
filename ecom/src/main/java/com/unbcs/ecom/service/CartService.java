package com.unbcs.ecom.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unbcs.ecom.dto.OperationResultStatus;
import com.unbcs.ecom.dto.OperationStatus;
import com.unbcs.ecom.dto.UserCartDTO;
import com.unbcs.ecom.misc.CartConverter;
import com.unbcs.ecom.model.Product;
import com.unbcs.ecom.model.UserCart;
import com.unbcs.ecom.repository.CartRepository;
import com.unbcs.ecom.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class CartService {

	@Autowired
	CartRepository cartRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CartConverter cartConverter;

	private Float calculateTotal(UserCart cart) {
		Float sum = 0.0f;
		for (Product p : cart.getProducts()) {
			sum = sum + p.getPrice();
		}
		return sum;
	}

	@Transactional
	public OperationStatus addItem(String productId, String userId) {
		Optional<UserCart> optCart = cartRepository.findByUserId(userId);
		UserCart cart = null;

		Optional<Product> optProduct = productRepository.findById(productId);
		if (optProduct.isEmpty()) {
			return new OperationStatus("productId not found", OperationResultStatus.FAILED, 404);
		}

		if (optCart.isEmpty()) {
			cart = new UserCart();
			cart.setUserId(userId);
			cart.setProducts(new ArrayList<Product>());
		} else {
			cart = optCart.get();
		}

		cart.getProducts();
		cart.getProducts().add(optProduct.get());
		cart.setCartTotal(calculateTotal(cart));
		cartRepository.save(cart);

		return new OperationStatus(OperationResultStatus.SUCCESS);

	}

	public OperationStatus removeItem(String productId, String userId) {
		Optional<UserCart> optCart = cartRepository.findByUserId(userId);
		UserCart cart = null;

		Optional<Product> optProduct = productRepository.findById(productId);
		if (optProduct.isEmpty()) {
			return new OperationStatus("productId not found", OperationResultStatus.FAILED, 404);
		}

		if (optCart.isEmpty()) {
			return new OperationStatus("Cart Empty", OperationResultStatus.FAILED, 404);
		} else {
			cart = optCart.get();
		}

		cart.getProducts().remove(optProduct.get());
		if (cart.getProducts().isEmpty()) {
			cartRepository.delete(cart);
		}
		cart.setCartTotal(calculateTotal(cart));
		cartRepository.save(cart);

		return new OperationStatus(OperationResultStatus.SUCCESS);

	}

	public UserCartDTO getCart(String userId) {
		Optional<UserCart> optCart = cartRepository.findByUserId(userId);
		UserCart cart = null;

		if (optCart.isEmpty()) {
			return cartConverter.toUserCartDTO(new UserCart(userId, new ArrayList<Product>(), 0.0f));
		} else {
			cart = optCart.get();
		}

		return cartConverter.toUserCartDTO(cart);

	}

	public OperationStatus clearCart(String userId) {
		Optional<UserCart> optCart = cartRepository.findByUserId(userId);
		UserCart cart = null;

		if (optCart.isEmpty()) {
			return new OperationStatus(OperationResultStatus.SUCCESS);
		} else {
			cart = optCart.get();
		}
		cartRepository.delete(cart);
		return new OperationStatus(OperationResultStatus.SUCCESS);

	}

}

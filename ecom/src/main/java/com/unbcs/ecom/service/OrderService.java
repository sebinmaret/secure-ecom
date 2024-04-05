package com.unbcs.ecom.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unbcs.ecom.dto.CheckoutDTO;
import com.unbcs.ecom.dto.OperationResultStatus;
import com.unbcs.ecom.dto.OperationStatus;
import com.unbcs.ecom.dto.OrderDTO;
import com.unbcs.ecom.dto.UserCartDTO;
import com.unbcs.ecom.misc.OrderConverter;
import com.unbcs.ecom.repository.OrderRepository;

import jakarta.validation.Valid;

@Service
public class OrderService {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	CartService cartService;

	@Autowired
	OrderConverter orderConverter;

	private Boolean isCheckoutValid(CheckoutDTO checkoutDTO) {
		List<String> acceptedCards = Arrays.asList("4032030658496287", "4032038161760784", "4032035373749753");
//		if (!acceptedCards.contains(checkoutDTO.getCardNumber())) {
//			return false;
//		}
		if (Integer.valueOf(checkoutDTO.getCardCVV()) > 999 && Integer.valueOf(checkoutDTO.getCardCVV()) < 100) {
			return false;
		}
		return true;

	}

	public OperationStatus createOrder(String userId, @Valid CheckoutDTO checkoutDTO) {

		if (!isCheckoutValid(checkoutDTO)) {
			return new OperationStatus("Given Card is invalid.", OperationResultStatus.FAILED, 400);
		}
		UserCartDTO cart = cartService.getCart(userId);
		if (cart == null || cart.getProducts().size() == 0) {
			return new OperationStatus("Cart is empty.", OperationResultStatus.FAILED, 400);
		}
		OrderDTO order = new OrderDTO();
		order.setId(UUID.randomUUID().toString());
		order.setUserId(userId);
		order.setCartTotal(cart.getCartTotal());
		order.setProducts(cart.getProducts());

		orderRepository.save(orderConverter.toOrder(order));
		cartService.clearCart(userId);
		return new OperationStatus("Order is created with Order ID: " + order.getId(), OperationResultStatus.SUCCESS,
				201);
	}

	public List<OrderDTO> getOrderList(String userId) {
		return orderRepository.findByUserId(userId).stream().map(p -> orderConverter.toOrderDTO(p)).toList();
	}

}

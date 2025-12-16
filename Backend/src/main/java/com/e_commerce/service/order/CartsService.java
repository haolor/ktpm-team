package com.e_commerce.service.order;

import com.e_commerce.entity.order.Carts;
import org.springframework.stereotype.Service;

@Service
public interface CartsService {
    Carts getCartsEntityById(Integer id);

    Carts createCarts();

    Carts getCartByAccountId(Integer accountId);
}

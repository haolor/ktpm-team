package com.e_commerce.unit.order;

import com.e_commerce.entity.account.Account;
import com.e_commerce.entity.order.Carts;
import com.e_commerce.exceptions.CustomException;
import com.e_commerce.mapper.order.CartsMapper;
import com.e_commerce.repository.order.CartsRepository;
import com.e_commerce.service.account.AccountService;
import com.e_commerce.service.order.impl.CartsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartsServiceImplTest {

    @Mock
    private CartsRepository cartsRepository;
    @Mock
    private CartsMapper cartsMapper;
    @Mock
    private AccountService accountService;

    @InjectMocks
    private CartsServiceImpl cartsService;

    private Account account;
    private Carts carts;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(5);

        carts = new Carts();
        carts.setId(50);
        carts.setAccount(account);
    }

    @Test
    void getCartsEntityById_found_returnsEntity() {
        when(cartsRepository.findById(50)).thenReturn(Optional.of(carts));

        Carts result = cartsService.getCartsEntityById(50);

        assertThat(result).isSameAs(carts);
        verify(cartsRepository).findById(50);
    }

    @Test
    void getCartsEntityById_notFound_throws() {
        when(cartsRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartsService.getCartsEntityById(1))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void createCarts_returnsExistingIfPresent() {
        when(accountService.getAccountAuth()).thenReturn(account);
        when(cartsRepository.findByAccountId(account.getId())).thenReturn(Optional.of(carts));

        Carts result = cartsService.createCarts();

        assertThat(result).isSameAs(carts);
        verify(cartsRepository, never()).save(any(Carts.class));
    }

    @Test
    void createCarts_createsWhenMissing() {
        when(accountService.getAccountAuth()).thenReturn(account);
        when(cartsRepository.findByAccountId(account.getId())).thenReturn(Optional.empty());
        when(cartsRepository.save(any(Carts.class))).thenAnswer(inv -> inv.getArgument(0));

        Carts result = cartsService.createCarts();

        assertThat(result.getAccount()).isEqualTo(account);
        verify(cartsRepository).save(any(Carts.class));
    }

    @Test
    void getCartByAccountId_found_returns() {
        when(cartsRepository.findByAccountId(account.getId())).thenReturn(Optional.of(carts));

        Carts result = cartsService.getCartByAccountId(account.getId());

        assertThat(result).isSameAs(carts);
        verify(cartsRepository).findByAccountId(account.getId());
    }

    @Test
    void getCartByAccountId_notFound_throws() {
        when(cartsRepository.findByAccountId(account.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartsService.getCartByAccountId(account.getId()))
                .isInstanceOf(CustomException.class);
    }
}

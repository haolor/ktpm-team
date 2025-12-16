package com.e_commerce.repository.order;

import com.e_commerce.entity.order.Orders;
import com.e_commerce.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

public interface OrdersRepository extends JpaRepository<Orders, Integer>, JpaSpecificationExecutor<Orders> {
    Optional<Orders> findTopByAccount_IdOrderByOrderTimeDesc(Integer accountId);

    List<Orders> findByRestaurantId(Integer restaurantId);

    boolean existsByRestaurantIdAndOrderStatusIn(Integer restaurantId, List<OrderStatus> statuses);

    Page<Orders> findByRestaurantId(Integer restaurantId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Orders o WHERE o.restaurant.id = :restaurantId AND o.orderStatus = :orderStatus")
    BigDecimal sumTotalPriceByRestaurantIdAndStatus(@Param("restaurantId") Integer restaurantId,
                                                    @Param("orderStatus") OrderStatus status);

            @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Orders o WHERE o.restaurant.id = :restaurantId "
                + "AND o.orderStatus = :orderStatus "
                + "AND o.orderTime BETWEEN :start AND :end")
            BigDecimal sumTotalPriceByRestaurantIdAndStatusAndOrderTimeBetween(
                @Param("restaurantId") Integer restaurantId,
                @Param("orderStatus") OrderStatus status,
                @Param("start") LocalDateTime start,
                @Param("end") LocalDateTime end
            );

            @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Orders o WHERE o.restaurant.id = :restaurantId "
                + "AND o.orderStatus = :orderStatus "
                + "AND o.orderTime >= :start")
            BigDecimal sumTotalPriceByRestaurantIdAndStatusAndOrderTimeAfter(
                @Param("restaurantId") Integer restaurantId,
                @Param("orderStatus") OrderStatus status,
                @Param("start") LocalDateTime start
            );

            @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Orders o WHERE o.restaurant.id = :restaurantId "
                + "AND o.orderStatus = :orderStatus "
                + "AND o.orderTime <= :end")
            BigDecimal sumTotalPriceByRestaurantIdAndStatusAndOrderTimeBefore(
                @Param("restaurantId") Integer restaurantId,
                @Param("orderStatus") OrderStatus status,
                @Param("end") LocalDateTime end
            );

}

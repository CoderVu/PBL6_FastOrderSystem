package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.entity.Order;
import com.example.BE_PBL6_FastOrderSystem.entity.OrderDetail;
import com.example.BE_PBL6_FastOrderSystem.entity.StatusOrder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByOrderCode(String orderCode);

    @Query("SELECT o FROM Order o WHERE o.orderCode = ?1")
    Optional<Order> findByOrderCode(String orderCode);

    @Query("SELECT o FROM Order o WHERE o.orderId = ?1")
    Optional<Order> findByOrderId(Long orderId);

    List<Order> findAllByStatusAndUserId(StatusOrder statusOrder, Long userId);

    @Query("SELECT o FROM Order o WHERE o.status = ?1")
    boolean findByStatusOrder(StatusOrder statusOrder);

    @Query("SELECT o FROM Order o WHERE o.user.id = ?1")
    List<Order> findAllByUserId(Long userId);
//    Page<Order> findAllByUserId(Long userId, Pageable pageable);

    List<Order> findAllByStatus(StatusOrder statusOrder);
    @Query("SELECT o FROM Order o WHERE o.status = ?1")
    List<Order> findAllByOrderCode(String orderCode);
    @Query("SELECT o FROM Order o JOIN o.orderDetails od WHERE o.user.id = ?1 AND (od.product.productId = ?2 OR od.combo.comboId = ?3)")
    List<Order> findByUserIdAndProductIdOrComboId(Long userId, Long productId, Long comboId);

    @Query("SELECT o FROM Order o")
    List<Order> findAlll();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status.statusId = 5")
    Long getTotalAmountForCompletedOrders();

    @Query("SELECT COUNT(o) FROM Order o WHERE FUNCTION('MONTH', o.createdAt) = ?1 AND FUNCTION('YEAR', o.createdAt) = ?2")
    Long countOrdersByMonth(int month, int year);

    @Query("select sum(o.totalAmount) from Order o where function('MONTH',o.createdAt) = ?1 and function('YEAR', o.createdAt)= ?2 and o.status.statusId = 5")
    Long getTotalAmountByMonth(int month, int year);

    @Query("select sum(o.totalAmount) from Order o where function('MONTH',o.createdAt) = ?2 and function('DAY', o.createdAt) = ?1 and function('YEAR', o.createdAt)= ?3  and o.status.statusId = 5")
    Long getTotalAmountByWeek(int day, int month, int year);

    @Query("SELECT SUM(o.totalPrice) FROM OrderDetail o WHERE o.status.statusId = 5 and o.store.storeId= ?1")
    Long getTotalAmountForCompletedOrdersStore(Long idStore);

    @Query("SELECT COUNT(o) FROM OrderDetail o join o.order od WHERE o.store.storeId = ?1 and FUNCTION('MONTH', od.createdAt) = ?2 AND FUNCTION('YEAR', od.createdAt) = ?3 and o.status.statusId = 5")
    Long countOrdersByMonthStore(Long storeId,int month, int year);

    @Query("select sum(od.totalPrice) from OrderDetail od join od.order o where od.store.storeId= ?1 and function('MONTH',o.createdAt) = ?2 and function('YEAR', o.createdAt)= ?3  and od.status.statusId = 5")
    Long getTotalAmountByMonthStore(Long storeId,int month, int year);

    @Query("select sum(od.totalPrice) from OrderDetail od join od.order o where od.store.storeId = ?1 and function('MONTH',o.createdAt) = ?3 and function('DAY', o.createdAt) = ?2 and function('YEAR', o.createdAt)= ?4  and od.status.statusId = 5")
    Long getTotalAmountByWeekStore(Long storeId,int day, int month, int year);

    @Query("SELECT od " +
            "FROM Order o " +
            "JOIN o.orderDetails od " +
            "WHERE o.orderCode = ?1 AND od.store.storeId = ?2")
    Optional<List<OrderDetail>> findOrderDetailsByOrderCodeAndStore(String orderCode, Long storeId);

    @Query("SELECT distinct o FROM Order o JOIN o.orderDetails od WHERE od.store.storeId = ?1 and od.status.statusName = ?2")
    Page<Order> findOrdersWithStatusAndStores(Long storeId, String statusName, Pageable pageable);
}

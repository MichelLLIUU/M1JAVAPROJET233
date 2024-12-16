package services;

import dao.OrderDAO;
import dao.CartDAO;
import dao.CustomerDAO;
import dao.FactureDAO;

import items.Cart;
import items.Customer;
import items.Order;

import java.util.List;

public class OrderService {
    private OrderDAO orderDAO;
    private CartDAO cartDAO;
    private CustomerDAO customerDAO;

    public OrderService() {
        orderDAO = new OrderDAO();
        cartDAO = new CartDAO();
        customerDAO = new CustomerDAO();
    }


    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }


    public boolean createOrder(int customerId) {

        List<Cart> cartItems = cartDAO.getAllCartItems();
        if (cartItems.isEmpty()) {
            return false;
        }

        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            return false;
        }

        boolean orderCreated = orderDAO.createOrder(customer, cartItems);
        if (orderCreated) {
            cartDAO.clearCart();
        }
        return orderCreated;
    }
    public boolean generateFacture(int orderId) {
        FactureDAO factureDAO = new FactureDAO();
        return factureDAO.generateFacture(orderId);
    }

    public boolean cancelOrder(int orderId) {
        return orderDAO.cancelOrder(orderId);
    }






    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }
}



package services;

import dao.CustomerDAO;
import dao.FactureDAO;
import dao.OrderDAO;
import items.Customer;
import items.Facture;
import items.Order;
import items.Product;

import java.util.List;
import java.util.Map;

public class FactureService {
    private FactureDAO factureDAO;
    private OrderDAO orderDAO;
    private CustomerDAO customerDAO;

    public FactureService() {
        this.factureDAO = new FactureDAO();
        this.orderDAO = new OrderDAO();
        this.customerDAO = new CustomerDAO();
    }

    public List<Facture> getAllFactures() {
        return factureDAO.getAllFactures();
    }


    public Facture getFactureById(int id) {
        return factureDAO.getFactureById(id);
    }


    public Map<Product, Integer> getOrderDetails(int orderId) {
        return orderDAO.getOrderItemsByOrderId(orderId);
    }


    public Customer getCustomerDetails(int customerId) {
        return customerDAO.getCustomerById(customerId);
    }
    public boolean generateFacture(int orderId) {
        return factureDAO.generateFacture(orderId);
    }
}


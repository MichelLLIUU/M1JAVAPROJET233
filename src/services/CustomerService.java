package services;

import dao.CustomerDAO;
import items.Customer;

import java.util.List;

public class CustomerService {
    private CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }


    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }


    public Customer getCustomerById(int id) {
        return customerDAO.getCustomerById(id);
    }


    public boolean addCustomer(Customer customer) {
        return customerDAO.addCustomer(customer);
    }

    public List<Customer> searchCustomersByName(String keyword) {
        return customerDAO.searchCustomersByName(keyword);
    }


    public boolean deleteCustomer(int customerId) {
        return customerDAO.deleteCustomer(customerId);
    }
}



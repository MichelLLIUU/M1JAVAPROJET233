package items;

import java.util.Date;
import java.util.Map;


public class Order {
    private int id;
    private Customer customer;
    private Map<Product,Integer> items; //product and its quantity
    private double totalAmount;
    private Date date;
    private String status;
    private int customerId;
    private String address;
    private static int counter = 0;

    public Order() {};

    public Order(int id, Customer customer, double totalAmount, Date date, String status, String address) {
        this.id = id;
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.date = date;
        this.status = status;
        this.address = address;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Map<Product, Integer> getItems() {
        return items;
    }

    public void setItems(Map<Product, Integer> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String toString() {
        return "Order{" + "id=" + id + ", customerId=" + customerId + ", totalAmount=" + totalAmount + ", orderDate=" + date + ", orderStatus=" + status + '}';
    }

    //Add item into the order
    public void addItem(Product product, int quantity) {
        items.put(product, quantity);
    }

    // 获取商品列表
    public Map<Product, Integer> getProducts() {
        return items;
    }

    //Remove the item
    public void removeItem(Product product) {
        if(items.containsKey(product)) {
            int quantity = items.get(product);
            product.setStock(product.getStock() - quantity);
            recalculateTotal();
        }else{
            throw new IllegalArgumentException("Don't have such product" + product.getName());
        }
    }

    private void recalculateTotal() {
        this.totalAmount = items.values().stream().mapToInt(x -> x).sum();
    }

}

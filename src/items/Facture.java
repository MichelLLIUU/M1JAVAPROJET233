package items;

import java.util.Date;

public class Facture {
    private int id;
    private int orderId;
    private int customerId;
    private double totalAmount;
    private Date issueDate;

    public Facture(int id, int orderId, int customerId, double totalAmount, Date issueDate) {
        this.id = id;
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.issueDate = issueDate;
    }

    // Getter 和 Setter
    public int getId() {
        return id;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Date getIssueDate() {
        return issueDate;
    }
}

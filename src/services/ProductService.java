package services;

import dao.ProductDAO;
import items.Product;

import java.util.List;

public class ProductService {
    private ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }


    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }


    public Product getProductById(int id) {
        return productDAO.getProductById(id);
    }


    public boolean updateStock(int productId, int newStock) {
        return productDAO.updateProductStock(productId, newStock);
    }

    public boolean updateProduct(Product product) {
        return productDAO.updateProduct(product);
    }

    public boolean deleteProduct(int productId) {
        return productDAO.deleteProduct(productId);
    }


    public List<Product> searchProductsByName(String keyword) {
        return productDAO.searchProductsByName(keyword);
    }


    public boolean addProduct(Product product) {
        return productDAO.addProduct(product);
    }

}

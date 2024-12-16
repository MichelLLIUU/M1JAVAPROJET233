package services;

import dao.CartDAO;
import items.Cart;

import java.util.List;

public class CartService {
    private CartDAO cartDAO;

    public CartService() {
        cartDAO = new CartDAO();
    }


    public List<Cart> getAllCartItems() {
        return cartDAO.getAllCartItems();
    }


    public boolean removeFromCartById(int productid) {
        Cart cartItem = cartDAO.getCartItemByProductId(productid);
        if (cartItem != null) {
            cartDAO.removeCartItem(cartItem.getProductId());
            return true;
        }
        return false;
    }


    public void addToCart(int productId, String productName, double price, int quantity) {
        Cart existingItem = cartDAO.getCartItemByProductId(productId);
        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + quantity;
            cartDAO.updateCartItemQuantity(productId, newQuantity);
        } else {
            cartDAO.addCartItem(productId, productName, price, quantity);
        }
    }

    public boolean updateQuantity(int productId, int newQuantity) {
        try {
            return cartDAO.updateQuantity(productId, newQuantity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void clearCart() {
        cartDAO.clearCart();
    }
}





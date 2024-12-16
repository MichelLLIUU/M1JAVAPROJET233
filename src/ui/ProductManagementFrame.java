package ui;

import services.ProductService;
import services.CartService;
import items.Product;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class ProductManagementFrame extends JFrame {
    private ProductService productService;
    private CartService cartService;
    private JPanel productPanel;
    private int currentPage = 1;
    private int itemsPerPage = 6;

    public ProductManagementFrame() {
        setTitle("Product Management Interface");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialisation services
        productService = new ProductService();
        cartService = new CartService();

        // Setting up the main layout
        setLayout(new BorderLayout());


        productPanel = new JPanel();
        productPanel.setLayout(new GridLayout(3, 2, 10, 10));
        add(new JScrollPane(productPanel), BorderLayout.CENTER);


        JPanel bottomPanel = new JPanel();
        JButton prevButton = new JButton("previous page");
        JButton nextButton = new JButton("next page");
        JButton editProductsButton = new JButton("Edit Product"); // 新增编辑商品按钮

        bottomPanel.add(prevButton);
        bottomPanel.add(nextButton);
        bottomPanel.add(editProductsButton);
        add(bottomPanel, BorderLayout.SOUTH);

        //在顶部添加搜索功能
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchProducts(searchField.getText()));

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search by Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // Functions of the Pagination Button
        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadProducts();
            }
        });

        nextButton.addActionListener(e -> {
            if ((currentPage - 1) * itemsPerPage < productService.getAllProducts().size()) {
                currentPage++;
                loadProducts();
            }
        });

        // Edit Product Button Function
        editProductsButton.addActionListener(e -> new ProductEditFrame(productService).setVisible(true));


        loadProducts();
    }

    // Load product data on current page
    private void loadProducts() {
        productPanel.removeAll();
        productPanel.setLayout(new GridLayout(3, 2, 10, 10));
        List<Product> products = productService.getAllProducts();
        int start = (currentPage - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, products.size());

        for (int i = start; i < end; i++) {
            Product product = products.get(i);
            JPanel itemPanel = createProductPanel(product);
            productPanel.add(itemPanel);
        }

        productPanel.revalidate();
        productPanel.repaint();
    }

    // Creating individual product panels
    private JPanel createProductPanel(Product product) {
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BorderLayout());
        productPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        //Load image area
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            URL imageUrl = new URL(product.getImageUrl()); // Get the URL of the image from the database
            ImageIcon icon = new ImageIcon(new ImageIcon(imageUrl).getImage().getScaledInstance(120, 90, Image.SCALE_SMOOTH));
            imageLabel.setIcon(icon);
        } catch (Exception e) {
            imageLabel.setText("Image failed to load");
        }

        // 商品信息区域
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5)); // 水平排列，间距为10
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel priceLabel = new JLabel("price: " + product.getPrice());
        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);

        // 按钮区域
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> {
            cartService.addToCart(product.getId(), product.getName(), product.getPrice(), 1);

            JOptionPane.showMessageDialog(this, product.getName() + " Added to cart!");
        });

        // Add to main panel
        productPanel.add(imageLabel, BorderLayout.CENTER);
        productPanel.add(infoPanel, BorderLayout.NORTH);   // Name and price at the top
        productPanel.add(addToCartButton, BorderLayout.SOUTH);

        return productPanel;
    }

    // 实现搜索逻辑
    private void searchProducts(String keyword) {
        productPanel.removeAll();
        List<Product> filteredProducts = keyword.isEmpty()
                ? productService.getAllProducts()
                : productService.searchProductsByName(keyword);

        for (Product product : filteredProducts) {
            productPanel.add(createProductPanel(product));
        }

        productPanel.revalidate();
        productPanel.repaint();
    }

    public static void main(String[] args) {

        // Set the global language to English
        Locale.setDefault(Locale.ENGLISH);

        SwingUtilities.invokeLater(() -> {
            ProductManagementFrame frame = new ProductManagementFrame();
            frame.setVisible(true);
        });
    }
}



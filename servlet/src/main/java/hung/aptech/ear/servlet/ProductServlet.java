package hung.aptech.ear.servlet;

import hung.aptech.ejb.entity.Product;
import hung.aptech.ejb.service.impl.ProductService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Query;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(
        name = "productServlet",
        urlPatterns = {"/products", "/product"}, // Modified URL patterns
        asyncSupported = true,
        description = "PRODUCT"
)
public class ProductServlet extends HttpServlet {
    @EJB
    private ProductService productService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null) {
            __handleListProducts(req, resp);
        } else {
            switch (action) {
                case "add":
                    __handleCreateProductView(req, resp);
                    break;
                case "edit":
                    __handleEditProductView(req, resp);
                    break;
                case "delete":
                    __handleDeleteProduct(req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");

        if (action == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action is required");
        } else {
            switch (action) {
                case "add":
                    __handleCreateProduct(req, resp);
                    break;
                case "edit":
                    __handleEditProduct(req, resp);
                    break;
                case "delete":
                    __handleDeleteProduct(req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        }
    }

    private void __handleListProducts(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<Product> products = productService.findAll(new Query());
            req.setAttribute("products", products);

            req.getRequestDispatcher("/views/index.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to retrieve products.");
        }
    }

    private void __handleCreateProductView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/views/create_product.jsp").forward(req, resp);
    }

    private void __handleCreateProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String name = req.getParameter("name");
            double price = Double.parseDouble(req.getParameter("price"));

            Product product = new Product();
            product.setName(name);
            product.setPrice(price);

            productService.save(product);
            resp.sendRedirect(req.getContextPath() + "/products");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product details.");
        }
    }

    private void __handleEditProductView(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int productId = Integer.parseInt(req.getParameter("id"));
            Optional<Product> product = productService.findById((long) productId);

            if (product.isPresent()) {
                req.setAttribute("product", product.get());
                req.getRequestDispatcher("/views/edit_product.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found.");
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID.");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to retrieve product.");
        }
    }

    private void __handleEditProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int productId = Integer.parseInt(req.getParameter("id"));
            String name = req.getParameter("name");
            double price = Double.parseDouble(req.getParameter("price"));

            Optional<Product> productOptional = productService.findById((long) productId);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                product.setName(name);
                product.setPrice(price);

                productService.update(product);
                resp.sendRedirect(req.getContextPath() + "/products");
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found.");
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product details.");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to update product.");
        }
    }

    private void __handleDeleteProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int productId = Integer.parseInt(req.getParameter("id"));
            productService.deleteById((long) productId);
            resp.sendRedirect(req.getContextPath() + "/products");
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID.");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to delete product.");
        }
    }
}

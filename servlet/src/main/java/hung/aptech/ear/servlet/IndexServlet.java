package hung.aptech.ear.servlet;

import hung.aptech.ejb.entity.Product;
import hung.aptech.ejb.service.impl.ProductService;
import jakarta.ejb.EJB;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Operator;
import model.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(
        name = "indexServlet",
        urlPatterns = {"/index"},
        loadOnStartup = 1,
        asyncSupported = true,
        description = "INDEX"
)
public class IndexServlet extends HttpServlet {
    @EJB
    private ProductService productService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getServletPath();

        switch (path) {
            case "/index":
                __handleFindAll(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void __handleFindAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // Extract parameters from the request
            int pageNumber = 1; // Default to the first page
            int pageSize = 10; // Default page size
            boolean getAllRecords = false; // Default to false (pagination)

            // Extract parameters from the request (with default fallback)
            String pageNumberParam = req.getParameter("pageNumber");
            String pageSizeParam = req.getParameter("pageSize");
            String getAllRecordsParam = req.getParameter("getAllRecords");

            if (pageNumberParam != null) {
                pageNumber = Integer.parseInt(pageNumberParam);
            }
            if (pageSizeParam != null) {
                pageSize = Integer.parseInt(pageSizeParam);
            }

            if (getAllRecordsParam != null) {
                getAllRecords = Boolean.parseBoolean(getAllRecordsParam);
            }

            // Extract sort fields
            List<Query.SortField> sortFields = new ArrayList<>();
            String sortFieldParam = req.getParameter("sortFields");
            if (sortFieldParam != null) {
                for (String sortField : sortFieldParam.split(",")) {
                    String[] parts = sortField.split(":");
                    sortFields.add(new Query.SortField(parts[0], "asc".equalsIgnoreCase(parts[1])));
                }
            }

            // Extract filter fields
            List<Query.FilterField> filterFields = new ArrayList<>();
            String filterFieldParam = req.getParameter("filterFields");
            if (filterFieldParam != null) {
                for (String filterField : filterFieldParam.split(",")) {
                    String[] parts = filterField.split(":");
                    // Validate and parse operator safely
                    Operator operator = Operator.valueOf(parts[2].toUpperCase());
                    filterFields.add(new Query.FilterField(parts[0], parts[1], operator));
                }
            }

            // Create the Query object
            Query query = Query.builder()
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .getAllRecords(getAllRecords)
                    .sortFields(sortFields)
                    .filterFields(filterFields)
                    .build();

            // Call the productService's findAll method
            List<Product> products = productService.findAll(query);

            // Set the products as a request attribute
            req.setAttribute("products", products);

            // Forward the request to the JSP view
            req.getRequestDispatcher("/views/products.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid page number or page size.");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing your request.");
        }
    }

}

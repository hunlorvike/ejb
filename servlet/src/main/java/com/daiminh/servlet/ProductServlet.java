package com.daiminh.servlet;

import com.daiminh.ejb.bean.ProductBean;
import com.daiminh.ejb.dto.ProductDto;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "productServlet", value = "/products")
public class ProductServlet extends HttpServlet {

    @EJB
    private ProductBean productBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<ProductDto> products = productBean.findAll();
        request.setAttribute("products", products);
        request.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        Double price = Double.valueOf(request.getParameter("price"));

        ProductDto productDTO = ProductDto.builder()
                .name(name)
                .price(price)
                .build();
        productBean.create(productDTO);
        response.sendRedirect(request.getContextPath() + "/products");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.valueOf(request.getParameter("id"));
        String name = request.getParameter("name");
        Double price = Double.valueOf(request.getParameter("price"));

        ProductDto productDTO = ProductDto.builder()
                .id(id)
                .name(name)
                .price(price)
                .build();
        productBean.update(id, productDTO);
        response.sendRedirect(request.getContextPath() + "/products");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.valueOf(request.getParameter("id"));
        productBean.delete(id);
        response.sendRedirect(request.getContextPath() + "/products");
    }
}

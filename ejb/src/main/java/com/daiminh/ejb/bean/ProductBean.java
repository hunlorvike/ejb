package com.daiminh.ejb.bean;

import com.daiminh.ejb.dto.ProductDto;
import com.daiminh.ejb.entity.Product;
import com.daiminh.ejb.jms.MessageProducer;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class ProductBean {
    @PersistenceContext(unitName = "dethi")
    private EntityManager em;

    @Inject
    private MessageProducer messageProducer;

    public void create(ProductDto productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        em.persist(product);

        messageProducer.sendMessage("Hung", "Created new product: " + product.getName());
    }

    public List<ProductDto> findAll() {
        List<Product> products = em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
        return products.stream()
                .map(product -> new ProductDto(product.getId(), product.getName(), product.getPrice()))
                .collect(Collectors.toList());
    }

    public Optional<ProductDto> findById(Long id) {
        Product product = em.find(Product.class, id);
        return Optional.ofNullable(product)
                .map(p -> new ProductDto(p.getId(), p.getName(), p.getPrice()));
    }

    public List<ProductDto> findByName(String name) {
        List<Product> products = em.createQuery("SELECT p FROM Product p WHERE p.name LIKE :name", Product.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
        return products.stream()
                .map(product -> new ProductDto(product.getId(), product.getName(), product.getPrice()))
                .collect(Collectors.toList());
    }

    public void update(Long id, ProductDto productDTO) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice());
            em.merge(product);
        }
    }

    public void delete(Long id) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            em.remove(product);
        }
    }
}

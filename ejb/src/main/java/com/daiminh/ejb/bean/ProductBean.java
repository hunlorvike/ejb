package com.daiminh.ejb.bean;

import com.daiminh.ejb.dto.ProductDto;
import com.daiminh.ejb.entity.Product;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class ProductBean {
    @PersistenceContext(unitName = "dethi")
    private EntityManager em;

    public void create(ProductDto productDTO) {
        Product product = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .build();
        em.persist(product);
    }

    public List<ProductDto> findAll() {
        List<Product> products = em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
        return products.stream()
                .map(product -> ProductDto.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .build())
                .collect(Collectors.toList());
    }

    public Optional<ProductDto> findById(Long id) {
        Product product = em.find(Product.class, id);
        return Optional.ofNullable(product)
                .map(p -> ProductDto.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .price(p.getPrice())
                        .build());
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

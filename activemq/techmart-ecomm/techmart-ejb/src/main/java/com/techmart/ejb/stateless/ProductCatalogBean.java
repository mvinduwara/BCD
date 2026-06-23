package com.techmart.ejb.stateless;

import com.techmart.model.Product;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.logging.Logger;

@Stateless
public class ProductCatalogBean {

    private static final Logger logger = Logger.getLogger(ProductCatalogBean.class.getName());

    @PersistenceContext(unitName = "TechMartPU")
    private EntityManager em;

    public Product findById(Long id) {
        long start = System.nanoTime();
        Product product = em.find(Product.class, id);
        long elapsed = (System.nanoTime() - start) / 1_000_000;
        logger.info("findById(" + id + ") completed in " + elapsed + "ms");
        return product;
    }

    public List<Product> findAll() {
        long start = System.nanoTime();
        List<Product> products = em
                .createQuery("SELECT p FROM Product p ORDER BY p.name", Product.class)
                .getResultList();
        long elapsed = (System.nanoTime() - start) / 1_000_000;
        logger.info("findAll() returned " + products.size() + " products in " + elapsed + "ms");
        return products;
    }

    public List<Product> findByCategory(String category) {
        long start = System.nanoTime();
        TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.category = :category ORDER BY p.name",
                Product.class);
        query.setParameter("category", category);
        List<Product> result = query.getResultList();
        long elapsed = (System.nanoTime() - start) / 1_000_000;
        logger.info("findByCategory(" + category + ") returned " + result.size() + " in " + elapsed + "ms");
        return result;
    }

    public List<Product> searchByName(String keyword) {
        long start = System.nanoTime();
        TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE LOWER(p.name) LIKE :keyword ORDER BY p.name",
                Product.class);
        query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
        List<Product> result = query.getResultList();
        long elapsed = (System.nanoTime() - start) / 1_000_000;
        logger.info("searchByName(" + keyword + ") returned " + result.size() + " in " + elapsed + "ms");
        return result;
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            em.persist(product);
            logger.info("New product saved: " + product.getName());
            return product;
        } else {
            Product merged = em.merge(product);
            logger.info("Product updated: " + merged.getName());
            return merged;
        }
    }

    public void delete(Long id) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            em.remove(product);
            logger.info("Product deleted: " + id);
        }
    }
}
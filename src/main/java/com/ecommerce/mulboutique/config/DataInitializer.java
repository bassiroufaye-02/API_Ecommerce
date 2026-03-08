package com.ecommerce.mulboutique.config;

import com.ecommerce.mulboutique.entity.Category;
import com.ecommerce.mulboutique.entity.Product;
import com.ecommerce.mulboutique.entity.Store;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.repository.CategoryRepository;
import com.ecommerce.mulboutique.repository.ProductRepository;
import com.ecommerce.mulboutique.repository.StoreRepository;
import com.ecommerce.mulboutique.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeData();
    }

    private void initializeData() {
        // Verifier si les donnees existent deja
        if (userRepository.count() > 0) {
            return;
        }

        // Creer les utilisateurs
        User admin = createUser("admin", "admin@ecommerce.com", "Admin", "User", User.Role.ADMIN);
        User storeOwner = createUser("boutiquier1", "boutiquier1@mulboutique.sn", "Mamadou", "Diop", User.Role.STORE_OWNER);
        User client = createUser("client1", "client1@mulboutique.sn", "Awa", "Ndiaye", User.Role.CLIENT);

        admin = userRepository.save(admin);
        storeOwner = userRepository.save(storeOwner);
        client = userRepository.save(client);

        // Creer les boutiques
        Store techStore = createStore("Tech Dakar", "Boutique de produits technologiques", "contact@techdakar.sn", "221771234567", "12 Avenue Cheikh Anta Diop", "Dakar", "Senegal", "11000", storeOwner);
        Store fashionStore = createStore("Boutique Paris Mode", "Mode et vetements", "contact@parismode.fr", "33123456789", "8 Rue de Rivoli", "Paris", "France", "75004", storeOwner);

        techStore = storeRepository.save(techStore);
        fashionStore = storeRepository.save(fashionStore);

        // Creer les categories
        Category smartphones = createCategory("Telephones", "Telephones mobiles et smartphones", techStore);
        Category computers = createCategory("Ordinateurs", "Ordinateurs portables et de bureau", techStore);
        Category menClothing = createCategory("Vetements Homme", "Vetements pour hommes", fashionStore);
        Category womenClothing = createCategory("Vetements Femme", "Vetements pour femmes", fashionStore);

        smartphones = categoryRepository.save(smartphones);
        computers = categoryRepository.save(computers);
        menClothing = categoryRepository.save(menClothing);
        womenClothing = categoryRepository.save(womenClothing);

        // Creer les produits
        createProduct("Telephone Itel A70", "Telephone abordable", new BigDecimal("89.99"), 60, "./image/telephone.png", "ITELA70", smartphones, techStore);
        createProduct("Samsung Galaxy A15", "Smartphone populaire", new BigDecimal("179.99"), 40, "./image/galaxy_a15.png", "GALAXYA15", smartphones, techStore);
        createProduct("HP EliteBook 840", "Ordinateur portable professionnel", new BigDecimal("799.99"), 15, "./image/elitebook840.png", "ELITEBOOK840", computers, techStore);
        createProduct("Boubou Traditionnel", "Tenue traditionnelle senegalaise", new BigDecimal("49.99"), 80, "./image/boubou.png", "BOUBOU001", menClothing, fashionStore);
        createProduct("Robe Parisienne", "Robe pour sorties", new BigDecimal("69.99"), 35, "./image/robe_parisienne.png", "ROBE002", womenClothing, fashionStore);
    }

    private User createUser(String username, String email, String firstName, String lastName, User.Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private Store createStore(String name, String description, String contactEmail, String contactPhone, String address, String city, String country, String postalCode, User owner) {
        Store store = new Store();
        store.setName(name);
        store.setDescription(description);
        store.setContactEmail(contactEmail);
        store.setContactPhone(contactPhone);
        store.setAddress(address);
        store.setCity(city);
        store.setCountry(country);
        store.setPostalCode(postalCode);
        store.setOwner(owner);
        store.setIsActive(true);
        store.setCreatedAt(LocalDateTime.now());
        store.setUpdatedAt(LocalDateTime.now());
        return store;
    }

    private Category createCategory(String name, String description, Store store) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setStore(store);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return category;
    }

    private Product createProduct(String name, String description, BigDecimal price, Integer stockQuantity, String imageUrl, String sku, Category category, Store store) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setImageUrl(imageUrl);
        product.setSku(sku);
        product.setCategory(category);
        product.setStore(store);
        product.setIsActive(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }
}
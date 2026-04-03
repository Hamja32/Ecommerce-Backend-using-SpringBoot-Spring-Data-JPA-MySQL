package com.EcomProject.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.EcomProject.model.Product;
import com.EcomProject.repository.ProductRepo;

@Service
public class ProductService {
	@Autowired
	ProductRepo repo;
	
	public List<Product> getAllProducts() {
		List<Product> products = repo.findAll();
		return products;
	}
	public List<Product> searchProducts(String keyword) {
	    return repo.searchProducts(keyword);
	}
	public Optional<Product> getProductByItsId(int id) {
		return repo.findById(id);
	}
	// ✅ createProduct - image optional
	@Transactional
	public Product createProduct(Product prod, MultipartFile imageFile) throws IOException {
	    // Image sirf tab set karo jab diya gaya ho
	    if(imageFile != null && !imageFile.isEmpty()){
	        prod.setImageName(imageFile.getOriginalFilename());
	        prod.setImageType(imageFile.getContentType());
	        prod.setImageDate(imageFile.getBytes());
	    }
	    return repo.save(prod);
	}
	
	public Product updateTheProduct(int id, Product product, MultipartFile imageFile) throws IOException {
	    Product existingProduct = repo.findById(id)
	            .orElseThrow(() -> new RuntimeException("Product not found"));
	    
	    // ✅ Image sirf tab update karo jab nayi di gayi ho
	    if(imageFile != null && !imageFile.isEmpty()){
	        product.setImageName(imageFile.getOriginalFilename());
	        product.setImageType(imageFile.getContentType());
	        product.setImageDate(imageFile.getBytes());
	    } else {
	        // ✅ Purani image retain karo
	        product.setImageName(existingProduct.getImageName());
	        product.setImageType(existingProduct.getImageType());
	        product.setImageDate(existingProduct.getImageDate());
	    }
	    return repo.save(product);
	}
	public void deleteProduct(int Pid) {
		repo.deleteById(Pid);
		
	}

}

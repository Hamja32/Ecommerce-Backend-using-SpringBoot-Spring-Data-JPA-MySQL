package com.EcomProject.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.EcomProject.model.Product;
import com.EcomProject.service.ProductService;


@CrossOrigin
@RestController
@RequestMapping("/api")
public class ProductController {
	@Autowired
	ProductService service;

	@GetMapping("/products")
	public ResponseEntity<List<Product>> getAllProducts() {
		List<Product> listProducts = service.getAllProducts();
		if (listProducts.isEmpty()) {
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    }
		return new ResponseEntity<>(listProducts,HttpStatus.OK);
	}
	
	@GetMapping("/products/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable int id) {
	    return service.getProductByItsId(id)
	            .map(product -> ResponseEntity.ok(product)) // If present, return 200 + body
	            .orElseGet(() -> ResponseEntity.notFound().build()); // If empty, return 404
	}
	
	//add product
	@PostMapping("/products")
	public ResponseEntity<?> addProduct(
	    @RequestPart("product") Product product, // Changed name to "product"
	    @RequestPart("imageFile") MultipartFile imageFile
	) {
		// 1. Basic Validation
	    if (imageFile.isEmpty()) {
	        return ResponseEntity.badRequest().body("Please upload an image.");
	    }
	    try {
	        Product p = service.createProduct(product, imageFile);
	        return new ResponseEntity<>(p, HttpStatus.CREATED);
	    } catch (IOException e) {
	    	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not save the image: " + e.getMessage());
	    }catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating product");
	    }
	}
	//image getting
	@GetMapping("/products/{id}/image")
	public ResponseEntity<byte[]> getImageByProductId(@PathVariable int id){
		Product product = service.getProductByItsId(id).get();
		byte[] imageFile = product.getImageDate();
		
		return ResponseEntity.ok().contentType(MediaType.valueOf(product.getImageType()))
				.body(imageFile);
	}
	
	//update the product
	@PutMapping("/products/{id}")
	public ResponseEntity<?> updateProduct(
	    @PathVariable int id, 
	    @RequestPart Product product, 
	    @RequestPart MultipartFile imageFile // imageFile can be optional
	) {
	    try {
	        Product updatedProduct = service.updateTheProduct(id, product, imageFile);
	        // Return the actual object so the frontend can use the new data
	        return ResponseEntity.ok(updatedProduct);
	    } catch (ConfigDataResourceNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Update failed");
	    }
	}
	//delete the product
	@DeleteMapping("/products/{id}")
	public ResponseEntity<String> removeProduct(@PathVariable int id){
		Product prd = service.getProductByItsId(id).get();
		if(prd != null) {			
			service.deleteProduct(id);
			return new ResponseEntity<String>("Deleted",HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Product Not Found",HttpStatus.NOT_FOUND);
		}
	}
}

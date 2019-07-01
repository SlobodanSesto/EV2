package com.test.evstore.controllers;


import com.test.evstore.models.Person;
import com.test.evstore.models.Product;
import com.test.evstore.repositories.ProductRepo;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    ProductRepo productRepo;
    @Autowired
    JdbcTemplate jdbcTemplate;

//    @ResponseBody
//    public String test() {
//        return "Products";
//    }

    //just returns data no view
    @GetMapping(value = "allproducts")
    @ResponseBody
    public List<Product> allProducts() {
        return productRepo.getAllProducts();
    }

    public List<Product> getAllProducts() {
        return productRepo.getAllProducts();
    }

    public List<Product> getFeaturedProducts() { return productRepo.getFeatured(); }

    @RequestMapping(value = "/search-products", method = RequestMethod.POST)
    public String searchProducts(@RequestParam("tfSearch") String searchText,
                                 Model model, HttpSession session) {
        List<Product> products = null;
        products = productRepo.searchProducts(searchText);
//        System.out.println(searchText);
//        System.out.println(products.size());
        model.addAttribute("products", products);
        return "products";
    }

    @RequestMapping(value = "/filter/{cate:[\\d]+}", method = RequestMethod.GET )
    public String filter(@PathVariable("cate") int category , Model model,
                         HttpSession session) {
        List<Product> products = null;
//        System.out.println(category);
        products = productRepo.findByCategory(category);
        model.addAttribute("products", products);
        return "products";
    }

    @RequestMapping(value = "/deleteProduct", method = RequestMethod.POST)
    public String deleteProduct(@RequestParam("productId") int id, Model model,
                                HttpSession session) {
        productRepo.deleteProductById(id);
        List<Product> products = null;
        products = productRepo.getAllProducts();
        model.addAttribute("products", products);
        return "controlpanel";
    }

    @GetMapping(value = "/findProduct/{id:[\\d]+}")
    @ResponseBody
    public Product findProduct(@PathVariable("id") int id) {
//        System.out.println(id);
        Product product = productRepo.getProductById(id);
        return product;
    }

    @RequestMapping(value = "/saveProduct", method = RequestMethod.POST)
    public String saveProduct(@RequestParam("productId") Integer id,
                              @RequestParam("product-name") String productName,
                              @RequestParam("product-price") Double productPrice,
                              @RequestParam("product-desc") String productDesc,
                              @RequestParam("product-stock") Integer productStock,
                              @RequestParam("product-category") Integer productCategory,
                              @RequestParam("product-img") MultipartFile img,
                              Model model, HttpSession session) {
        List<Product> products = null;
        if (id == null) {

            Product p = new Product(productName, productPrice, productDesc);
            p.setCategory(productCategory);
            p.setQuantity(productStock);
            p.setProductId(productRepo.addProduct(p));
            products = productRepo.getAllProducts();
//            if (!file.isEmpty()) {
//            if (file != null) {
//                System.out.println("Dobio sam fajl!");
//                BufferedImage src = null;
//                try {
//                    src = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                String filePath = "src/main/resources/static/images/"+p.getProductId()+".jpg";
//                File destination = new File(filePath); // something like C:/Users/tom/Documents/nameBasedOnSomeId.png
//
//                System.out.println(filePath);
//                try {
//                    ImageIO.write(src, ".jpg", destination);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                //Save the id you have used to create the file name in the DB. You can retrieve the image in future with the ID.

            if (img != null) {
                try {
                    String folder = "src/main/resources/static/images/";
                    String filename = img.getOriginalFilename();
                    String updatedFileName = p.getProductId() + "." + FilenameUtils.getExtension(filename);
                    byte[] bytes = img.getBytes();
                    Path path = Paths.get(folder + updatedFileName);
                    try (FileOutputStream outputStream = new FileOutputStream(new File(folder+updatedFileName))) {
//                        Files.write(path, bytes);
                        outputStream.write(bytes);
                        model.addAttribute("photoUploaded", "Photo uploaded successfully");
                    }
                }catch (Exception e) {
                    System.out.println(e);
                    jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
                    model.addAttribute("photoError", "Encountered an error uploading the file");
                    return "controlpanel";
                }
            }
            model.addAttribute("products", products);
            return "controlpanel";
        }
        Product p = new Product(productName, productPrice, productDesc);
        p.setProductId(id);
        p.setCategory(productCategory);
        p.setQuantity(productStock);
        productRepo.updateProduct(p);
        products = productRepo.getAllProducts();
        model.addAttribute("products", products);
        return "controlpanel";
    }

    //might change it to GET request , thats why admin check is there
    @RequestMapping(value = "/togglefeatured", method = RequestMethod.POST)
    public String toggleFeatured(HttpSession session, Model model,
                                 @RequestParam("proId") int proId,
                                 @RequestParam("featuredVal") int featuredVal) {
        Person p = (Person) session.getAttribute("person");
        if (p.getUser().getRole() == 2) {
            if (featuredVal == 2) {
                productRepo.toggleFeatured(proId, 1);
            } else {
                productRepo.toggleFeatured(proId, 2);
            }
            List<Product> products = null;
            products = productRepo.getAllProducts();
            model.addAttribute("products", products);
            return "controlpanel";
        }
        return "index";
    }
}

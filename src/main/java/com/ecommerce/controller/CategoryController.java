package com.ecommerce.controller;

import com.ecommerce.model.Category;
import com.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
@RestController
@RequestMapping("/api")
public class CategoryController {

@Autowired
    CategoryService categoryService;


   // @GetMapping("/api/public/categories")
    @RequestMapping(value = "/public/categories", method = RequestMethod.GET )
    private ResponseEntity<List<Category>> getAllCategories()
    {
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories,HttpStatus.OK);}

    @RequestMapping(value = "/public/categories", method = RequestMethod.POST )
    public ResponseEntity<String> createCategory(@RequestBody Category category)
    {
        categoryService.createCategory(category);
        return new ResponseEntity<>("Category added successfully", HttpStatus.CREATED);
    }

    //@PutMapping("/public/categories/{categoryId}")
    @RequestMapping(value = "/public/categories/{categoryId}", method = RequestMethod.PUT )
    public ResponseEntity<String> updateCategory(@RequestBody Category category,@PathVariable Long categoryId)
    {
       try {
           Category savedCategory = categoryService.updateCategory(category, categoryId);
           return ResponseEntity.status(HttpStatus.OK).body("Category updated successfully");
       }
       catch (ResponseStatusException ex)
       {return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());}
       }

   // @DeleteMapping("/admin/categories/{categoryId}")
    @RequestMapping(value = "/admin/categories/{categoryId}", method = RequestMethod.DELETE )
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId)
    {
        try {
            String status = categoryService.deleteCategory(categoryId);
            return ResponseEntity.status(HttpStatus.OK).body(status);
            // catch(ResponseStatusException ex)
        }
        catch (ResponseStatusException ex)
        {return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());}
    }

}

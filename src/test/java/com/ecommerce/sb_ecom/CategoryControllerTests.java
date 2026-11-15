package com.ecommerce.sb_ecom;

import com.ecommerce.SbEcomApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SbEcomApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CategoryControllerTests {

    private static final String CATEGORIES = "/api/public/categories";

    @Autowired
    private MockMvc mockMvc;

    private void create(String name) throws Exception {
        mockMvc.perform(post(CATEGORIES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\"" + name + "\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void createsAndListsCategoriesWithPagination() throws Exception {
        create("Electronics");
        create("Home Garden");

        mockMvc.perform(get(CATEGORIES).param("pageNumber", "0").param("pageSize", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.lastPage").value(false));
    }

    @Test
    void rejectsInvalidCategoryNameWithFieldError() throws Exception {
        mockMvc.perform(post(CATEGORIES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\"ab\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.categoryName").value("Needs to be a minimum of 5 chars"));
    }

    @Test
    void rejectsDuplicateCategoryName() throws Exception {
        create("Electronics");

        mockMvc.perform(post(CATEGORIES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\"Electronics\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatesExistingCategory() throws Exception {
        create("Electronics");

        mockMvc.perform(put(CATEGORIES + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\"Consumer Electronics\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Consumer Electronics"));
    }

    @Test
    void returnsNotFoundForMissingCategory() throws Exception {
        mockMvc.perform(delete("/api/admin/categories/999"))
                .andExpect(status().isNotFound());
    }
}

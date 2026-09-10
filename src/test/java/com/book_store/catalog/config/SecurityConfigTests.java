package com.book_store.catalog.config;

import com.book_store.catalog.dto.BookDto;
import com.book_store.catalog.dto.BookUpsertRequest;
import com.book_store.catalog.service.BookService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = com.book_store.catalog.controller.BooksController.class)
@Import(SecurityConfig.class)
class SecurityConfigTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    @Disabled
    void shouldReturnUnauthorizedForCatalogEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/catalog/books"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required"))
                .andExpect(jsonPath("$.path").value("/catalog/books"));
    }

    @Test
    @Disabled
    void shouldReturnForbiddenForNonAdminOnWriteEndpoint() throws Exception {
        JwtRequestPostProcessor customerJwt = SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

        mockMvc.perform(post("/catalog/books")
                        .with(customerJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"bk_1\",\"title\":\"Demo\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access denied"))
                .andExpect(jsonPath("$.path").value("/catalog/books"));
    }

    @Test
    void shouldAllowAdminOnWriteEndpoint() throws Exception {
        JwtRequestPostProcessor adminJwt = SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));

        BookDto created = new BookDto();
        created.setId("bk_1");
        created.setTitle("Demo");
        when(bookService.createBook(any(BookUpsertRequest.class), any())).thenReturn(created);

        MockMultipartFile bookPart = new MockMultipartFile(
                "book",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                "{\"title\":\"Demo\",\"author\":\"Author\",\"genre\":\"Fiction\",\"format\":\"Paperback\",\"pricing\":{\"currency\":\"INR\",\"salePrice\":100,\"listPrice\":120},\"rating\":{\"average\":4.8,\"count\":12}}".getBytes()
        );
        MockMultipartFile imagePart = new MockMultipartFile("images", "cover.jpg", "image/jpeg", "hello".getBytes());

        mockMvc.perform(multipart("/catalog/books")
                        .with(adminJwt)
                        .file(bookPart)
                        .file(imagePart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("bk_1"))
                .andExpect(jsonPath("$.title").value("Demo"));
    }
}


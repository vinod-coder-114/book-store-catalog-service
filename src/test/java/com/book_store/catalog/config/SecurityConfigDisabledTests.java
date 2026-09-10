package com.book_store.catalog.config;

import com.book_store.catalog.dto.BookDto;
import com.book_store.catalog.dto.BookUpsertRequest;
import com.book_store.catalog.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = com.book_store.catalog.controller.BooksController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "catalog.security.enabled=false")
class SecurityConfigDisabledTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void shouldAllowReadWithoutTokenWhenSecurityDisabled() throws Exception {
        BookDto dto = new BookDto();
        dto.setId("bk_1");
        dto.setTitle("Demo");
        when(bookService.getAllBooks()).thenReturn(List.of(dto));

        mockMvc.perform(get("/catalog/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("bk_1"))
                .andExpect(jsonPath("$[0].title").value("Demo"));
    }

    @Test
    void shouldAllowWriteWithoutTokenWhenSecurityDisabled() throws Exception {
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

        mockMvc.perform(multipart("/catalog/books")
                        .file(bookPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("bk_1"))
                .andExpect(jsonPath("$.title").value("Demo"));
    }
}


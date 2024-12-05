package br.edu.utfpr.bankapi.controller;

import br.edu.utfpr.bankapi.dto.AccountDTO;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void getByNumberShouldReturnAccount() throws Exception {
        Account account = new Account("Armarildo Mansur", 12345, 1000, 0);
        BDDMockito.given(accountService.getByNumber(12345L)).willReturn(Optional.of(account));

        mockMvc.perform(get("/account/12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Armarildo Mansur"))
                .andExpect(jsonPath("$.number").value(12345))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void getAllShouldReturnListOfAccounts() throws Exception {
        List<Account> accounts = List.of(
                new Account("Armarildo Mansur", 12345, 1000, 0),
                new Account("Creuza Beleuza", 67890, 2000, 0)
        );
        BDDMockito.given(accountService.getAll()).willReturn(accounts);

        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Armarildo Mansur"))
                .andExpect(jsonPath("$[1].name").value("Creuza Beleuza"));
    }

    @Test
    void saveShouldCreateAccount() throws Exception {
        AccountDTO accountDTO = new AccountDTO("Armarildo Mansur", 12345L, 1000, 0);
        Account account = new Account("Armarildo Mansur", 12345, 1000, 0);
        BDDMockito.given(accountService.save(accountDTO)).willReturn(account);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(accountDTO);

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Armarildo Mansur"))
                .andExpect(jsonPath("$.number").value(12345))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void updateShouldUpdateAccount() throws Exception {
        AccountDTO accountDTO = new AccountDTO("Armarildo Mansur", 12345L, 1500, 0);
        Account account = new Account("Armarildo Mansur", 12345, 1500, 0);
        BDDMockito.given(accountService.update(1L, accountDTO)).willReturn(account);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(accountDTO);

        mockMvc.perform(put("/account/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Armarildo Mansur"))
                .andExpect(jsonPath("$.number").value(12345))
                .andExpect(jsonPath("$.balance").value(1500));
    }
}
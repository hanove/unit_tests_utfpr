package br.edu.utfpr.bankapi.controller;

import br.edu.utfpr.bankapi.dto.TransferDTO;
import br.edu.utfpr.bankapi.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(TransactionController.class)
public class TransferControllerTest {

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    //Esse teste está falhando pois o controller está retornando 201 para uma requisição inválida
    @Test
    void deveriaRetornar400ParaRequisicaoInvalida() throws Exception {
        var json = "{}";

        var res = mockMvc.perform(post("/transaction/transfer")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        Assertions.assertEquals(400, res.getStatus());
    }

    @Test
    void deveriaRetornar201ParaRequisicaoValida() throws Exception {
        var transferDTO = new TransferDTO(12345, 67890, 1000);
        var objectMapper = new ObjectMapper();
        var json = objectMapper.writeValueAsString(transferDTO);

        BDDMockito.given(transactionService.transfer(transferDTO)).willReturn(null); // Replace null with the correct return type

        var res = mockMvc.perform(post("/transaction/transfer")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        Assertions.assertEquals(201, res.getStatus());
    }
}
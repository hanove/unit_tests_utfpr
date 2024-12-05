package br.edu.utfpr.bankapi.validations;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.utfpr.bankapi.exception.NotFoundException;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
public class AvaliableAccountValiadationTest {
    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    AvailableAccountValidation validation;

    @Mock
    Account account;

    @Test
    void deveriaEncontrarUmaConta() throws NotFoundException {
        // ARRANGE
        long number = 12345;

        // Simulando o comportamento do Repositorio (AccountRepository)
        BDDMockito
                .given(accountRepository.getByNumber(number))
                .willReturn(Optional.of(account));

        // ACT + ASSERT
        Assertions.assertDoesNotThrow(() -> validation.validate(number));
        // não ocorra uma exception...
    }

    @Test
    void deveriaLancarNotFoundExceptionParaContaInexistente() {
        // ARRANGE
        long number = 12345;

        // Simulando o comportamento do Repositorio (AccountRepository)
        BDDMockito
                .given(accountRepository.getByNumber(number))
                .willReturn(Optional.empty());

        // ACT + ASSERT
        Assertions.assertThrows(NotFoundException.class, () -> validation.validate(number));
    }

    @Test
    void deveriaLancarNotFoundExceptionParaNumeroDeContaInvalido() {
        // ARRANGE
        long invalidNumber = -1;

        // Simulando o comportamento do Repositorio (AccountRepository)
        BDDMockito
                .given(accountRepository.getByNumber(invalidNumber))
                .willReturn(Optional.empty());

        // ACT + ASSERT
        Assertions.assertThrows(NotFoundException.class, () -> validation.validate(invalidNumber));
    }
}

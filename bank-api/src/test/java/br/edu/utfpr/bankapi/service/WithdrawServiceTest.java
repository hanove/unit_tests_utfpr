package br.edu.utfpr.bankapi.service;

import br.edu.utfpr.bankapi.dto.WithdrawDTO;
import br.edu.utfpr.bankapi.exception.NotFoundException;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.model.Transaction;
import br.edu.utfpr.bankapi.model.TransactionType;
import br.edu.utfpr.bankapi.repository.AccountRepository;
import br.edu.utfpr.bankapi.repository.TransactionRepository;
import br.edu.utfpr.bankapi.validations.AvailableAccountValidation;
import br.edu.utfpr.bankapi.validations.AvailableBalanceValidation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WithdrawServiceTest {
    @Mock
    AccountRepository accountRepository;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    AvailableAccountValidation accountValidation;

    @Mock
    AvailableBalanceValidation availableBalanceValidation;

    @InjectMocks
    TransactionService transactionService; // Object to be tested

    @Captor
    ArgumentCaptor<Transaction> transactionCaptor;

    @Test
    void deveriaSacar() throws NotFoundException {
        // ARRANGE
        double saldoInicial = 2000.00;
        var withdrawDTO = new WithdrawDTO(12345, 1000);
        var sourceAccount = new Account("John Smith", 12345, saldoInicial, 0);

        BDDMockito.given(accountValidation.validate(withdrawDTO.sourceAccountNumber()))
                .willReturn(sourceAccount);
        BDDMockito.willDoNothing().given(availableBalanceValidation).validate(BDDMockito.any());

        // ACT
        transactionService.withdraw(withdrawDTO);

        // ASSERT
        BDDMockito.then(transactionRepository).should().save(BDDMockito.any());
        BDDMockito.then(transactionRepository).should().save(transactionCaptor.capture());
        Transaction transacaoSalva = transactionCaptor.getValue();

        Assertions.assertEquals(sourceAccount, transacaoSalva.getSourceAccount());
        Assertions.assertEquals(withdrawDTO.amount(), transacaoSalva.getAmount());
        Assertions.assertEquals(TransactionType.WITHDRAW, transacaoSalva.getType());
        Assertions.assertEquals(saldoInicial - withdrawDTO.amount(), transacaoSalva.getSourceAccount().getBalance());
    }

    @Test
    void deveriaLancarNotFoundExceptionQuandoContaNaoExisteParaSaque() throws NotFoundException {
        var withdrawDTO = new WithdrawDTO(99999, 1000);

        BDDMockito.given(accountValidation.validate(withdrawDTO.sourceAccountNumber()))
                .willThrow(new NotFoundException("Account not found"));

        Assertions.assertThrows(NotFoundException.class, () -> {
            transactionService.withdraw(withdrawDTO);
        });
    }

    @Test
    void deveriaLancarExcecaoQuandoSaldoInsuficienteParaSaque() throws NotFoundException {
        var withdrawDTO = new WithdrawDTO(12345, 1000);
        var sourceAccount = new Account("John Smith", 12345, 500, 0);

        BDDMockito.given(accountValidation.validate(withdrawDTO.sourceAccountNumber()))
                .willReturn(sourceAccount);

        BDDMockito.willThrow(new IllegalArgumentException("Insufficient balance"))
                .given(availableBalanceValidation).validate(BDDMockito.any());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            transactionService.withdraw(withdrawDTO);
        });
    }
}
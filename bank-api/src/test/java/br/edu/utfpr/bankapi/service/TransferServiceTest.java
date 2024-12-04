package br.edu.utfpr.bankapi.service;

import br.edu.utfpr.bankapi.dto.TransferDTO;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {
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
    void deveriaTransferir() throws NotFoundException {
        // ARRANGE
        double saldoInicialSource = 2000.00;
        double saldoInicialReceiver = 500.00;
        var transferDTO = new TransferDTO(12345, 67890, 1000);
        var sourceAccount = new Account("John Smith", 12345, saldoInicialSource, 0);
        var receiverAccount = new Account("Jane Doe", 67890, saldoInicialReceiver, 0);

        BDDMockito.given(accountValidation.validate(transferDTO.sourceAccountNumber()))
                .willReturn(sourceAccount);
        BDDMockito.given(accountValidation.validate(transferDTO.receiverAccountNumber()))
                .willReturn(receiverAccount);
        BDDMockito.willDoNothing().given(availableBalanceValidation).validate(BDDMockito.any());

        // ACT
        transactionService.transfer(transferDTO);

        // ASSERT
        BDDMockito.then(transactionRepository).should().save(BDDMockito.any());
        BDDMockito.then(transactionRepository).should().save(transactionCaptor.capture());
        Transaction transacaoSalva = transactionCaptor.getValue();

        Assertions.assertEquals(sourceAccount, transacaoSalva.getSourceAccount());
        Assertions.assertEquals(receiverAccount, transacaoSalva.getReceiverAccount());
        Assertions.assertEquals(transferDTO.amount(), transacaoSalva.getAmount());
        Assertions.assertEquals(TransactionType.TRANSFER, transacaoSalva.getType());
        Assertions.assertEquals(saldoInicialSource - transferDTO.amount(), transacaoSalva.getSourceAccount().getBalance());
        Assertions.assertEquals(saldoInicialReceiver + transferDTO.amount(), transacaoSalva.getReceiverAccount().getBalance());
    }

    @Test
    void deveriaLancarNotFoundExceptionQuandoContaNaoExisteParaTransferencia() throws NotFoundException {
        var transferDTO = new TransferDTO(99999, 67890, 1000);

        BDDMockito.given(accountValidation.validate(transferDTO.sourceAccountNumber()))
                .willThrow(new NotFoundException("Source account not found"));

        Assertions.assertThrows(NotFoundException.class, () -> {
            transactionService.transfer(transferDTO);
        });
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void deveriaLancarNotFoundExceptionQuandoContaDestinoNaoExisteParaTransferencia() throws NotFoundException {
        var transferDTO = new TransferDTO(12345, 99999, 1000);

        BDDMockito.given(accountValidation.validate(transferDTO.receiverAccountNumber()))
                .willThrow(new NotFoundException("Receiver account not found"));

        Assertions.assertThrows(NotFoundException.class, () -> {
            transactionService.transfer(transferDTO);
        });
    }

    @Test
    void deveriaLancarExcecaoQuandoSaldoInsuficienteParaTransferencia() throws NotFoundException {
        var transferDTO = new TransferDTO(12345, 67890, 1000);
        var sourceAccount = new Account("John Smith", 12345, 500, 0);
        var receiverAccount = new Account("Jane Doe", 67890, 500, 0);

        BDDMockito.given(accountValidation.validate(transferDTO.sourceAccountNumber()))
                .willReturn(sourceAccount);
        BDDMockito.given(accountValidation.validate(transferDTO.receiverAccountNumber()))
                .willReturn(receiverAccount);

        BDDMockito.willThrow(new IllegalArgumentException("Insufficient balance"))
                .given(availableBalanceValidation).validate(BDDMockito.any());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            transactionService.transfer(transferDTO);
        });
    }

}
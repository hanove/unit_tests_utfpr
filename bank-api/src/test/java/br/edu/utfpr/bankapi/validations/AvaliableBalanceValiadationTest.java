package br.edu.utfpr.bankapi.validations;
import br.edu.utfpr.bankapi.exception.WithoutBalanceException;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.model.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AvaliableBalanceValiadationTest {

    private final AvailableBalanceValidation validation = new AvailableBalanceValidation();

    @Test
    void shouldNotThrowExceptionWhenBalanceIsSufficient() {
        // Arrange
        Account sourceAccount = Mockito.mock(Account.class);
        Mockito.when(sourceAccount.getBalanceWithLimit()).thenReturn(2000.0);
        Transaction transaction = new Transaction();
        transaction.setSourceAccount(sourceAccount);
        transaction.setAmount(1000.0);

        // Assert
        Assertions.assertDoesNotThrow(() -> validation.validate(transaction));
    }

    @Test
    void shouldThrowWithoutBalanceExceptionWhenBalanceIsInsufficient() {
        // Arrange
        Account sourceAccount = Mockito.mock(Account.class);
        Mockito.when(sourceAccount.getBalanceWithLimit()).thenReturn(500.0);
        Transaction transaction = new Transaction();
        transaction.setSourceAccount(sourceAccount);
        transaction.setAmount(1000.0);

        // Assert
        Assertions.assertThrows(WithoutBalanceException.class, () -> validation.validate(transaction));
    }
}
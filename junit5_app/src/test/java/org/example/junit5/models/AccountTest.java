package org.example.junit5.models;

import org.example.junit5.exceptions.NotEnoughMoneyException;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountTest {
    Account account;

    @BeforeEach
    public void initMethodTest() {
        this.account = new Account("Andres", new BigDecimal("1000.12345"));
        System.out.println("Initializing method");
    }

    @AfterEach
    public void tearDown() {
        System.out.println("End of the method");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Initializing test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("End of the test");
    }

    @Test
    @DisplayName("Testing account name")
    public void testAccountName() {
        //account.setName("Andres");
        String expected = "Andres";
        String actual = this.account.getName();
        assertNotNull(actual, () -> "The account can not be null");
        assertEquals(expected, actual, () ->"The account name is no as expected: expected:d " + expected + " actual: " + actual);
        assertTrue(actual.equals("Andres"), () -> "Name of the expected account should be equal to the actual");
    }

    @Test
    @DisplayName("Testing account amount, account not null, greater than 0, and expected value")
    public void testAccountAmount() {
        assertNotNull(account.getAmount());
        assertEquals(1000.12345, account.getAmount().doubleValue());
        assertFalse(account.getAmount().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    public void testAccountReference() {
        Account account = new Account("John doe", new BigDecimal("8900.997"));
        Account account2 = new Account("John doe", new BigDecimal("8900.997"));

        //assertNotEquals(account,account2);
        assertEquals(account, account2);
    }

    @Test
    public void testDebitAccount() {
        Account account = new Account("Andres", new BigDecimal("999.999"));
        account.debit(new BigDecimal("100"));

        assertNotNull(account.getAmount());
        assertEquals(899, account.getAmount().intValue());
        assertEquals("899.999", account.getAmount().toPlainString());
    }

    @Test
    public void testCreditAccount() {
        Account account = new Account("Andres", new BigDecimal("999.999"));
        account.credit(new BigDecimal("100"));

        assertNotNull(account.getAmount());
        assertEquals(1099, account.getAmount().intValue());
        assertEquals("1099.999", account.getAmount().toPlainString());
    }

    @Test
    public void testNotEnoughMoneyExceptionAccount() {
        Exception exception = assertThrows(NotEnoughMoneyException.class, () -> {
            account.debit(new BigDecimal(1500));
        });

        String actual = exception.getMessage();
        String expected = "Not enough money";

        assertEquals(expected, actual);
    }

    @Test
    void TestTransferMoneyAccounts() {
        Account account = new Account("John ", new BigDecimal("2500"));
        Account account2 = new Account("Andres ", new BigDecimal("1500"));

        Bank bank = new Bank();
        bank.setName("State Bank");
        bank.transfer(account2, account, new BigDecimal(500));

        assertEquals("1000", account2.getAmount().toPlainString());
        assertEquals("3000", account.getAmount().toPlainString());
    }

    @Test
    @Disabled
    @DisplayName("Testing relationship between bank accounts")
    void TestRelationBankAccounts() {
        fail();
        Account account = new Account("John", new BigDecimal("2500"));
        Account account2 = new Account("Andres", new BigDecimal("1500"));

        Bank bank = new Bank();
        bank.addAccount(account);
        bank.addAccount(account2);

        bank.setName("State Bank");
        bank.transfer(account2, account, new BigDecimal(500));

        assertAll(() -> assertEquals("1000", account2.getAmount().toPlainString(), () -> "The amount of the account2 is not the expected"),
                () -> assertEquals("3000", account.getAmount().toPlainString(), () -> "The amount of the account is not the expected"),
                () -> assertEquals(2, bank.getAccounts().size(), () -> "The bank does not have the expected accounts"),
                () -> assertEquals("State Bank", account.getBank().getName()),
                () -> assertEquals("Andres", bank.getAccounts().stream()
                        .filter(ac -> ac.getName().equals("Andres"))
                        .findFirst()
                        .get()
                        .getName()),
                () -> assertTrue(bank.getAccounts().stream()
                        .anyMatch(ac -> ac.getName().equals("Andres")))
        );
    }
}
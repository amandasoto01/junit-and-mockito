package org.example.junit5.models;

import org.example.junit5.exceptions.NotEnoughMoneyException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

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

    @Nested
    @DisplayName("Testing account attributes")
    class AccountNameAndAmountTest {
        @Test
        @DisplayName("Test account name")
        public void testAccountName() {
            //account.setName("Andres");
            String expected = "Andres";
            String actual = account.getName();
            assertNotNull(actual, () -> "The account can not be null");
            assertEquals(expected, actual, () ->"The account name is no as expected: expected:d " + expected + " actual: " + actual);
            assertTrue(actual.equals("Andres"), () -> "Name of the expected account should be equal to the actual");
        }

        @Test
        @DisplayName("amount, account not null, greater than 0, and expected value")
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
    }

    @Nested
    class AccountOperationTest {
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
        void testTransferMoneyAccounts() {
            Account account = new Account("John ", new BigDecimal("2500"));
            Account account2 = new Account("Andres ", new BigDecimal("1500"));

            Bank bank = new Bank();
            bank.setName("State Bank");
            bank.transfer(account2, account, new BigDecimal(500));

            assertEquals("1000", account2.getAmount().toPlainString());
            assertEquals("3000", account.getAmount().toPlainString());
        }
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
    //@Disabled
    @DisplayName("Test relationship between bank accounts")
    void testRelationBankAccounts() {
        //fail();
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

    @Nested
    class OperationSystemTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testJustWindows() {
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testJustLinuxMac() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNotWindows() {
        }
    }

    @Nested
    class JavaVersionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void justJDK8() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_11)
        void justJDK11(){
        }

        @Test
        @DisabledOnJre(JRE.JAVA_11)
        void testNoJDK11(){
        }
    }

    @Nested
    class SystemPropertiesTest {
        @Test
        void printSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach( (k, v) -> System.out.println(k +": "+ v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches= "11")
        void testJavaVersion(){
        }

        @Test
        @DisabledIfSystemProperty(named="os.arch", matches=".*32.*")
        void testJust64() {
        }

        @Test
        @EnabledIfSystemProperty(named="os.arch", matches=".*32.*")
        void testNot64() {
        }

        @Test
        @EnabledIfSystemProperty(named="user.name", matches="amanda")
        void testUsername() {
        }

        @Test
        @EnabledIfSystemProperty(named="ENV", matches="dev")
        void testDev() {
        }
    }

    class EnvironmentVariablesTest {
        @Test
        void printEnvironmentVariables() {
            Map<String, String> getEnv = System.getenv();
            getEnv.forEach((k,v)->System.out.println(k + ": "+v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named="JAVA_HOME", matches = ".*jdk-15.0.1.*")
        void testJavaHome() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named="NUMBER_OF_PROCESSORS", matches="8")
        void testProcessor() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named="ENVIRONMENT", matches = "dev")
        void testEnv() {
        }

        @Test
        @DisabledIfEnvironmentVariable(named="ENVIRONMENT", matches = "prod")
        void testDisabledProdEnv() {
        }
    }

    @Test
    @DisplayName("Test account amount dev")
    public void testAccountAmountDev() {
        boolean isDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(isDev);
        assertNotNull(account.getAmount());
        assertEquals(1000.12345, account.getAmount().doubleValue());
        assertFalse(account.getAmount().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Test account amount dev 2")
    public void testAccountAmountDev2() {
        boolean isDev = "dev".equals(System.getProperty("ENV"));

        assumingThat(isDev, () -> {
            assertNotNull(account.getAmount());
            assertEquals(1000.12345, account.getAmount().doubleValue());
        });

        assertFalse(account.getAmount().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @DisplayName("Testing debit account repetition!")
    @RepeatedTest(value = 5, name = "{displayName} - Repetition number {currentRepetition} of {totalRepetitions}")
    public void testDebitAccountRepeated(RepetitionInfo info) {

        if(info.getCurrentRepetition() == 3) {
            System.out.println("We're in repetition " + info.getCurrentRepetition());
        }

        Account account = new Account("Andres", new BigDecimal("999.999"));
        account.debit(new BigDecimal("100"));

        assertNotNull(account.getAmount());
        assertEquals(899, account.getAmount().intValue());
        assertEquals("899.999", account.getAmount().toPlainString());
    }

    class TestParameterizedTest {
        @ParameterizedTest(name = "number of repetition {index} executing with value {0} - {argumentsWithNames}" )
        @ValueSource(strings = {"100","200", "500", "700", "1000.12345"})
        public void testDebitAccountValueSource(String amount) {
            account.debit(new BigDecimal(amount));

            assertNotNull(account.getAmount());
            assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "number of repetition {index} executing with value {0} - {argumentsWithNames}" )
        @CsvSource({"1,100","2,200", "3,500", "4,700", "5,1000.12345"})
        public void testDebitAccountCsvSource(String index, String amount) {
            System.out.println(index + " -> " + amount);
            account.debit(new BigDecimal(amount));

            assertNotNull(account.getAmount());
            assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "number of repetition {index} executing with value {0} - {argumentsWithNames}" )
        @CsvFileSource (resources = "/data.csv")
        public void testDebitAccountCsvFileSource(String amount) {
            account.debit(new BigDecimal(amount));

            assertNotNull(account.getAmount());
            assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "number of repetition {index} executing with value {0} - {argumentsWithNames}" )
        @CsvSource({"200,100, John, Andres","250,200, Pepe, Pepe", "300,300,maria, Maria","510,500, Pepa, Pepa", "750,700, Lucas, Lucas", "1000.12345,1000.12345, Cata, Cata"})
        public void testDebitAccountCsvSource2(String index, String amount, String expected, String actual) {
            System.out.println(index + " -> " + amount);
            account.setAmount(new BigDecimal(index));
            account.debit(new BigDecimal(amount));
            account.setName(actual);

            assertNotNull(account.getAmount());
            assertNotNull(account.getName());
            assertEquals(expected,actual);
            assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "number of repetition {index} executing with value {0} - {argumentsWithNames}" )
        @CsvFileSource (resources = "/data2.csv")
        public void testDebitAccountCsvFileSource2(String index, String amount, String expected, String actual) {
            account.setAmount(new BigDecimal(index));
            account.debit(new BigDecimal(amount));
            account.setName(actual);

            assertNotNull(account.getAmount());
            assertNotNull(account.getName());
            assertEquals(expected,actual);
            assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @ParameterizedTest(name = "number of repetition {index} executing with value {0} - {argumentsWithNames}" )
    @MethodSource("amountList")
    public void testDebitAccountMethodSource(String amount) {
        account.debit(new BigDecimal(amount));

        assertNotNull(account.getAmount());
        assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    static List<String> amountList() {
        return Arrays.asList("100","200", "500", "700", "1000.12345");
    }
}
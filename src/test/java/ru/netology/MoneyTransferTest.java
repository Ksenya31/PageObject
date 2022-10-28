package ru.netology;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.TransactionPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.getFirstCardNumber;
import static ru.netology.data.DataHelper.getSecondCardNumber;


public class MoneyTransferTest {

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
        val loginPage = new LoginPage();
        val authInfo = DataHelper.getAuthInfo();
        val verificationPage = loginPage.validLogin(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        val cardBalance = verificationPage.validVerify(verificationCode);
    }

    @Test
    public void shouldTransferFromFirstToSecond() {
        int amount = 5000;
        val cardBalance = new DashboardPage();
        val firstCardBalanceStart = cardBalance.getFirstCardBalance();
        val secondCardBalanceStart = cardBalance.getSecondCardBalance();
        cardBalance.transferButtonFirstToSecond();
        val transactionPage = new TransactionPage();
        transactionPage.transferMoney(amount, getFirstCardNumber());
        val firstCardBalanceFinish = firstCardBalanceStart - amount;
        val secondCardBalanceFinish = secondCardBalanceStart + amount;
        assertEquals(firstCardBalanceFinish, cardBalance.getFirstCardBalance());
        assertEquals(secondCardBalanceFinish, cardBalance.getSecondCardBalance());
    }

    @Test
    public void shouldTransferFromSecondToFirst() {
        int amount = 4300;
        val cardBalance = new DashboardPage();
        val firstCardBalanceStart = cardBalance.getFirstCardBalance();
        val secondCardBalanceStart = cardBalance.getSecondCardBalance();
        cardBalance.transferButtonSecondToFirst();
        val transactionPage = new TransactionPage();
        transactionPage.transferMoney(amount, getSecondCardNumber());
        val firstCardBalanceFinish = firstCardBalanceStart + amount;
        val secondCardBalanceFinish = secondCardBalanceStart - amount;
        assertEquals(firstCardBalanceFinish, cardBalance.getFirstCardBalance());
        assertEquals(secondCardBalanceFinish, cardBalance.getSecondCardBalance());
    }

    @Test
    public void shouldTransferMoreBalance() {
        int amount = 61739;
        val cardBalance = new DashboardPage();
        val firstCardBalanceStart = cardBalance.getFirstCardBalance();
        val secondCardBalanceStart = cardBalance.getSecondCardBalance();
        val transactionPage = new TransactionPage();
        transactionPage.transferMoney(amount, getFirstCardNumber());
        transactionPage.errorLimit();
    }
}
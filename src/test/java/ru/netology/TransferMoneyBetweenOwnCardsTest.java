package ru.netology;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

class TransferMoneyBetweenOwnCardsTest {

    @BeforeAll
    public static void loginToPersonalAccount() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getUserAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor();
        verificationPage.validVerify(verificationCode);
    }

    @AfterEach
    public void cardBalancing() {
        var dashboardPage = new DashboardPage();
        var firstCardId = DataHelper.getFirstCardId();
        var balanceFirstCard = dashboardPage.getCardBalance(firstCardId);
        var secondCardId = DataHelper.getSecondCardId();
        var balanceSecondCard = dashboardPage.getCardBalance(secondCardId);
        int amountTransfer;
        if (balanceFirstCard > balanceSecondCard) {
            amountTransfer = (balanceFirstCard - balanceSecondCard) / 2;
            var replenishmentPage = dashboardPage.transfer(secondCardId);
            var transferInfo = DataHelper.setSecondCardTransferInfo(amountTransfer);
            replenishmentPage.transferBetweenOwnCards(transferInfo);
        }
        if (balanceFirstCard < balanceSecondCard) {
            amountTransfer = (balanceSecondCard - balanceFirstCard) / 2;
            var replenishmentPage = dashboardPage.transfer(firstCardId);
            var transferInfo = DataHelper.setFirstCardTransferInfo(amountTransfer);
            replenishmentPage.transferBetweenOwnCards(transferInfo);
        }
    }

    //Позитивные проверки:
    @Test   //Перевод со второй карты на первую
    @DisplayName("Transfer money from the second card to the first card")
    public void shouldTransferFromSecondToFirst() {
        var dashboardPage = new DashboardPage();
        var firstCardId = DataHelper.getFirstCardId();
        var initialBalanceFirstCard = dashboardPage.getCardBalance(firstCardId);
        var secondCardId = DataHelper.getSecondCardId();
        var initialBalanceSecondCard = dashboardPage.getCardBalance(secondCardId);
        var replenishmentPage = dashboardPage.transfer(firstCardId);
        var transferInfo = DataHelper.getFirstCardTransferInfoPositive();
        replenishmentPage.transferBetweenOwnCards(transferInfo);
        var finalBalanceFirstCard = dashboardPage.getCardBalance(firstCardId);
        var finalBalanceSecondCard = dashboardPage.getCardBalance(secondCardId);
        assertEquals(transferInfo.getAmount(), finalBalanceFirstCard - initialBalanceFirstCard);
        assertEquals(transferInfo.getAmount(), initialBalanceSecondCard - finalBalanceSecondCard);
    }

    @Test   //Перевод с первой карты на вторую
    @DisplayName("Transfer money from the first card to the second card")
    public void shouldTransferFromFirstToSecond() {
        var dashboardPage = new DashboardPage();
        var firstCardId = DataHelper.getFirstCardId();
        var initialBalanceFirstCard = dashboardPage.getCardBalance(firstCardId);
        var secondCardId = DataHelper.getSecondCardId();
        var initialBalanceSecondCard = dashboardPage.getCardBalance(secondCardId);
        var replenishmentPage = dashboardPage.transfer(secondCardId);
        var transferInfo = DataHelper.getSecondCardTransferInfoPositive();
        replenishmentPage.transferBetweenOwnCards(transferInfo);
        var finalBalanceFirstCard = dashboardPage.getCardBalance(firstCardId);
        var finalBalanceSecondCard = dashboardPage.getCardBalance(secondCardId);
        assertEquals(transferInfo.getAmount(), initialBalanceFirstCard - finalBalanceFirstCard);
        assertEquals(transferInfo.getAmount(), finalBalanceSecondCard - initialBalanceSecondCard);
    }

    //Негативные проверки:
        @Test   //Попытка перевода с первой карты на вторую с суммой перевода превышающей баланс первой карты
    @DisplayName("Transfer money from the first card to the second " +
            "with the transfer amount exceeding the balance of the first card")
    public void shouldTransferFromFirstToSecondNegativeAmount() throws InterruptedException {
        var dashboardPage = new DashboardPage();
        var firstCardId = DataHelper.getFirstCardId();
        var initialBalanceFirstCard = dashboardPage.getCardBalance(firstCardId);
        var secondCardId = DataHelper.getSecondCardId();
        var initialBalanceSecondCard = dashboardPage.getCardBalance(secondCardId);
        var replenishmentPage = dashboardPage.transfer(secondCardId);
        var transferInfo = DataHelper.getSecondCardTransferInfoNegative();
        replenishmentPage.transferBetweenOwnCards(transferInfo);
        var finalBalanceFirstCard = dashboardPage.getCardBalance(firstCardId);
        var finalBalanceSecondCard = dashboardPage.getCardBalance(secondCardId);
        assertEquals(initialBalanceFirstCard, finalBalanceFirstCard,
                "Изменился баланс первой карты");
        assertEquals(initialBalanceSecondCard, finalBalanceSecondCard,
                "Изменился баланс второй карты");
    }
}
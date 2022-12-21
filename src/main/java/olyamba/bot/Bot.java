package olyamba.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.SendResponse;
import olyamba.shop.CustomerService;
import olyamba.shop.ProductsService;

import java.util.Map;

public class Bot {
    private final TelegramBot bot = new TelegramBot(System.getenv("BOT_TOKEN"));
    private static boolean waitingForMail = false;
    private static boolean ordering = false;

    public void serve() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::process);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void process(Update update) {
        Message message = update.message();
        CallbackQuery callbackQuery = update.callbackQuery();
        if (message != null) {
            long chatId = update.message().chat().id();
            if (waitingForMail) {
                CustomerService.addCustomer(message.from().username(), message.text().split(" ")[0], message.text().split(" ")[1]);
                waitingForMail = false;
                SendResponse response = bot.execute(new SendMessage(chatId, "Регистрирую (ﾉ´ з `)ノ"));
            }
            SendResponse response = bot.execute(new SendMessage(chatId, "Добро пожаловать в наш магазин! Что вы хотите сделать?(/▽＼*)｡o○♡").replyMarkup(getStartKeyboard()));
        } else if (callbackQuery != null) {
            answerForCallback(callbackQuery);
        }
    }

    private void answerForCallback(CallbackQuery callbackQuery) {
        switch (callbackQuery.data()) {
            case "register" -> {
                waitingForMail = true;
                SendResponse response = bot.execute(new SendMessage(callbackQuery.message().chat().id(), "Введите вашу почту и город через пробел(´• ω •`) ♡"));
            }
            case "order" -> {
                ordering = true;
                SendResponse response = bot.execute(new SendMessage(callbackQuery.message().chat().id(), "Что вы хотите купить?╰(▔∀▔)╯").replyMarkup(getOrderKeyboard()));
            }
            default -> {
                if (callbackQuery.data().split(" ")[0].equals("product")) {
                    String productType = callbackQuery.data().split(" ")[1];
                    if (ProductsService.order(productType)) {
                        SendResponse response = bot.execute(new SendMessage(callbackQuery.message().chat().id(), "Поздравляю с покупкой!ヽ(*・ω・)ﾉ Проверьте ваш почтовый ящик(=`ω´=)"));
                    } else {
                        SendResponse response = bot.execute(new SendMessage(callbackQuery.message().chat().id(), "К сожалению, товар закончился(μ_μ)"));
                    }
                } else {
                    SendResponse response = bot.execute(new SendMessage(callbackQuery.message().chat().id(), "Я не знаю что вы от меня хотите."));
                }
            }
        }
    }

    private InlineKeyboardMarkup getOrderKeyboard() {
        Map<String, String> products = ProductsService.getProducts();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        assert products != null;
        for (Map.Entry<String, String> entry :
                products.entrySet()) {
            inlineKeyboardMarkup.addRow(new InlineKeyboardButton(entry.getKey()).callbackData("product " + entry.getValue()));
        }
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup getStartKeyboard() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("Зарегистрироваться😍").callbackData("register"),
                new InlineKeyboardButton("Сделать заказ🐈").callbackData("order"));
    }
}
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
import olyamba.utills.Producer;

import javax.jms.JMSException;
import java.sql.SQLException;
import java.util.Map;

public class Bot {
    private final TelegramBot bot = new TelegramBot(System.getenv("BOT_TOKEN"));
    private static boolean waitingForMail = false;

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
                if (CustomerService.checkCustomer(callbackQuery.from().username())) {
                    SendResponse response = bot.execute(new SendMessage(callbackQuery.message().chat().id(), "Вы уже зарегистрированны в системе(→_→)").replyMarkup(getStartKeyboard()));
                    return;
                }
                waitingForMail = true;
                SendResponse response = bot.execute(new SendMessage(callbackQuery.message().chat().id(), "Введите вашу почту и город через пробел(´• ω •`) ♡"));
            }
            case "about-me" -> {
                if (!CustomerService.checkCustomer(callbackQuery.from().username())) {
                    SendResponse response = bot.execute(new SendMessage(callbackQuery.message().chat().id(), "Вы еще не регистрировалисьᕕ( ᐛ )ᕗ").replyMarkup(getStartKeyboard()));
                    return;
                }
                SendResponse response = bot.execute(new SendMessage(callbackQuery.message().chat().id(), CustomerService.getCustomerInfo(callbackQuery.from().username())).replyMarkup(getStartKeyboard()));
            }
            case "order" -> {
                if (!CustomerService.checkCustomer(callbackQuery.from().username())) {
                    SendResponse response = bot.execute(new SendMessage(callbackQuery.message().chat().id(), "Вы еще не регистрировалисьᕕ( ᐛ )ᕗ").replyMarkup(getStartKeyboard()));
                    return;
                }
                SendResponse response = bot.execute(new SendMessage(callbackQuery.message().chat().id(), "Что вы хотите купить?╰(▔∀▔)╯").replyMarkup(getOrderKeyboard()));
            }
            case "delete" -> {
                if (!CustomerService.checkCustomer(callbackQuery.from().username())) {
                    SendResponse response = bot.execute(new SendMessage(callbackQuery.message().chat().id(), "Вы еще не регистрировались, мне нечего удалятьヽ(°〇°)ﾉ").replyMarkup(getStartKeyboard()));
                } else {
                    CustomerService.deleteCustomer(callbackQuery.from().username());
                    SendResponse response = bot.execute(new SendMessage(callbackQuery.message().chat().id(), "Удаляю┐('～`;)┌").replyMarkup(getStartKeyboard()));
                }
            }
            default -> {
                if (callbackQuery.data().split(" ")[0].equals("product")) {
                    String productType = callbackQuery.data().split(" ")[1];
                    if (ProductsService.order(productType)) {
                        try {
                            Producer.sendMessage(productType + " " + callbackQuery.from().username() + " " + CustomerService.getCustomerMail(callbackQuery.from().username()) + " " + CustomerService.getCustomerCity(callbackQuery.from().username()));
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
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
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton("Зарегистрироваться😍").callbackData("register"));
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton("Сделать заказ🐈").callbackData("order"));
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton("Удалить информацию обо мне💀").callbackData("delete"));
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton("Посмотреть информацию обо мне🙂").callbackData("about-me"));
        return inlineKeyboardMarkup;
    }
}
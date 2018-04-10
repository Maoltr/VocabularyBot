import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

public class VocabularyBot extends TelegramLongPollingBot {

    private long channelID;
    private int step = 0;
    private static String user;
    private static String password;
    private static String url;
    private static String driver;
    private static Connection connection;

    public static void main(String[] args) {
        connect();
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new VocabularyBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "@MarketingDictionaryBot ";
    }

    @Override
    public String getBotToken() {
        return "564176694:AAG-LOs3P29X8apT0w2PP3-mBjrDy40AgAU";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        Message channelPost = update.getChannelPost();
        if (channelPost != null) {
            System.out.println("Добавляем пост шаг 1");
            addPost(channelPost);
            channelID = channelPost.getChatId();
            System.out.println(channelID);
        }
        if (message != null && message.hasText()) {
            switch (step) {
                case 0:
                    sendMsg(message, "Привет! Я — бот-словарь маркетолога от компании IN-top marketing(in-top.pro)" +
                            " и знаю очень много маркетинговых терминов. Попробуем? " +
                            "Для начало введи термин. ");
                    step = 1;
                    break;
                case 1:
                    search(message);
                    end(message);
                    break;
            }
        }
    }

    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());

        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //this method give word to user
    private void search(Message message) {
        String result = "Упс:( Термин не найден. Пошел изучать вопрос! Попробуй найти этот термин завтра или перефразировать запрос";

        Word word = new Word(message.getText(), connection);

        LinkedList<String[]> map = word.getResults();

        if (map.size() == 0 || map == null) {
            sendMsg(message, result);
            step = 1;

        } else if (map.size() == 1) {
            result = map.get(0)[0] + "\n" + "\n" + map.get(0)[1];
            sendMsg(message, check(result));
        } else {
            result = "Было найдено " + map.size() + " результатов:";
            if (map.size() < 5) {
                sendMsg(message, result);
                for (int i = 0; i < map.size(); i++) {
                    result = map.get(i)[0] + "\n" + map.get(i)[1];
                    sendMsg(message, check(result));
                }
            } else {
                sendMsg(message, "Уточните пожалуйста запрос, так как найдено слишком много совпадений");
            }
        }
        step = 1;
    }

    //this method output end message to user
    private void end(Message message) {
        sendMsg(message, "Кстати, ты еще не подписан на канал @marketinglikbez? " +
                "Там появляются уведомление о новых терминах. Присоединяйся - @marketinglikbez\n"
                + "Бот сделан по заказу компании in-top marketing. Нужен бот? Обращайтесь - @tretir");
        sendMsg(message, "Для дальнейшего поиска просто введите термин, который хотите найти");
        step = 1;
    }

    //this method add post to database
    private void addPost(Message message) {
        System.out.println("Добавляем пост шаг 2");
        Word.addWord(message.getText(), connection);
    }

// reformat string
    private String check(String text){
        StringBuilder str = new StringBuilder();
        if (text.toCharArray()[0] == '?') {
            if (text.indexOf("Где применяем -") == -1 && text.indexOf("Пример -") == -1) {
                str.append(text.substring(2, text.indexOf("Определение -") - 4));
                str.append("\n");
                str.append(text.substring(text.indexOf("Определение -"), text.length()));
                return str.toString();
                //result = text.substring(2, text.indexOf("Определение -") - 4) + "\n" + text.substring(text.indexOf("Определение -"), text.length());
            } else if (text.indexOf("Где применяем -") == -1) {
                str.append(text.substring(2, text.indexOf("Определение -") - 4));
                str.append("\n");
                str.append( text.substring(text.indexOf("Определение -"),text.indexOf("Пример -") - 3));
                str.append("\n");
                str.append(text.substring(text.indexOf("Пример -"), text.length()));
                return str.toString();
                //result = text.substring(2, text.indexOf("Определение -") - 4) + "\n" + text.substring(text.indexOf("Определение -"),
                 //       text.indexOf("Пример -") - 3) + "\n" + text.substring(text.indexOf("Пример -"), text.length());
            } else if (text.indexOf("Пример -") == -1) {
                str.append(text.substring(2, text.indexOf("Определение -") - 4));
                str.append("\n");
                str.append(text.substring(text.indexOf("Определение -"),text.indexOf("Где применяем -") - 3));
                str.append("\n");
                str.append(text.substring(text.indexOf("Где применяем -", text.length())));
                return str.toString();
                //result = text.substring(2, text.indexOf("Определение -") - 4) + text.substring(text.indexOf("Определение -"),
                 //       text.indexOf("Где применяем -") - 3) + "\n" + text.substring(text.indexOf("Где применяем -"), text.length());
            } else {
                str.append(text.substring(2, text.indexOf("Определение -") - 4));
                str.append("\n");
                str.append(text.substring(text.indexOf("Определение -"),text.indexOf("Где применяем -") - 3));
                str.append("\n");
                str.append(text.substring(text.indexOf("Где применяем -"), text.indexOf("Пример -") - 3));
                str.append(text.substring(text.indexOf("Пример -"), text.length()));
                //result = text.substring(2, text.indexOf("Определение -") - 4) + "\n" + text.substring(text.indexOf("Определение -"),
                 //       text.indexOf("Где применяем -") - 3) + "\n" + text.substring(text.indexOf("Где применяем -"), text.indexOf("Пример -") - 3)
                  //      + text.substring(text.indexOf("Пример -"), text.length());
                return str.toString();
            }
        }   else {
            return text;
        }
    }
    private static void connect() {
        Configs configs = new Configs();
        ArrayList<String> properties = configs.takeProperties();
        user = properties.get(0);
        password = properties.get(1);
        url = properties.get(2);
        driver = properties.get(3);

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
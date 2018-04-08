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
        if(channelPost.hasText()){
            addPost(channelPost);
            channelID = channelPost.getChatId();
            System.out.println(channelID);
        }
        if (message != null && message.hasText()) {
            if (message.getText().equals("/start") || step == 0) {
                sendMsg(message, "Привет! Я — бот-словарь маркетолога от компании IN-top marketing(in-top.pro)" +
                        " и знаю очень много маркетинговых терминов. Попробуем? " +
                        "Для начало вводи команду search и напиши термин. ");
                step = 1;
            } else {
                logic(step, message);
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

    private void logic (int step, Message message){
        switch (step){
            case 1:
                search(message);
                break;
            case 2:
                end(message);
                break;
            default:
                sendMsg(message, find(""));
        }
    }

    //this method give word to user
    private void search (Message message){
        sendMsg(message, find(message.getText()));
    }

    //this method output end message to user
    private void end (Message message){
        sendMsg(message, "Кстати, ты еще не подписан на канал @marketingbiz? " +
                "Там появляются уведомление о новых терминах. Присоединяйся - @marketingbiz\n"
                + "Бот сделан по заказу компании in-top marketing. Нужен бот? Обращайтесь - @mmtretiak");
    }

    //this method add post to database
    private void addPost(Message message){

    }

    private static void connect(){
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

    //this method try to find word in database
    private String find (String str){
        String result = "Упс:( Термин не найден. Пошел изучать вопрос! Попробуй найти этот термин завтра или перефразировать запрос";

        Word word = new Word(str, connection);

        Map<String, String> map = word.getResults();

        if (map.size() == 0){
        return result;
        } else if (map.size() == 1){
            result;
        } else {

        }

}
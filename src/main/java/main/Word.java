package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class Word {
    private Connection connect;
    private LinkedList<String[]> results;

    public Word(String s, Connection connect){
        this.connect = connect;
        results = find(s);
    }

    public static void addWord(String s, Connection connect){
        System.out.println("Добавляем пост шаг 3");
        s = s.substring(0, s.lastIndexOf("Ставим"));
        StringBuilder name = new StringBuilder(s.substring(0, s.indexOf("Определение -")));
        //StringBuilder text = new StringBuilder( s.substring(s.indexOf("Определение - "), s.length()));
        //String name = s.substring(0, s.indexOf("Определение -"));
        String text = s.substring(s.indexOf("Определение - "), s.length());
        StringBuilder str = new StringBuilder();
        if (text.indexOf("Где применяем -") == -1 && text.indexOf("Пример -") == -1){
        } else if (text.indexOf("Где применяем -") == -1){
            str.append(text.substring(0, text.indexOf("Пример -")));
            str.append("\n");
            str.append(text.substring(text.indexOf("Пример -"), text.length()));
            //text = text.substring(0, text.indexOf("Пример -")) + "\n" + text.substring(text.indexOf("Пример -"), text.length());
        } else if (text.indexOf("Пример -") == -1) {
            str.append(text.substring(0, text.indexOf("Где применяем - ")));
            str.append("\n");
            str.append(text.substring(text.indexOf("Где применяем - "), text.length()));
            //text = text.substring(0, text.indexOf("Где применяем - ")) + "\n" + text.substring(text.indexOf("Где применяем - "), text.length());
        } else {
            str.append(text.substring(0, text.indexOf("Где применяем - ")));
            str.append("\n");
            str.append(text.substring(text.indexOf("Где применяем -"), text.indexOf("Пример -")));
            str.append("\n");
            str.append(text.substring(text.indexOf("Пример -"), text.length()));
            //text = text.substring(0, text.indexOf("Где применяем - ")) + "\n" + text.substring(text.indexOf("Где при, text.indexOf("Пример -")) + "\n" + text.substring(text.indexOf("Пример -"), text.length());
        }
        try {
            PreparedStatement statement = connect.prepareStatement("INSERT INTO muzeum00_intop.word (name, text)" +
                    "VALUES (?, ?)");
            statement.setString(1, name.toString());
            statement.setString(2, str.toString());
            statement.execute();
            System.out.println(name + "\n" + text);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private LinkedList<String[]> find(String s){
        LinkedList<String[]> map = new LinkedList<>();
        try {
            PreparedStatement statement = connect.prepareStatement("SELECT * FROM muzeum00_intop.word WHERE name LIKE '%"+s+"%'");
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                String name = rs.getNString("name");
                if (name.toUpperCase().contains(s.toUpperCase())){
                    map.add(new String[]{name, rs.getNString("text")});
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public LinkedList<String[]>  getResults(){
        return results;
    }

}

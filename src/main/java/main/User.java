package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class User {
    private Connection connect;
    private long id;

    User(long id, Connection connection) {
        this.id = id;
        this.connect = connection;
        addUser();
    }

    User(Connection connect){
        this.connect = connect;
    }

    private void addUser() {
        try {
            PreparedStatement statement = connect.prepareStatement("SELECT * FROM muzeum00_intop.users WHERE id=" + id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                PreparedStatement st = connect.prepareStatement("INSERT INTO muzeum00_intop.users (id)" +
                        "VALUES (?)");
                st.setLong(1, id);
                st.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LinkedList<Long> getAllUsers() {
        LinkedList<Long> result = new LinkedList<>();
        try {
            PreparedStatement statement = connect.prepareStatement("SELECT * FROM muzeum00_intop.users");
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                result.add(rs.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}

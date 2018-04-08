import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Word {
    private Connection connect;
    private Map<String, String> results;

    public Word(String s, Connection connect){
        this.connect = connect;
        results = find(s);
    }

    private Map<String, String> find(String s){
        Map<String, String> map = new HashMap<>();
        try {
            PreparedStatement statement = connect.prepareStatement("SELECT * FROM word WHERE name LIKE '%"+s+"'%'");
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                String name = rs.getNString("name");
                if (name.contains(s)){
                    map.put(name, rs.getNString("text"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public Map<String, String> getResults(){
        return results;
    }

}

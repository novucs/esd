package net.novucs.esd;

import java.sql.*;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws SQLException {
        Map<String, String> env = System.getenv();
        String dbHost = env.getOrDefault("DB_HOST", "localhost");
        String dbPort = env.getOrDefault("DB_PORT", "1527");
        String dbUser = env.getOrDefault("DB_USER", "impact");
        String dbPass = env.getOrDefault("DB_PASS", "derbypass");
        Connection con = DriverManager.getConnection(
                String.format("jdbc:derby://%s:%s/esd;create=true", dbHost, dbPort),
                dbUser, dbPass);

        try {
            PreparedStatement createUserTable = con.prepareStatement(
                    "CREATE TABLE person (" +
                            "id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                            "name VARCHAR(255), " +
                            "PRIMARY KEY (id))");
            createUserTable.execute();
        } catch (SQLException ignore) {
        }

        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM person");
        ResultSetMetaData metaData = resultSet.getMetaData();
        int numberOfColumns = metaData.getColumnCount();
        for (int i = 1; i <= numberOfColumns; i++) {
            System.out.print(metaData.getColumnName(i) + "\t");
        }
        System.out.println();

        while (resultSet.next()) {
            for (int i = 1; i <= numberOfColumns; i++)
                System.out.print(resultSet.getObject(i) + "\t");
            System.out.println();
        }
        resultSet.close();
        statement.close();
        con.close();
        System.out.println("Success");
    }
}

package net.novucs.esd;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection con = DriverManager.getConnection(
                "jdbc:derby://localhost:1527/esd;create=true",
                "impact", "derbypass");

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
    }
}

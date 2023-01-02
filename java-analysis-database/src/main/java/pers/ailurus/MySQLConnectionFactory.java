package pers.ailurus;

import com.github.braisdom.objsql.ConnectionFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnectionFactory implements ConnectionFactory {

    @Override
    public Connection getConnection(String dataSourceName) throws SQLException {
        try {
            String url = "jdbc:mysql://localhost:33306/java_analysis";
            String user = "root";
            String password = "123456";
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}

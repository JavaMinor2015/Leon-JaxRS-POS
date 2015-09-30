package nl.stoux.posrs.Util;

import lombok.Getter;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.Connection;

/**
 * Created by Leon Stam on 30-9-2015.
 */
public class DataHandler {

    //TODO: Hardcoding connection details & passwords + checking them into git. Always a great idea.
    private final static String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private final static String USER = "system";
    private final static String PASSWORD = "system";

    @Getter
    private Connection connection;

    public DataHandler() {
        setupConnection();
    }

    public boolean setupConnection() {
        try {
            OracleDataSource ods = new OracleDataSource();
            ods.setURL(JDBC_URL);
            connection = ods.getConnection(USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}

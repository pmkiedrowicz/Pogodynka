package dao;

import dto.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DataDAO {

    private static final String ID = "Id";
    private static final String TEMPERATURE = "Temperature";
    private static final String HUMIDITY = "Humidity";
    private static final String DATE = "DateTime";
    private static final String insertQuery = "INSERT INTO data (Temperature,Humidity,DateTime)VALUES(?,?,?)";
    //zeby embedded dzialala localhost musi byc 4545
    // w normalnym tescie localhost 3306
    private String connectionString = "jdbc:mysql://localhost:4545/%s?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private String login;
    private String password;

    public DataDAO(String login, String password, String database) {
        this.login = login;
        this.password = password;
        connectionString = String.format(connectionString, database);
    }

    public List<Data> getAll() {
        List<Data> result = new ArrayList<>();
        try {
            try (Connection con = DriverManager.getConnection(connectionString, login, password)) {
                try (Statement statement = con.createStatement()) {
                    String tableSql = "SELECT * FROM Data";
                    try (ResultSet resultSet = statement.executeQuery(tableSql)) {
                        while (resultSet.next()) {
                            Data data = Data.builder()
                                    .id(resultSet.getInt("id"))
                                    .temperature(resultSet.getDouble("temperature"))
                                    .humidity(resultSet.getDouble("humidity"))
                                    .dateTime(resultSet.getTimestamp(DATE).toLocalDateTime())
                                    .build();
                            result.add(data);
                        }
                    }
                }
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Data> getRecent7Days() {
        List<Data> result = new ArrayList<>();
        try {
            try (Connection con = DriverManager.getConnection(connectionString, login, password)) {
                try (Statement statement = con.createStatement()) {
                    String tableSql = "SELECT * FROM DATA WHERE DateTime >= DATE(NOW()) - INTERVAL 7 DAY";
                    try (ResultSet resultSet = statement.executeQuery(tableSql)) {
                        while (resultSet.next()) {
                            Data data = Data.builder()
                                    .id(resultSet.getInt("id"))
                                    .temperature(resultSet.getDouble("temperature"))
                                    .humidity(resultSet.getDouble("humidity"))
                                    .dateTime(resultSet.getTimestamp(DATE).toLocalDateTime())
                                    .build();
                            result.add(data);
                        }
                    }
                }
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Data> getRecentRecord() {
        List<Data> result = new ArrayList<>();
        try {
            try (Connection con = DriverManager.getConnection(connectionString, login, password)) {
                try (Statement statement = con.createStatement()) {
                    String tableSql = "SELECT * FROM DATA WHERE id=(SELECT MAX(id) FROM DATA)";
                    try (ResultSet resultSet = statement.executeQuery(tableSql)) {
                        while (resultSet.next()) {
                            Data data = Data.builder()
                                    .id(resultSet.getInt("id"))
                                    .temperature(resultSet.getDouble("temperature"))
                                    .humidity(resultSet.getDouble("humidity"))
                                    .dateTime(resultSet.getTimestamp(DATE).toLocalDateTime())
                                    .build();
                            result.add(data);
                        }
                    }
                }
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Integer insert(Data data) {
        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
        Integer result = null;
        try {
            try (Connection con = DriverManager.getConnection(connectionString, login, password)) {
                try (PreparedStatement statement = con.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setDouble(1, data.getTemperature());
                    statement.setDouble(2, data.getHumidity());
                    statement.setTimestamp(3, Timestamp.valueOf(data.getDateTime()));
//                    statement.setTimestamp(3, Timestamp.valueOf(data.getDateTime()));
                    statement.executeUpdate();
                    try (ResultSet rs = statement.getGeneratedKeys()) {
                        if (rs.next()) {
                            result = rs.getInt(1);
                        }
                    }
                    data.setId(result);
                }
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Boolean deleteRecords() {
        Boolean result = false;
        try {
            try (Connection con = DriverManager.getConnection(connectionString, login, password)) {
                String tableSql = "TRUNCATE TABLE data";
                try (Statement statement = con.createStatement()) {
                    statement.execute(tableSql);
                    result = true;
                }
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}

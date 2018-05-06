import dao.DataDAO;
import dto.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

public class Main {

    public static void main(String[] args) {
        final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/pogodynka?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

//        port(8080);
//        get("/hello", (req, res) -> "Hello World");
//        // http://localhost:8080/sensor?temperature=22.4&humidity=33
//        post("/sensor", (request, response) -> {
//            String temperature = request.queryParams("temperature");
//            String humidity = request.queryParams("humidity");
//            return "Podana temperatura=" + temperature + ", wilgotność=" + humidity;
//        });
        double temp = 22;
        double humi = 23;
        LocalDate data2 = LocalDate.now();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Podaj login: ");
        String login = scanner.next();
        System.out.print("Podaj hasło: ");
        String password = scanner.next();

        Data newData = new Data(temp, humi, data2);
        new DataDAO(login, password).insert(newData, CONNECTION_STRING);

        List<Data> result = new DataDAO(login, password).getAll(CONNECTION_STRING);
        result.forEach(data -> System.out.println(data));
        System.out.println("7days");
        List<Data> result2 = new DataDAO(login, password).getRecent7Days(CONNECTION_STRING);
        result2.forEach(data -> System.out.println(data));
        System.out.println("recent record");
        List<Data> result3 = new DataDAO(login, password).getRecentRecord(CONNECTION_STRING);
        result3.forEach(data -> System.out.println(data));
    }
}

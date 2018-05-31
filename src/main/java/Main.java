import controlers.WeatherAppControler;
import dao.DataDAO;
import dao.DataDAOImpl;
import dto.Data;
import dto.SensorService;
import dto.ValueCheck;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;
import settings.AppSettings;
import threads.TemperatureWatcher;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

public class Main extends Application {
    //main method
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Logger logger = Logger.getLogger("Logger");

        DataDAOImpl dataDAOImp = new DataDAOImpl(AppSettings.login, AppSettings.password, AppSettings.port, AppSettings.database);

        Parent root = FXMLLoader.load(getClass().getResource("/graphView.fxml"));
        Parent root3 = FXMLLoader.load(getClass().getResource("/graphViewWithDates.fxml"));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/weatherAppTemp.fxml"));
        Parent root2 = loader.load();

        Scene scene = new Scene(root, 500, 300);
        Scene scene2 = new Scene(root2, 500, 300);
        Scene scene3 = new Scene(root3, 600, 400);

        scene.getStylesheets().add("style.css");
        Stage stage = new Stage();
        Stage stage2 = new Stage();
        Stage stage3 = new Stage();

        stage.setTitle("Wykres");
        stage.setScene(scene);
        stage2.setTitle("Pogodynka");
        stage2.setScene(scene2);
        stage3.setTitle("Daty");
        stage3.setScene(scene3);

        WeatherAppControler controler = loader.<WeatherAppControler>getController();
        TemperatureWatcher temperatureWatcher = new TemperatureWatcher(dataDAOImp, controler);
        temperatureWatcher.start();

        stage.show();
        stage2.show();
        stage3.show();

        port(8080);
        get("/sensor", (req, res) -> {
            String temperature = req.queryParams("temperature");
            String humidity = req.queryParams("humidity");
            return "Podana temperatura=" + temperature + ", wilgotność=" + humidity;
        });
        post("/sensor", (request, response) -> {
            DateTimeFormatter dTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            DataDAOImpl dataDAO = new DataDAOImpl(AppSettings.login, AppSettings.password, AppSettings.port, AppSettings.database);
//            System.out.println(dataDAO.getRecent7Days());
            LocalDateTime currentlyDate = LocalDateTime.parse(LocalDateTime.now().format(dTF));
            String temperature = request.queryParams("temperature");
            String humidity = request.queryParams("humidity");
            ValueCheck valueCheck = new ValueCheck();
            if (valueCheck.isDouble(temperature, humidity) == true) {
                Double temp = Double.parseDouble(temperature);
                Double humi = Double.parseDouble(humidity);
                Data data = new Data(temp, humi, currentlyDate);
                dataDAO.insert(data);
            logger.info("Data were put into database in format: "+data);
            } else {
                logger.info("Data were not put into database");
            }
            return "Data were put into database";
        });

//        SensorService sensorService = new SensorService();
//        sensorService.getRecent7DaysFrom12OClock().forEach(el -> System.out.println(el.toString()));


//        System.out.println(" 222");
//        sensorService.getRecent7DaysFrom12OClock2().forEach(el-> System.out.println(el.toString()));
//
//        LocalDate now = LocalDate.now();
//        int then = now.minusDays(7).getDayOfMonth();
//        System.out.println(then);
    }
}

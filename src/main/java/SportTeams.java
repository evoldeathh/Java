import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

public class SportTeams {
    public static void main(String[] args) throws Exception {

        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection("jdbc:sqlite:Players.db");
        Statement statement = connection.createStatement();

        statement.execute("DROP TABLE Player;");
        statement.execute("CREATE TABLE Player (" +
                "name varchar, " +
                "team varchar, " +
                "position varchar, " +
                "age real," +
                "height int, " +
                "weigh int);");


        insert(connection, getTeams());
        show(statement);
        statement.close();
    }

    private static void show(Statement statement) throws SQLException {
        System.out.println("Найдите команду с самым высоким средним ростом. Выведите в консоль 5 самых высоких игроков команды.");
        String sql = "SELECT *  FROM Player WHERE team in (SELECT team from (SELECT team,max(avg_height) FROM (SELECT *, avg(height) as avg_height from Player GROUP by team))) ORDER BY height DESC\n";
        ResultSet resultSet1 = statement.executeQuery(sql);

        for (int i = 0; i < 5; i++) {
            resultSet1.next();
            System.out.println(resultSet1.getString("name"));
        }

        sql = "SELECT * FROM (SELECT *,avg(height) AS avgHeight,avg(weigh) AS avgWeigh, avg(age) AS avgAge FROM Player  GROUP BY team )\n" +
                "        WHERE (avgHeight BETWEEN 74 and 78)  and (avgWeigh BETWEEN 190 and 210)\n" +
                "        ORDER BY avgAge DESC";
        System.out.println("Найдите команду, с средним ростом равным от 74 до 78 inches и средним весом от 190 до 210 lbs, с самым высоким средним возрастом.");

        System.out.println(statement.executeQuery(sql).getString("team"));

        System.out.println("Данные для графика");
        ResultSet rs = statement.executeQuery("SELECT team, avg(age) AS avgAge FROM Player GROUP by team ORDER by avgAge");
        while (rs.next()) {
            System.out.println(rs.getString("team") + " " + rs.getString("avgAge"));
        }
    }

    private static void insert(Connection connection, HashMap<String, Team> teams) throws SQLException, IOException {
        PreparedStatement prepareStatement = connection.prepareStatement("INSERT INTO Player  VALUES (?,?,?,?,?,?);");
        for (Team team : teams.values()) {
            prepareStatement.setString(2, team.getName());
            for (Player player : team.getPlayers()) {
                prepareStatement.setString(1, player.getName());
                prepareStatement.setString(3, player.getPosition());
                prepareStatement.setDouble(4, player.getAge());
                prepareStatement.setInt(5, player.getHeight());
                prepareStatement.setInt(6, player.getWeight());
                prepareStatement.executeUpdate();

            }
        }
    }

    private static HashMap<String, Team> getTeams() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("teams.csv"));
        HashMap<String, Team> teams = new HashMap<>();
        String reader;
        br.readLine();
        while ((reader = br.readLine()) != null) {
            addTeam(teams, reader);
        }
        br.close();
        return teams;
    }

    private static void addTeam(HashMap<String, Team> teams, String line) {
        String[] split = line.replace("\"", "").split(",");
        String name = split[0];
        String teamName = split[1];
        String position = split[2];
        int height = Integer.parseInt(split[3]);
        int weight = Integer.parseInt(split[4]);
        double age = Double.parseDouble(split[5]);

        Player player = new Player(name, position, height, weight, age);

        if (teams.containsKey(teamName)) {
            teams.get(teamName).addPlayer(player);
        } else {
            Team team = new Team();
            team.setName(teamName);
            team.addPlayer(player);
            teams.put(teamName, team);
        }
    }
}

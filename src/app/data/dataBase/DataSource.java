package app.data.dataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class DataSource {
    protected Connection connection;
    protected HashMap<String, PreparedStatement> statements;

    public DataSource(){
        connection = DataBaseManager.getConnection();
        statements = DataBaseManager.getStatements();
    }

    /* Metody wykorzystywane do komunikacji z bazą danych */
    public LinkedList<AktualnoscData> getAktualnosciDB() throws SQLException{

        LinkedList<AktualnoscData> news_list = new LinkedList<>();

        PreparedStatement exeStatement;
        ResultSet resultSet;
        exeStatement = statements.get("pobierz_dzisiaj_aktualnosci_P");
        resultSet = exeStatement.executeQuery();

        while(resultSet.next()){
            news_list.push(new AktualnoscData(
                    resultSet.getInt("id_aktualnosci"),
                    resultSet.getString("tytul"),
                    resultSet.getString("tresc"),
                    DateTransformer.getJavaDate(resultSet.getDate("data_od")),
                    DateTransformer.getJavaDate(resultSet.getDate("data_do")),
                    resultSet.getInt("id_pracownika")));
            System.out.println("Dodalem :)");
        }
        return news_list;
    }

    /* Metoda odpowiedzialna za dodawanie news'a do bazy danych */
    public void createAktualnoscDB(HashMap<String, String> parameters) throws DBReadWriteException, SQLException, ParseException {

        PreparedStatement exeStatement;
        int result;
        exeStatement = statements.get("createNews_P");
        exeStatement.setString(1, parameters.get("tytul"));
        exeStatement.setString(2, parameters.get("opis"));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date_od = dateFormat.parse(parameters.get("data-od"));
        Date date_do = dateFormat.parse(parameters.get("data-do"));
        exeStatement.setDate(3, DateTransformer.getSqlDate(date_od));
        exeStatement.setDate(4, DateTransformer.getSqlDate(date_do));
        exeStatement.setInt(5, Integer.parseInt(parameters.get("id-pracownika")));
        result = exeStatement.executeUpdate();
        if(result != 1)
            throw new DBReadWriteException(result + " rows add with execute: createNews_P");
    }
}

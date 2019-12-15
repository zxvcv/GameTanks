package app.data.dataBase;

import app.Game;
import app.abstractObjects.Block;
import app.data.dataBase.dbData.Spawn;
import app.data.send.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DataSource {
    protected Connection connection;
    protected HashMap<String, PreparedStatement> statements;

    public DataSource(){
        connection = DataBaseManager.getConnection();
        statements = DataBaseManager.getStatements();
    }

    /* Metody wykorzystywane do komunikacji z bazą danych */
    public Block[][] getMapDB(String mapName) throws SQLException{
        PreparedStatement exeStatement;
        ResultSet resultSet;
        Indexer indexer = Game.getIndexer();
        Block[][] blocks;

        exeStatement = statements.get("get_mapByName");
        exeStatement.setString(1, mapName);
        resultSet = exeStatement.executeQuery();

        resultSet.last();
        if(resultSet.getRow() != 1)
            throw new SQLException("not one result");
        resultSet.first();

        int sizeX = resultSet.getInt("sizeX");
        int sizeY = resultSet.getInt("sizeY");
        blocks = new Block[sizeX][sizeY];

        exeStatement = statements.get("get_allBlocks");
        exeStatement.setString(1, mapName);
        resultSet = exeStatement.executeQuery();

        int x, y, counter = 0;
        String type;
        while(resultSet.next()){
            type = resultSet.getString("blockType");
            x = resultSet.getInt("positionX");
            y = resultSet.getInt("positionY");
            if(type.equals("STONE"))
                blocks[x][y] = new StoneBlock(new Position(x * 50 + 25, y * 50 + 25), indexer.getIndex());
            else if(type.equals("GRASS"))
                blocks[x][y] = new GrassBlock(new Position(x * 50 + 25,y * 50 + 25), indexer.getIndex());
            else
                throw new SQLException("wrong blockType");
            counter++;
        }

        if(counter != sizeX * sizeY)
            throw new SQLException("wrong num of data");

        return blocks;
    }

    public Spawn[] getSpawnsDB(String mapName) throws SQLException{
        PreparedStatement exeStatement;
        ResultSet resultSet;
        Spawn[] spawns;

        exeStatement = statements.get("get_mapByName");
        exeStatement.setString(1, mapName);
        resultSet = exeStatement.executeQuery();

        resultSet.last();
        if(resultSet.getRow() != 1)
            throw new SQLException("not one result");
        resultSet.first();

        exeStatement = statements.get("get_allSpawns");
        exeStatement.setString(1, mapName);
        resultSet = exeStatement.executeQuery();

        resultSet.last();
        int length = resultSet.getRow();
        if(length <= 0)
            throw new SQLException("no data");
        resultSet.beforeFirst();

        spawns = new Spawn[length];
        String color;
        int x,y,i=0;
        while(resultSet.next()){
            color = resultSet.getString("playerColor");
            x = resultSet.getInt("positionX");
            y = resultSet.getInt("positionY");
            spawns[i++] = new Spawn(x, y, color);
        }

        return spawns;
    }
}

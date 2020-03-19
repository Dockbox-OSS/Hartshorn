package com.darwinreforged.servermodifications.util;


import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class PaintingsDatabaseUtil {
	private static Path root;
    private static SqlService sql;

	public PaintingsDatabaseUtil(SqlService sqlP, Path rootP) throws SQLException{
    	sql = sqlP;
    	root = rootP;
    	create();
    	
    }
	public DataSource getDataSource(String jdbcUrl) throws SQLException {
        if (sql == null) {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }
        return sql.getDataSource(jdbcUrl);
    }
    
    public void create() throws SQLException{
    	
        String uri = "jdbc:sqlite:" + root + "/DarwinPaintings.db";
        ArrayList <String> queries = new ArrayList<>();
        queries.add("CREATE TABLE IF NOT EXISTS Submissions (`ID` INTEGER, `PlayerUUID` TEXT, `Command` TEXT, `Status` TEXT, PRIMARY KEY(`ID`))");
        File file = new File(root + "/DarwinPaintings.db");
        if (!file.exists()) {
        	try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Connection conn = getDataSource(uri).getConnection();
			Statement statement = conn.createStatement();

			for (String query : queries) {
				statement.addBatch(query);
			}
			statement.executeBatch();
			statement.close();
			conn.close();
			}
        
    }
}

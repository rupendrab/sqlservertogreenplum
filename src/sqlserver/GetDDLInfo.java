/*
 * Original Developer: Rupendra Bandyopadhyay
 * Company: Pivotal Inc.
 * Publish Date: 06/16/2013
 */
package sqlserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.sql.*;

import com.microsoft.sqlserver.jdbc.SQLServerStatement;

public class GetDDLInfo
{
  Connection con = null;
  Statement stmt = null;
  ResultSet rs = null;
  String sql;
  Connection hCon = null;
  
  public void connectToDatabase (String host, String port, String dbName, String userID, String password) throws ClassNotFoundException, SQLException
  {
    String connectionUrl = "jdbc:sqlserver://" + host + ":" + port + ";" + "databaseName=" + dbName + ";" + "responseBuffering=adaptive";
    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    con = DriverManager.getConnection(connectionUrl, userID, password);
  }
  
  public void connectToHSQL(String hDB) throws SQLException, ClassNotFoundException
  {
    Class.forName("org.hsqldb.jdbcDriver");
    hCon = DriverManager.getConnection("jdbc:hsqldb:file:" + hDB + ";shutdown=true", "sa", ""); // can through sql exception
  }
  
  public GetDDLInfo (String host, String port, String dbName, String userID, String password, String hDB) throws ClassNotFoundException, SQLException, IOException
  {
    connectToDatabase(host, port, dbName, userID, password);
    connectToHSQL("mydb/db");
  }
  
  public SQLServerStatement createStmt () throws SQLException
  {
    try
    {
      if (stmt != null)
      {
        stmt.close();
      }
    }
    catch (Exception e)
    {
    }

    stmt = con.createStatement();

    SQLServerStatement SQLstmt = (SQLServerStatement) stmt;
    SQLstmt.setResponseBuffering("adaptive");

    // Display the response buffering mode.
    // System.err.println("Response buffering mode has been set to " + SQLstmt.getResponseBuffering());
    return SQLstmt;
  }
  
  public static String getSQL(String fileName) throws IOException
  {
    StringBuilder sb = new StringBuilder(1000);
    BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
    String line;
    while ((line = reader.readLine()) != null)
    {
      sb.append(line);
      sb.append("\n");
    }
    reader.close();
    return sb.toString();
  }
  
  public void runDDLExtractOne() throws SQLException, IOException
  {
    sql = getSQL ("coltab.sql");
    SQLServerStatement stmtOne = createStmt();
    rs = stmt.executeQuery(sql);
    Statement stGen = hCon.createStatement();
    stGen.execute("truncate table table_cols");
    stGen.close();
    String sqlIns = "insert into table_cols values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    PreparedStatement psOne = hCon.prepareStatement(sqlIns);
    int lines = 0;
    while (rs.next())
    {
      lines++;
      psOne.setString (1,  rs.getString(1));
      psOne.setString (2,  rs.getString(2));
      psOne.setString (3,  rs.getString(3));
      psOne.setInt    (4,  rs.getInt(4));
      psOne.setString (5,  rs.getString(5));
      psOne.setString (6,  rs.getString(6));
      psOne.setInt    (7,  rs.getInt(7));
      psOne.setInt    (8,  rs.getInt(8));
      psOne.setInt    (9,  rs.getInt(9));
      psOne.setString (10, rs.getString(10));
      psOne.setInt    (11, rs.getInt(11));
      psOne.setString (12, rs.getString(12));
      psOne.setInt    (13, rs.getInt(13));
      psOne.setString (14, rs.getString(14));
      psOne.setString (15, rs.getString(15));
      psOne.setString (16, rs.getString(16));
      psOne.setLong   (17, rs.getLong(14));
      psOne.executeUpdate();
    }
    
    System.err.println("Number of records written: " + lines);
    if (psOne != null)
    {
      psOne.close();
    }
    if (rs != null)
    {
      rs.close();
    }
    if (stmtOne != null)
    {
      stmtOne.close();
    }
  }

  public void runDDLExtractTwo() throws SQLException, IOException
  {
    sql = getSQL ("colind.sql");
    SQLServerStatement stmtOne = createStmt();
    rs = stmt.executeQuery(sql);
    Statement stGen = hCon.createStatement();
    stGen.execute("truncate table index_cols");
    stGen.close();
    String sqlIns = "insert into index_cols values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    PreparedStatement psOne = hCon.prepareStatement(sqlIns);
    int lines = 0;
    while (rs.next())
    {
      lines++;
      psOne.setString (1,  rs.getString(1));
      psOne.setString (2,  rs.getString(2));
      psOne.setString (3,  rs.getString(3));
      psOne.setInt    (4,  rs.getInt(4));
      psOne.setString (5,  rs.getString(5));
      psOne.setBoolean(6,  rs.getBoolean(6));
      psOne.setBoolean(7,  rs.getBoolean(7));
      psOne.setBoolean(8,  rs.getBoolean(8));
      psOne.setBoolean(9,  rs.getBoolean(9));
      psOne.setString (10, rs.getString(10));
      psOne.setBoolean(11, rs.getBoolean(11));
      psOne.setString (12, rs.getString(12));
      psOne.setInt    (13, rs.getInt(13));
      psOne.setInt    (14, rs.getInt(14));
      psOne.setInt    (15, rs.getInt(15));
      psOne.setBoolean(16, rs.getBoolean(16));
      psOne.executeUpdate();
    }
    
    System.err.println("Number of records written: " + lines);
    if (psOne != null)
    {
      psOne.close();
    }
    if (rs != null)
    {
      rs.close();
    }
    if (stmtOne != null)
    {
      stmtOne.close();
    }
  }

  public void runDDLExtractThree() throws SQLException, IOException
  {
    sql = getSQL ("partdef.sql");
    SQLServerStatement stmtOne = createStmt();
    rs = stmt.executeQuery(sql);
    Statement stGen = hCon.createStatement();
    stGen.execute("truncate table part_def");
    stGen.close();
    String sqlIns = "insert into part_def values (?,?,?,?,?,?,?,?,?,?,?,?)";
    PreparedStatement psOne = hCon.prepareStatement(sqlIns);
    int lines = 0;
    while (rs.next())
    {
      lines++;
      psOne.setString (1,  rs.getString(1));
      psOne.setString (2,  rs.getString(2));
      psOne.setString (3,  rs.getString(3));
      psOne.setString (4,  rs.getString(4));
      psOne.setInt    (5,  rs.getInt(5));
      psOne.setBoolean(6,  rs.getBoolean(6));
      psOne.setString (7,  rs.getString(7));
      psOne.setString (8,  rs.getString(8));
      psOne.setString (9,  rs.getString(9));
      psOne.setString (10, rs.getString(10));
      psOne.setInt    (11, rs.getInt(11));
      psOne.setLong   (12, rs.getLong(12));
      psOne.executeUpdate();
    }
    
    System.err.println("Number of records written: " + lines);
    if (psOne != null)
    {
      psOne.close();
    }
    if (rs != null)
    {
      rs.close();
    }
    if (stmtOne != null)
    {
      stmtOne.close();
    }
  }
  
  public void closeAll()
  {
    try
    {
      if (rs != null)
      {
        rs.close();
      }
    }
    catch (Exception e)
    {
    }
    try
    {
      if (stmt != null)
      {
        stmt.close();
      }
    }
    catch (Exception e)
    {
    }
    try
    {
      if (con != null)
      {
        con.close();
      }
    }
    catch (Exception e)
    {
    }
    try
    {
      if (hCon != null)
      {
        hCon.close();
      }
    }
    catch (Exception e)
    {
    }
  }

  public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException
  {
    String host = Utility.getArgument("-h", args);
    String port = Utility.getArgument("-p", args);
    String dbName = Utility.getArgument("-d", args);
    String userID = Utility.getArgument("-u", args);
    if ( host == null || host.trim().equals("") || port == null || port.trim().equals("") || dbName == null || dbName.trim().equals("") || userID == null || userID.trim().equals(""))
    {
      System.out.println("Usage: java GetDDLInfo -h <Host> -p <Port> -d <Database> -u <User>");
      System.exit(1);
    }

    String password = Utility.readEnv("SSPASSWORD");
    if (password == null)
    {
      ReadPassword rp = new ReadPassword();
      password = rp.getPassword();
      if (password == null || password.trim().equals(""))
      {
        System.out.println("Password must be entered.");
        System.exit(1);
      }
    }
    
    // GetDDLInfo gdi = new GetDDLInfo("10.0.0.19", "1433", "AdventureWorks2008", "sa", "n3wf0x");
    GetDDLInfo gdi = new GetDDLInfo(host, port, dbName, userID, password, "mydb/db");
    gdi.runDDLExtractOne();
    gdi.runDDLExtractTwo();
    gdi.runDDLExtractThree();
    gdi.closeAll();
  }
}

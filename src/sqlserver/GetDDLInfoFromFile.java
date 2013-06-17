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


public class GetDDLInfoFromFile
{
  String sql;
  Connection hCon = null;
  BufferedReader colReader;
  BufferedReader indReader;
  BufferedReader partReader;
  
  public void openFiles (String colFile, String indFile, String partFile) throws IOException
  {
    colReader = new BufferedReader(new FileReader(new File(colFile)));
    indReader = new BufferedReader(new FileReader(new File(indFile)));
    partReader = new BufferedReader(new FileReader(new File(partFile)));
  }
  
  public void connectToHSQL(String hDB) throws SQLException, ClassNotFoundException
  {
    Class.forName("org.hsqldb.jdbcDriver");
    hCon = DriverManager.getConnection("jdbc:hsqldb:file:" + hDB + ";shutdown=true", "sa", ""); // can through sql exception
  }
  
  public GetDDLInfoFromFile (String colFile, String indFile, String partFile, String hDB) throws ClassNotFoundException, SQLException, IOException
  {
    openFiles (colFile, indFile, partFile);
    connectToHSQL("mydb/db");
  }
  
  public void runDDLExtractOne() throws SQLException, IOException, Exception
  {
    Statement stGen = hCon.createStatement();
    stGen.execute("truncate table table_cols");
    stGen.close();
    String sqlIns = "insert into table_cols values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    PreparedStatement psOne = hCon.prepareStatement(sqlIns);
    int lines = 0;
    String[] fields;
    while ((fields = Utility.readCSVLine(colReader, 17)) != null)
    {
      lines++;
      psOne.setString (1,  fields[0]);
      psOne.setString (2,  fields[1]);
      psOne.setString (3,  fields[2]);
      if (fields[3] != null)
      {
        psOne.setInt(4,  Integer.parseInt(fields[3]));
      }
      else
      {
        psOne.setNull(4, java.sql.Types.INTEGER);
      }
      psOne.setString (5,  fields[4]);
      psOne.setString (6,  fields[5]);
      if (fields[6] != null)
      {
        psOne.setInt(7,  Integer.parseInt(fields[6]));
      }
      else
      {
        psOne.setNull(7, java.sql.Types.INTEGER);
      }
      if (fields[7] != null)
      {
        psOne.setInt(8,  Integer.parseInt(fields[7]));
      }
      else
      {
        psOne.setNull(8, java.sql.Types.INTEGER);
      }
      if (fields[8] != null)
      {
        psOne.setInt(9,  Integer.parseInt(fields[8]));
      }
      else
      {
        psOne.setNull(9, java.sql.Types.INTEGER);
      }
      psOne.setString (10, fields[9]);
      if (fields[10] != null)
      {
        psOne.setInt(11, Integer.parseInt(fields[10]));
      }
      else
      {
        psOne.setNull(11, java.sql.Types.INTEGER);
      }
      psOne.setString (12, fields[11]);
      if (fields[12] != null)
      {
        psOne.setInt(13, Integer.parseInt(fields[12]));
      }
      else
      {
        psOne.setNull(13, java.sql.Types.INTEGER);
      }
      psOne.setString (14, fields[13]);
      psOne.setString (15, fields[14]);
      psOne.setString (16, fields[15]);
      if (fields[16] != null)
      {
        psOne.setLong(17, Long.parseLong(fields[16]));
      }
      else
      {
        psOne.setNull(17, java.sql.Types.BIGINT);
      }
      psOne.executeUpdate();
    }
    
    System.err.println("Number of records written: " + lines);
    if (psOne != null)
    {
      psOne.close();
    }
  }

  public void runDDLExtractTwo() throws SQLException, IOException, Exception
  {
    Statement stGen = hCon.createStatement();
    stGen.execute("truncate table index_cols");
    stGen.close();
    String sqlIns = "insert into index_cols values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    PreparedStatement psOne = hCon.prepareStatement(sqlIns);
    int lines = 0;
    String[] fields;
    while ((fields = Utility.readCSVLine(indReader, 16)) != null)
    {
      lines++;
      psOne.setString (1,  fields[0]);
      psOne.setString (2,  fields[1]);
      psOne.setString (3,  fields[2]);
      if (fields[3] != null)
      {
        psOne.setInt(4,  Integer.parseInt(fields[3]));
      }
      else
      {
        psOne.setNull(4, java.sql.Types.INTEGER);
      }
      psOne.setString (5,  fields[4]);
      psOne.setBoolean(6,  Boolean.parseBoolean(fields[5]));
      psOne.setBoolean(7,  Boolean.parseBoolean(fields[6]));
      psOne.setBoolean(8,  Boolean.parseBoolean(fields[7]));
      psOne.setBoolean(9,  Boolean.parseBoolean(fields[8]));
      psOne.setString (10, fields[9]);
      psOne.setBoolean(11, Boolean.parseBoolean(fields[10]));
      psOne.setString (12, fields[11]);
      if (fields[12] != null)
      {
        psOne.setInt(13, Integer.parseInt(fields[12]));
      }
      else
      {
        psOne.setNull(13, java.sql.Types.INTEGER);
      }
      if (fields[13] != null)
      {
        psOne.setInt(14, Integer.parseInt(fields[13]));
      }
      else
      {
        psOne.setNull(14, java.sql.Types.INTEGER);
      }
      if (fields[14] != null)
      {
        psOne.setInt(15, Integer.parseInt(fields[14]));
      }
      else
      {
        psOne.setNull(15, java.sql.Types.INTEGER);
      }
      psOne.setBoolean(16, Boolean.parseBoolean(fields[15]));
      psOne.executeUpdate();
    }
    
    System.err.println("Number of records written: " + lines);
    if (psOne != null)
    {
      psOne.close();
    }
  }

  public void runDDLExtractThree() throws SQLException, IOException, Exception
  {
    Statement stGen = hCon.createStatement();
    stGen.execute("truncate table part_def");
    stGen.close();
    String sqlIns = "insert into part_def values (?,?,?,?,?,?,?,?,?,?,?,?)";
    PreparedStatement psOne = hCon.prepareStatement(sqlIns);
    int lines = 0;
    String[] fields;
    while ((fields = Utility.readCSVLine(partReader, 12)) != null)
    {
      lines++;
      psOne.setString (1,  fields[0]);
      psOne.setString (2,  fields[1]);
      psOne.setString (3,  fields[2]);
      psOne.setString (4,  fields[3]);
      if (fields[4] != null)
      {
        psOne.setInt(5,  Integer.parseInt(fields[4]));
      }
      else
      {
        psOne.setNull(5, java.sql.Types.INTEGER);
      }
      psOne.setBoolean(6,  Boolean.parseBoolean(fields[5]));
      psOne.setString (7,  fields[6]);
      psOne.setString (8,  fields[7]);
      psOne.setString (9,  fields[8]);
      psOne.setString (10, fields[9]);
      if (fields[10] != null)
      {
        psOne.setInt(11, Integer.parseInt(fields[10]));
      }
      else
      {
        psOne.setNull(11, java.sql.Types.INTEGER);
      }
      if (fields[11] != null)
      {
        psOne.setLong(12, Long.parseLong(fields[11]));
      }
      else
      {
        psOne.setNull(12, java.sql.Types.BIGINT);
      }
      psOne.executeUpdate();
    }
    
    System.err.println("Number of records written: " + lines);
    if (psOne != null)
    {
      psOne.close();
    }
  }
  
  public void closeAll()
  {
    try
    {
      if (colReader != null)
      {
        colReader.close();
      }
    }
    catch (Exception e)
    {
    }
    try
    {
      if (indReader != null)
      {
        indReader.close();
      }
    }
    catch (Exception e)
    {
    }
    try
    {
      if (partReader != null)
      {
        partReader.close();
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

  public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException, Exception
  {
    String colReaderFile = Utility.getArgument("-fc", args);
    String indReaderFile = Utility.getArgument("-fi", args);
    String partReaderFile = Utility.getArgument("-fp", args);
    if ( colReaderFile == null || colReaderFile.trim().equals("") || indReaderFile == null || indReaderFile.trim().equals("") || partReaderFile == null || partReaderFile.trim().equals(""))
    {
      System.out.println("Usage: java GetDDLInfoFromFile -fc <Col Def File> -fi <Ind Def File> -fp <Part Def File>");
      System.exit(1);
    }
    
    GetDDLInfoFromFile gdiff = new GetDDLInfoFromFile(colReaderFile, indReaderFile, partReaderFile, "mydb/db");
    gdiff.runDDLExtractOne();
    gdiff.runDDLExtractTwo();
    gdiff.runDDLExtractThree();
    gdiff.closeAll();
  }
}

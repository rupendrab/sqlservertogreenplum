/*
 * Original Developer: Rupendra Bandyopadhyay
 * Company: Pivotal Inc.
 * Publish Date: 06/16/2013
 */
package sqlserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Hashtable;

public class GenDDL
{

  Hashtable<String,Boolean> gpKeywords = new Hashtable<String,Boolean>(400); 
  Connection hCon = null;
  Statement stmt = null;
  ResultSet rs = null;
  String sql;
  BufferedWriter ddlWriter;
  String specificTableName;
  String specificSchemaName;
  
  public void connectToHSQL(String hDB) throws SQLException, ClassNotFoundException
  {
    Class.forName("org.hsqldb.jdbcDriver");
    hCon = DriverManager.getConnection("jdbc:hsqldb:file:" + hDB + ";shutdown=true", "sa", ""); // can through sql exception
    stmt = hCon.createStatement();
  }

  public void openDDLFile(String fileName) throws IOException
  {
    ddlWriter = new BufferedWriter(new FileWriter(new File(fileName)));
  }

  public void getKeywords() throws SQLException
  {
    String sql = "select keyword_id from gp_keywords order by 1";
    ResultSet rs = stmt.executeQuery(sql);
    while (rs.next())
    {
      gpKeywords.put(rs.getString(1), true);
    }
    rs.close();
  }
  
  public String addQuotes (String name)
  {
    if (gpKeywords.containsKey(name) || name.contains(" "))
    {
      return "\"" + name + "\"";
    }
    else
    {
      return name;
    }
  }
  
  public GenDDL (String hDB, String ddlFile, String schemaName, String tableName) throws ClassNotFoundException, SQLException, IOException
  {
    connectToHSQL(hDB);
    openDDLFile(ddlFile);
    getKeywords();
    specificSchemaName = schemaName;
    specificTableName = tableName;    
    if (tableName != null)
    {
      String[] tableParts = tableName.split("\\.");
      if (tableParts.length > 1)
      {
        specificSchemaName = tableParts[0];
        specificTableName = tableParts[1];
      }
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
      if (hCon != null)
      {
        hCon.close();
      }
    }
    catch (Exception e)
    {
    }
    try
    {
      if (ddlWriter != null)
      {
        ddlWriter.close();
      }
    }
    catch (Exception e)
    {
    }
  }

  public void sequenceDDL() throws SQLException, IOException
  {
    String sqlSequence = getSQL("identity.sql");
    String whereClause = "";
    if (specificSchemaName != null)
    {
      whereClause = whereClause + " and lower(schemaname) = '" + specificSchemaName.toLowerCase() + "'";
    }
    if (specificTableName != null)
    {
      whereClause = whereClause + " and lower(tablename) = '" + specificTableName.toLowerCase() + "'";
    }
    if (! whereClause.equals(""))
    {
      sqlSequence = sqlSequence.replaceAll("--myfilter", whereClause);
    }
    PreparedStatement psSeq = hCon.prepareStatement(sqlSequence);
    ResultSet rsSeq = psSeq.executeQuery();
    while (rsSeq.next())
    {
      StringBuilder sb = new StringBuilder(200);
      int colCount=0;
      String schemaName = rsSeq.getString(++colCount);
      String tableName = rsSeq.getString(++colCount);
      int colId = rsSeq.getInt(++colCount);
      String seedValue = rsSeq.getString(++colCount);
      String incrementValue = rsSeq.getString(++colCount);
      String lastValue = rsSeq.getString(++colCount);
      if (schemaName.equals("dbo"))
      {
        schemaName = "public";
      }
      sb.append ("create sequence " + addQuotes(schemaName.toLowerCase()) + ".seq_" + tableName.toLowerCase() + "_" + colId);
      sb.append("\n");
      sb.append ("  start with " + (lastValue!=null?(Long.parseLong(lastValue)+1):seedValue));
      sb.append("\n");
      sb.append ("  increment by " + (incrementValue));
      sb.append("\n");
      sb.append ("  cache 100;");
      sb.append("\n\n");
      ddlWriter.write(sb.toString());
    }
    rsSeq.close();
    psSeq.close();
  }

  public void schemaDDL() throws SQLException, IOException
  {
    sql = "select distinct schemaname from table_cols @@myfilter order by 1";
    String whereClause = "where 1=1";
    if (specificSchemaName != null)
    {
      whereClause = whereClause + " and lower(schemaname) = '" + specificSchemaName.toLowerCase() + "'";
    }
    if (specificTableName != null)
    {
      whereClause = whereClause + " and lower(tablename) = '" + specificTableName.toLowerCase() + "'";
    }
    sql = sql.replaceAll("@@myfilter", whereClause);
    rs = stmt.executeQuery(sql);
    while (rs.next())
    {
      String schemaName = rs.getString(1).toLowerCase();
      if (! schemaName.equals("dbo"))
      {
        String line = "create schema " + addQuotes(schemaName) + ";" + "\n";
        ddlWriter.write(line);
      }
    }
    ddlWriter.write("\n");
    rs.close();
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
  
  public String getDistributionClause (String schemaName, String tableName, PreparedStatement psDist) throws IOException, SQLException
  {
    StringBuilder sb = new StringBuilder(50);
    psDist.setString(1, schemaName);
    psDist.setString(2, tableName);
    ResultSet rs = psDist.executeQuery();
    int cols = 0;
    String prevIndexName = "";
    while (rs.next())
    {
      cols++;
      if (cols == 1)
      {
        sb.append("distributed by (");
      }
      int colIndex=0;
      String indexName = rs.getString(++colIndex);
      String columnName = rs.getString(++colIndex);
      if (! prevIndexName.equals("") && ! indexName.equals(prevIndexName) )
      {
        break;
      }
      else
      {
        if (cols > 1)
        {
          sb.append(", ");
        }
        sb.append(addQuotes(columnName.toLowerCase()));
      }
      prevIndexName = new String(indexName);
    }
    rs.close();
    if (cols == 0)
    {
      sb.append("distributed randomly");
    }
    else
    {
      sb.append(")");
    }
    return sb.toString();
  }
  
  public String getPartitionClause (String schemaName, String tableName, PreparedStatement psPart) throws IOException, SQLException
  {
    StringBuilder sb = new StringBuilder(50);
    psPart.setString(1, schemaName);
    psPart.setString(2, tableName);
    ResultSet rs = psPart.executeQuery();
    int cnt = 0;
    int written = 0;
    while (rs.next())
    {
      cnt++;
      int colIndex = 0;
      String columnName = rs.getString(++colIndex);
      int boundaryId = rs.getInt(++colIndex);
      boolean boundaryIsRight = rs.getBoolean(++colIndex);
      String partType = rs.getString(++colIndex);
      String boundaryValue = rs.getString(++colIndex);
      int partNumber = rs.getInt(++colIndex);
      if (partType.equalsIgnoreCase("range")) // Implemented range partitions only
      {
        written++;
        if (cnt == 1)
        {
          sb.append("partition by range (" + addQuotes(columnName.toLowerCase()) + ")");
          sb.append("\n");
          sb.append("(");
          sb.append("\n");
        }
        else
        {
          sb.append(",\n");
        }
        if (boundaryValue != null)
        {
          sb.append ("  partition p_" + partNumber + " end ('" + boundaryValue + "')");
          sb.append (boundaryIsRight?" exclusive":" inclusive");
        }
        else
        {
          sb.append("  default partition p_default");
        }
      }
    }
    rs.close();
    if (written > 1)
    {
      sb.append("\n");
      sb.append(")");
      sb.append("\n");
    }
    return sb.toString();
  }
  
  public void tableDDL() throws SQLException, IOException
  {
    sql = "select schemaname, tablename, count(*) from table_cols @@myfilter group by schemaname, tablename order by 1, 2";
    String whereClause = "where 1=1";
    if (specificSchemaName != null)
    {
      whereClause = whereClause + " and lower(schemaname) = '" + specificSchemaName.toLowerCase() + "'";
    }
    if (specificTableName != null)
    {
      whereClause = whereClause + " and lower(tablename) = '" + specificTableName.toLowerCase() + "'";
    }
    sql = sql.replaceAll("@@myfilter", whereClause);
    rs = stmt.executeQuery(sql);
    String sqlCols = getSQL("translate.sql");
    PreparedStatement ps = hCon.prepareStatement(sqlCols);
    String sqldist = getSQL("dist.sql");
    PreparedStatement psDist = hCon.prepareStatement(sqldist);
    String sqlpart = getSQL("part.sql");
    PreparedStatement psPart = hCon.prepareStatement(sqlpart);
    while (rs.next())
    {
      String schemaName = rs.getString(1);
      String tableName = rs.getString(2);
      int cntCols = rs.getInt(3);
      String newSchemaName = new String(schemaName);
      if (schemaName.equalsIgnoreCase("dbo"))
      {
        newSchemaName = "public";
      }
      ddlWriter.write("create table " + addQuotes(newSchemaName.toLowerCase()) + "." + addQuotes(tableName.toLowerCase()));
      ddlWriter.write("\n");
      ddlWriter.write("(");
      ddlWriter.write("\n");
      ps.setString(1, schemaName);
      ps.setString(2, tableName);
      ResultSet rs2 = ps.executeQuery();
      int colCount = 0;
      while (rs2.next())
      {
        colCount++;
        int colIndex = 0;
        String columnName = rs2.getString(++colIndex);
        int colId = rs2.getInt(++colIndex);
        String dataType = rs2.getString(++colIndex);
        String actDataType = rs2.getString(++colIndex);
        String gpType = rs2.getString(++colIndex);
        String dataTypeTranslated = rs2.getString(++colIndex);
        String nullClause = rs2.getString(++colIndex);
        String defaultVal = rs2.getString(++colIndex);
        String computeDef = rs2.getString(++colIndex);
        String seedValue = rs2.getString(++colIndex);
        StringBuilder line = new StringBuilder(100);
        if (gpType == null || dataTypeTranslated == null)
        {
          line.append("  --Attn " + columnName + ", " + dataType + "(" + actDataType + ")" + ", " + nullClause + ", default=" + defaultVal + ", computed=" + computeDef);
        }
        else
        {
          line.append(String.format("  %-40s %-30s %-10s", addQuotes(columnName.toLowerCase()), dataTypeTranslated, nullClause));
          if (defaultVal != null)
          {
            char[] defaultC = defaultVal.toCharArray();
            StringBuilder defaultClause = new StringBuilder(defaultC.length - 2);
            int startParens = 0;
            for (int i=0; i<defaultC.length; i++)
            {
              if (defaultC[i] != '(')
              {
                break;
              }
              startParens++;
            }
            defaultClause.append(String.copyValueOf(defaultC, startParens, defaultC.length - startParens *2));
            boolean defaultClauseValid = false;
            if (defaultClause.toString().matches("^getdate *\\( *\\) *$"))
            {
              defaultClause = new StringBuilder("current_timestamp");
              defaultClauseValid = true;
            }
            else if (defaultClause.toString().matches("^ *[0-9.]+ *"))
            {
              defaultClauseValid = true;
              if (gpType.equals("bit"))
              {
                defaultClause.append("::bit");
              }
            }
            else if (defaultClause.toString().matches("^ *'.*' *"))
            {
              defaultClauseValid = true;
            }
            if (defaultClauseValid)
            {
              line.append (" default " + defaultClause);
            }
            else
            {
              line.append ("/* Attn default " + defaultClause + " */" );
            }
          }
          if (seedValue != null)
          {
            line.append(" default nextval('");
            line.append(newSchemaName.toLowerCase() + ".seq_" + tableName.toLowerCase() + "_" + colId);
            line.append("')");
          }
          if (computeDef != null)
          {
            line.append(" /* Attn Compute Def=" + computeDef + " */");
          }
          if (colCount < cntCols)
          {
            line.append(",");
          }
        }
        ddlWriter.write(line.toString());
        ddlWriter.write("\n");
      }
      ddlWriter.write(")");
      ddlWriter.write("\n");
      rs2.close();
      String distributionClause = getDistributionClause(schemaName, tableName, psDist);
      ddlWriter.write(distributionClause);
      ddlWriter.write("\n");
      String partitionClause = getPartitionClause(schemaName, tableName, psPart);
      if (partitionClause != null && ! partitionClause.equals(""))
      {
        ddlWriter.write(partitionClause);
      }
      ddlWriter.write(";");
      ddlWriter.write("\n");
      ddlWriter.write("\n");
    }
    ddlWriter.write("\n");
    rs.close();
    ps.close();
    psDist.close();
  }

  public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException
  {
    String ddlFile = Utility.getArgument("-f", args);
    String tableName = Utility.getArgument("-t", args);
    String schemaName = Utility.getArgument("-s", args);
    if ( ddlFile == null || ddlFile.trim().equals(""))
    {
      System.out.println("Usage: java GenDDL -f <DDL File>");
      System.exit(1);
    }
    /*
    if (tableName != null && ! tableName.trim().equals(""))
    {
      if (schemaName != null && ! schemaName.trim().equals(""))
      {
        System.out.println("Use either schema name or table name");
        System.exit(1);
      }
    }
    */
    GenDDL gd = new GenDDL("mydb/db", ddlFile, schemaName, tableName);
    gd.schemaDDL();
    gd.sequenceDDL();
    gd.tableDDL();
    gd.closeAll();
  }
}

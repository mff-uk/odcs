/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
// JDBC Table Dump program.  This application opens an ODBC database,
// selects all the records in a table, and using the meta-data returned prints a
// report (dump).  By changing the SQL, this program can produce an arbitrary report
// on a join on a database.  The database and table must be supplied on the command
// line.  Not all databases support meta-data, especially if the resultSet is empty.
// The metadata also contains info on column widths, which should be used to
// line up columns correctly.  (Omitted here for clarity.)
//
// There is no standard SQL to list the databases (the SQL standard
// uses the term schema) available on some server.  However there
// is a standard SQL query to list the schemas in a database server (the
// SQL standard uses the term catalog), but some DBMSes don't support
// schemas, or don't follow the standard (e.g., DB2 and Oracle).  Use:
//   SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA
//
// The standard SQL to list the tables in a DB/schema is:
//  SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
//     WHERE TABLE_SCHEMA = 'name'
// but not all DBMSes support it.  Oracle uses "SELECT * FROM TAB",
// and DB2 uses "SYSCAT" instead of "INFORMATION_SCHEMA".)
//
// Using INFORMATION_SCHEMA it is possible to describe (list the columns
// and their types and constraints) any table, but not all DBMSes
// support this.  For Oracle use "DESCRIBE tablename" and for DB2
// use "DESCRIBE TABLE tablename".
//
// (C) 1999 by Wayne Pollock, Tampa Florida USA.  All Rights reserved.

import java.sql.*;

public class DBDump {

public static void main ( String [] args ) {
   if ( args.length != 2 ) {
      System.out.println( "**** Usage: java DBDump <database> <table>" );
      return;
   }
   String db = args[0];
   String table = args[1];
   String username = "";
   String password = "";
   String URL = "jdbc:odbc:" + db;
   String SQL_Query = "SELECT * FROM " + table + ";";

   try {   Class.forName( "sun.jdbc.odbc.JdbcOdbcDriver" ); }
   catch ( Exception e ) {
       System.out.println( "**** Cannot load ODBC driver!" );
       return;
   }

   Connection con = null;
   Statement stmt = null;
   try {
       con = DriverManager.getConnection( URL, username, password );
       stmt = con.createStatement();
   } catch ( Exception e ) {
       System.err.println( "**** Cannot open connection to " + URL + "!" );  }

   try {
       ResultSet results = stmt.executeQuery( SQL_Query );

       // Collect meta-data:
       ResultSetMetaData meta = results.getMetaData();
       int numColumns = meta.getColumnCount();

       // Display results:
       System.out.println( "\n\t--- " + db + " (" + table + ")" + " ---\n" );
       for ( int i = 1; i <= numColumns; ++i )
           System.out.print( "\t" + meta.getColumnLabel( i ) );
       System.out.println();

       while ( results.next() )       // Fetch next row, quit when no rows left.
       {   for ( int i = 1; i <= numColumns; ++i )
           {   String val = results.getString( i );
               if ( val == null )
                   val = "(null)";
               System.out.print( "\t" + val );
           }
           System.out.println();
       }
       con.close();
   }
   catch ( Exception e )
   {   e.printStackTrace();
   }
}  // End of Main
}  // End of DBDump

 

# SQL Server to Greenplum Migration Code using Java

This codebase is intended to help with migrating ddl and data from a sql server database to a greenplum database.

To compile the code,simply cd to the base directory and run ./build.sh (I am going to add ant support in the next release)

The DDL migration process is two stage, the intention being to add more ability for manual intervention for esoteric datatypes.

1. GetDDLInfo.sh collects the DDL info from the source sqlserver database and populates the data directory into an intermediate hsqldb database.
This database also contains some basic data type migration strategies in a configuration table. The database can be accessed by running the script "qt.sh".
2. GenDDL.sh to generate DDL (for the entire database or for a table) from the HSQLDB database.

The DDL migration handles partitioning, most of the datatypes available in SQL Server 2005 and 2008 and also adds a distribution key based on availability of primary key / unique indexes.

Data migration is also done using a two step process.

1. Step 1 generates a CSV file from the source sql server database. Use RunSQL.sh
2. Step 2 uses greenplum's gpload parallel data load utiliy to load this CSV file into database. Use script load_to_gp.sh

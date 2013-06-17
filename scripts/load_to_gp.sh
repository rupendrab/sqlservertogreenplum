#!/bin/bash
# Original Developer: Rupendra Bandyopadhyay
# Company: Pivotal Inc.
# Publish Date: 06/16/2013

if [ $# -ne 2 ]
then
  exit 1
fi

datafile="$1"

if [ ! -f "$datafile" ]
then
  exit 1
fi

schema=$2

if echo ${datafile} | grep "\.gz$" >/dev/null 2>&1
then
  gzip -d ${datafile}
  datafile=${datafile%.gz}
fi

filebasename=$(basename $datafile)
filebasename=${filebasename%.*}

if echo ${filebasename} | grep -i "^.*_PART_.*$" >/dev/null 2>&1
then
  tablename=$(echo "$filebasename" | sed -r 's/^(.*)_PART_.*$/\1/')
  ispart=1
else
  tablename=${filebasename}
  ispart=0
fi

echo $schema $tablename $datafile
datafilenew=$(echo "$datafile" | sed 's/\//\\\//g')

loadfile=$(echo $logfile | sed 's/_status.log/.txt/')
ymlfile="$schema"_"$tablename"_$$.yml

echo "
---
VERSION: 1.0.0.1
DATABASE: @dbName
USER: @userID
HOST: @dbHost
PORT: @dbPort
GPLOAD:
  INPUT:
    - SOURCE:
        LOCAL_HOSTNAME: 
          - @hostName
        PORT_RANGE: [8000,8009]
        FILE: 
          - @fileName
    - COLUMNS:
" | \
    sed "s/@fileName/$datafilenew/" | \
    sed "s/@hostName/`hostname`/" | \
    sed "s/@dbName/$PGDATABASE/" | \
    sed "s/@userID/$PGUSER/" | \
    sed "s/@dbHost/$PGHOST/" | \
    sed "s/@dbPort/$PGPORT/" \
    > $ymlfile
psql -At -c "
  select '        - ' || rpad(attname || ':',30,' ') || 
         case when pg_catalog.format_type(atttypid, atttypmod) like 'bytea%' then 'text' else pg_catalog.format_type(atttypid, atttypmod) end
  from   pg_attribute
  where  attrelid = '$schema.$tablename'::regclass
  and    attnum > 0
  order by attnum
" >> $ymlfile

echo " 
    - ERROR_LIMIT: 1000
    - ERROR_TABLE: ${schema}.${tablename}_errors
" >> $ymlfile

printf "
    - HEADER: false
    - FORMAT: csv
    - QUOTE: '%s'
  OUTPUT:
    - TABLE: @tableName
    - MODE: insert
    - MAPPING:
" '"' | sed "s/@tableName/$schema.$tablename/" >> $ymlfile

psql -At -c "
  select '        ' || attname || ' : ' ||
         case when pg_catalog.format_type(atttypid, atttypmod) like 'bytea%' then 'pg_catalog.decode(' || attname || ',''hex'')' else attname end
  from   pg_attribute
  where  attrelid = '$schema.$tablename'::regclass
  and    attnum > 0
  order by attnum
" >> $ymlfile

if [ $ispart -eq 0 ]
then
  echo "
  PRELOAD:
   - TRUNCATE : true
" >> $ymlfile
fi

cat $ymlfile
gpload -f $ymlfile
rm -f $ymlfile

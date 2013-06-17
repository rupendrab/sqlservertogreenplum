select schemaname,
       tablename,
       colid,
       seed_value,
       increment_value,
       last_value
from   table_cols
where  seed_value is not null
--myfilter
order by schemaname,
         tablename,
         colid

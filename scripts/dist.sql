select ic.indexname,
       ic.columnname
from   index_cols ic
where  ic.schemaname = ?
and    ic.tablename = ?
and    ( ic.is_primary_key or ic.is_unique )
and    ic.key_ordinal > 0
order by case
           when is_primary_key then 1
           when is_unique then 2
           else 3
         end,
         ic.indexname,
         ic.key_ordinal

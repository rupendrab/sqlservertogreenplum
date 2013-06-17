select sch.name schemaname,
       tab.name tablename,
       ind.name indexname,
       ind.index_id,
       ind.type_desc,
       ind.is_unique,
       ind.is_primary_key,
       ind.is_unique_constraint,
       ind.has_filter,
       ind.filter_definition,
       ind.is_disabled,
       col.name columnname,
       indc.column_id,
       indc.key_ordinal,
       indc.index_column_id,
       indc.is_included_column
from   sys.tables tab
left outer join sys.indexes ind on ind.object_id = tab.object_id and
ind.type_desc != 'HEAP'
inner join sys.schemas sch on sch.schema_id = tab.schema_id
inner join sys.index_columns indc on indc.object_id = ind.object_id
and indc.index_id = ind.index_id
left outer join sys.syscolumns col on col.id = indc.object_id and
col.colid = indc.column_id
order by 1,2,13

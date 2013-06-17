select parts.sch_name,
       parts.table_name,
       parts.index_name,
       sdef.column_name,
       partdef.boundary_id,
       sdef.boundary_value_on_right,
       sdef.type_desc,
       sdef.partition_function_name,
       sdef.partition_scheme_name,
       cast(partdef.value as varchar(2000)) partdef,
       parts.partition_number,
       parts.rows
from
(
select sch.name sch_name,
       tab.name table_name,
       ind.name index_name,
       partition_number,
       rows,
       max(partition_number) over(partition by part.object_id) maxpart
from sys.partitions part
inner join sys.tables tab on tab.object_id = part.object_id
inner join sys.schemas sch on sch.schema_id = tab.schema_id
inner join sys.indexes ind on ind.object_id = part.object_id and
ind.index_id = part.index_id
where part.index_id <= 1
) parts
inner join
(
select sch.name sch_name,
       tab.name table_name,
       ind.name index_name,
       col.name column_name,
       pf.name partition_function_name,
       ps.name partition_scheme_name,
       pf.type_desc,
       pf.boundary_value_on_right
from   sys.indexes ind
inner join sys.partition_schemes ps on ps.data_space_id = ind.data_space_id
inner join sys.partition_functions pf on pf.function_id = ps.function_id
inner join sys.tables tab on tab.object_id = ind.object_id
inner join sys.schemas sch on sch.schema_id = tab.schema_id
inner join sys.index_columns indc on indc.object_id = ind.object_id
                                 and indc.partition_ordinal = 1
inner join sys.columns col on col.object_id = ind.object_id
                          and col.column_id = indc.column_id
where  ind.index_id < 2
) sdef on sdef.sch_name = parts.sch_name
      and sdef.table_name = parts.table_name
      and sdef.index_name = parts.index_name
left outer join
(
select sch.name sch_name,
       tab.name table_name,
       ind.name index_name,
       col.name column_name,
       pf.name partition_function_name,
       ps.name partition_scheme_name,
       pf.type_desc,
       pf.boundary_value_on_right,
       prv.boundary_id,
       prv.parameter_id,
       prv.value
from   sys.indexes ind
inner join sys.partition_schemes ps on ps.data_space_id = ind.data_space_id
inner join sys.partition_functions pf on pf.function_id = ps.function_id
inner join sys.tables tab on tab.object_id = ind.object_id
inner join sys.schemas sch on sch.schema_id = tab.schema_id
inner join sys.index_columns indc on indc.object_id = ind.object_id
                                 and indc.partition_ordinal = 1
inner join sys.columns col on col.object_id = ind.object_id
                          and col.column_id = indc.column_id
inner join sys.partition_range_values prv on prv.function_id = pf.function_id
where  ind.index_id < 2
) partdef on partdef.sch_name = parts.sch_name
         and partdef.table_name = parts.table_name
         and partdef.index_name = parts.index_name
         and partdef.boundary_id = parts.partition_number
where parts.maxpart > 1
order by parts.table_name,
         parts.partition_number

select lower(tc.columnname) columnname,
       colid,
       tc.datatype,
       tc.actdatatype,
       tm.gp_type,
       case 
         when tc.actdatatype = 'float' and tc.dataprecision <= 24 then 'float'
         when tc.actdatatype = 'float' and tc.dataprecision > 24 then 'double precision'
         else tm.gp_type 
       end ||
       case 
         when ( ((tm.use_precision or tm.use_scale) and tc.dataprecision != -1) or tm.new_precision > 0 or tm.new_scale > 0 ) then 
           '(' ||
           case
             when tm.use_precision then cast(tc.dataprecision as varchar(100))
             when tm.new_precision > 0 then cast(tm.new_precision as varchar(100))
             else ''
           end ||
           case
             when tm.use_scale then ',' || cast(tc.datascale as varchar(100))
             when tm.new_scale > 0 then ',' || cast(tm.new_scale as varchar(100))
             else ''
           end ||
           ')'
         else ''
       end datatypetranslated,
       case when tc.nullable = 0 then 'not null ' else '' end null_clause,
       tc.defaultval,
       case when tc.iscomputed > 0 then tc.computedef else null end computedef,
       tc.seed_value
from   table_cols tc
left outer join type_mapping tm on tm.ss_type = tc.actdatatype
where  tc.schemaname = ?
and    tc.tablename = ?
order by tc.colid

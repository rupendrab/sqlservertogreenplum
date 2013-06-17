select sch.name schemaname,
       tab.name tablename,
       col.name columnname,
       col.colid,
       typ.name datatype,
       typ2.name actdatatype,
       col.length,
       col.prec,
       col.scale,
       def.definition default_val,
       col.iscomputed,
       cc.definition,
       col.isnullable,
       cast(ic.seed_value as varchar(100)) seed_value,
       cast(ic.increment_value as varchar(100)) increment_value,
       cast(ic.last_value as varchar(100)) last_value,
       sind.rowcnt
from   sys.tables tab
left outer join sys.sysindexes sind on sind.id = tab.object_id and
sind.indid in (0,1)
inner join sys.schemas sch on sch.schema_id = tab.schema_id
left outer join sys.syscolumns col on col.id = tab.object_id
left outer join sys.computed_columns cc on cc.object_id = col.id
                                       and cc.column_id = col.colid
inner join sys.types typ on typ.system_type_id = col.xtype
                        and typ.user_type_id = col.xusertype
left outer join sys.types typ2 on typ2.system_type_id = col.xtype
                              and typ2.user_type_id = col.xtype
left outer join sys.default_constraints def on def.parent_object_id = col.id
                                           and def.parent_column_id = col.colid
left outer join sys.identity_columns ic on ic.object_id = col.id
                                       and ic.column_id = col.colid
order by 1,2,4

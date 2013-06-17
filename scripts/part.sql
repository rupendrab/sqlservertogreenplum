select columnname,
       boundary_id,
       boundary_is_right,
       parttype,
       boundary_value,
       part_number
from   part_def
where  schemaname = ?
and    tablename = ?
order by part_number

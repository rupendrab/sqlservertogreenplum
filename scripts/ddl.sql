create schema ss_anal;

create table ss_anal.ss_tablecols
(
  schemaname      varchar(100),
  tablename       varchar(100),
  columnname      varchar(100),
  colid           int,
  datatype        varchar(100),
  actdatatype     varchar(100),
  datalength      int,
  dataprecision   int,
  datascale       int,
  defaultval      varchar,
  iscomputed      int,
  computedef      varchar,
  nullable        int,
  cntrows         bigint
)
distributed by (schemaname,tablename,columnname);

create table ss_anal.ss_indexcols
(
  schemaname                varchar(100),
  tablename                 varchar(100),
  indexname                 varchar(100),
  index_id                  int,
  type_desc                 varchar(100),
  is_unique                 boolean,
  is_primary_key            boolean,
  is_unique_constraint      boolean,
  has_filter                boolean,
  filter_definition         varchar,
  is_disabled               boolean,
  columnname                varchar(100),
  column_id                 int,
  key_ordinal               int,
  index_column_id           int,
  is_included_column        boolean
)
distributed by (schemaname,tablename,indexname,columnname);


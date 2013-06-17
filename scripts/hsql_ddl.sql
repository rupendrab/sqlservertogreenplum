create table part_def
(
  schemaname          varchar(100),
  tablename           varchar(100),
  indexname           varchar(100),
  columnname          varchar(100),
  boundary_id         int,
  boundary_is_right   boolean,
  parttype            varchar(100),
  part_function       varchar(100),
  part_scheme         varchar(100),
  boundary_value      varchar(1000),
  part_number         int,
  rowcnt              bigint
);

create index idx_part_def on part_def (schemaname, tablename);

create table table_cols
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
  defaultval      varchar(2000),
  iscomputed      int,
  computedef      varchar(2000),
  nullable        int,
  seed_value      varchar(100),
  increment_value varchar(100),
  last_value      varchar(100),
  cntrows         bigint
);

create index idx_table_cols on table_cols (schemaname, tablename);

create table index_cols
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
  filter_definition         varchar(2000),
  is_disabled               boolean,
  columnname                varchar(100),
  column_id                 int,
  key_ordinal               int,
  index_column_id           int,
  is_included_column        boolean
);

create index idx_index_cols on index_cols (schemaname, tablename);

create table type_mapping
(
  ss_type          varchar(100),
  gp_type          varchar(100),
  use_precision    boolean,
  use_scale        boolean,
  new_precision    int,
  new_scale        int
);

insert into type_mapping values ('bit', 'bit', false, false, -1, -1);
insert into type_mapping values ('date', 'date', false, false, -1, -1);
insert into type_mapping values ('datetime', 'timestamp', false, false, -1, -1);
insert into type_mapping values ('decimal', 'numeric', true, true, -1, -1);
insert into type_mapping values ('int', 'int', false, false, -1, -1);
insert into type_mapping values ('money', 'numeric', false, false, 19, 4);
insert into type_mapping values ('nchar', 'char', true, false, -1, -1);
insert into type_mapping values ('numeric', 'numeric', true, true, -1, -1);
insert into type_mapping values ('nvarchar', 'varchar', true, false, -1, -1);
insert into type_mapping values ('smallint', 'smallint', false, false, -1, -1);
insert into type_mapping values ('smallmoney', 'numeric', false, false, 10, 4);
insert into type_mapping values ('time', 'time', false, false, -1, -1);
insert into type_mapping values ('tinyint', 'smallint', false, false, -1, -1);
-- insert into type_mapping values ('uniqueidentifier -- none
insert into type_mapping values ('varbinary', 'bytea', false, false, -1, -1);
insert into type_mapping values ('varchar', 'varchar', true, false, -1, -1);
insert into type_mapping values ('xml', 'xml', false, false, -1, -1);
insert into type_mapping values ('char', 'char', true, false, -1, -1);
insert into type_mapping values ('text', 'text', false, false, -1, -1);
insert into type_mapping values ('ntext', 'text', false, false, -1, -1);
insert into type_mapping values ('image', 'bytea', false, false, -1, -1);

insert into type_mapping values ('bigint', 'bigint', false, false, -1, -1);
insert into type_mapping values ('smalldatetime', 'timestamp', false, false, -1, -1);
insert into type_mapping values ('real', 'real', false, false, -1, -1);
insert into type_mapping values ('float', 'double precision', false, false, -1, -1);
insert into type_mapping values ('double precision', 'double precision', false, false, -1, -1);

create unique index idx_type_mapping on type_mapping (ss_type);

create table gp_keywords
(
  keyword_id       varchar(100)
);

create unique index idx_gp_keywords on gp_keywords (keyword_id);

insert into gp_keywords values ('abort');
insert into gp_keywords values ('absolute');
insert into gp_keywords values ('access');
insert into gp_keywords values ('action');
insert into gp_keywords values ('active');
insert into gp_keywords values ('add');
insert into gp_keywords values ('admin');
insert into gp_keywords values ('after');
insert into gp_keywords values ('aggregate');
insert into gp_keywords values ('all');
insert into gp_keywords values ('also');
insert into gp_keywords values ('alter');
insert into gp_keywords values ('analyse');
insert into gp_keywords values ('analyze');
insert into gp_keywords values ('and');
insert into gp_keywords values ('any');
insert into gp_keywords values ('array');
insert into gp_keywords values ('as');
insert into gp_keywords values ('asc');
insert into gp_keywords values ('assertion');
insert into gp_keywords values ('assignment');
insert into gp_keywords values ('asymmetric');
insert into gp_keywords values ('at');
insert into gp_keywords values ('authorization');
insert into gp_keywords values ('backward');
insert into gp_keywords values ('before');
insert into gp_keywords values ('begin');
insert into gp_keywords values ('between');
insert into gp_keywords values ('bigint');
insert into gp_keywords values ('binary');
insert into gp_keywords values ('bit');
insert into gp_keywords values ('boolean');
insert into gp_keywords values ('both');
insert into gp_keywords values ('by');
insert into gp_keywords values ('cache');
insert into gp_keywords values ('called');
insert into gp_keywords values ('cascade');
insert into gp_keywords values ('cascaded');
insert into gp_keywords values ('case');
insert into gp_keywords values ('cast');
insert into gp_keywords values ('chain');
insert into gp_keywords values ('char');
insert into gp_keywords values ('character');
insert into gp_keywords values ('characteristics');
insert into gp_keywords values ('check');
insert into gp_keywords values ('checkpoint');
insert into gp_keywords values ('class');
insert into gp_keywords values ('close');
insert into gp_keywords values ('cluster');
insert into gp_keywords values ('coalesce');
insert into gp_keywords values ('collate');
insert into gp_keywords values ('column');
insert into gp_keywords values ('comment');
insert into gp_keywords values ('commit');
insert into gp_keywords values ('committed');
insert into gp_keywords values ('concurrently');
insert into gp_keywords values ('connection');
insert into gp_keywords values ('constraint');
insert into gp_keywords values ('constraints');
insert into gp_keywords values ('conversion');
insert into gp_keywords values ('convert');
insert into gp_keywords values ('copy');
insert into gp_keywords values ('cost');
insert into gp_keywords values ('create');
insert into gp_keywords values ('createdb');
insert into gp_keywords values ('createrole');
insert into gp_keywords values ('createuser');
insert into gp_keywords values ('cross');
insert into gp_keywords values ('csv');
insert into gp_keywords values ('cube');
insert into gp_keywords values ('current');
insert into gp_keywords values ('current_date');
insert into gp_keywords values ('current_role');
insert into gp_keywords values ('current_time');
insert into gp_keywords values ('current_timestamp');
insert into gp_keywords values ('current_user');
insert into gp_keywords values ('cursor');
insert into gp_keywords values ('cycle');
insert into gp_keywords values ('database');
insert into gp_keywords values ('day');
insert into gp_keywords values ('deallocate');
insert into gp_keywords values ('dec');
insert into gp_keywords values ('decimal');
insert into gp_keywords values ('declare');
insert into gp_keywords values ('default');
insert into gp_keywords values ('defaults');
insert into gp_keywords values ('deferrable');
insert into gp_keywords values ('deferred');
insert into gp_keywords values ('definer');
insert into gp_keywords values ('delete');
insert into gp_keywords values ('delimiter');
insert into gp_keywords values ('delimiters');
insert into gp_keywords values ('desc');
insert into gp_keywords values ('disable');
insert into gp_keywords values ('distinct');
insert into gp_keywords values ('distributed');
insert into gp_keywords values ('do');
insert into gp_keywords values ('domain');
insert into gp_keywords values ('double');
insert into gp_keywords values ('drop');
insert into gp_keywords values ('each');
insert into gp_keywords values ('else');
insert into gp_keywords values ('enable');
insert into gp_keywords values ('encoding');
insert into gp_keywords values ('encrypted');
insert into gp_keywords values ('end');
insert into gp_keywords values ('errors');
insert into gp_keywords values ('escape');
insert into gp_keywords values ('every');
insert into gp_keywords values ('except');
insert into gp_keywords values ('exchange');
insert into gp_keywords values ('exclude');
insert into gp_keywords values ('excluding');
insert into gp_keywords values ('exclusive');
insert into gp_keywords values ('execute');
insert into gp_keywords values ('exists');
insert into gp_keywords values ('explain');
insert into gp_keywords values ('external');
insert into gp_keywords values ('extract');
insert into gp_keywords values ('false');
insert into gp_keywords values ('fetch');
insert into gp_keywords values ('fields');
insert into gp_keywords values ('fill');
insert into gp_keywords values ('filter');
insert into gp_keywords values ('first');
insert into gp_keywords values ('float');
insert into gp_keywords values ('following');
insert into gp_keywords values ('for');
insert into gp_keywords values ('force');
insert into gp_keywords values ('foreign');
insert into gp_keywords values ('format');
insert into gp_keywords values ('forward');
insert into gp_keywords values ('freeze');
insert into gp_keywords values ('from');
insert into gp_keywords values ('full');
insert into gp_keywords values ('function');
insert into gp_keywords values ('global');
insert into gp_keywords values ('grant');
insert into gp_keywords values ('granted');
insert into gp_keywords values ('greatest');
insert into gp_keywords values ('group');
insert into gp_keywords values ('group_id');
insert into gp_keywords values ('grouping');
insert into gp_keywords values ('handler');
insert into gp_keywords values ('hash');
insert into gp_keywords values ('having');
insert into gp_keywords values ('header');
insert into gp_keywords values ('hold');
insert into gp_keywords values ('host');
insert into gp_keywords values ('hour');
insert into gp_keywords values ('if');
insert into gp_keywords values ('ignore');
insert into gp_keywords values ('ilike');
insert into gp_keywords values ('immediate');
insert into gp_keywords values ('immutable');
insert into gp_keywords values ('implicit');
insert into gp_keywords values ('in');
insert into gp_keywords values ('including');
insert into gp_keywords values ('inclusive');
insert into gp_keywords values ('increment');
insert into gp_keywords values ('index');
insert into gp_keywords values ('indexes');
insert into gp_keywords values ('inherit');
insert into gp_keywords values ('inherits');
insert into gp_keywords values ('initially');
insert into gp_keywords values ('inner');
insert into gp_keywords values ('inout');
insert into gp_keywords values ('input');
insert into gp_keywords values ('insensitive');
insert into gp_keywords values ('insert');
insert into gp_keywords values ('instead');
insert into gp_keywords values ('int');
insert into gp_keywords values ('integer');
insert into gp_keywords values ('intersect');
insert into gp_keywords values ('interval');
insert into gp_keywords values ('into');
insert into gp_keywords values ('invoker');
insert into gp_keywords values ('is');
insert into gp_keywords values ('isnull');
insert into gp_keywords values ('isolation');
insert into gp_keywords values ('join');
insert into gp_keywords values ('keep');
insert into gp_keywords values ('key');
insert into gp_keywords values ('lancompiler');
insert into gp_keywords values ('language');
insert into gp_keywords values ('large');
insert into gp_keywords values ('last');
insert into gp_keywords values ('leading');
insert into gp_keywords values ('least');
insert into gp_keywords values ('left');
insert into gp_keywords values ('level');
insert into gp_keywords values ('like');
insert into gp_keywords values ('limit');
insert into gp_keywords values ('list');
insert into gp_keywords values ('listen');
insert into gp_keywords values ('load');
insert into gp_keywords values ('local');
insert into gp_keywords values ('localtime');
insert into gp_keywords values ('localtimestamp');
insert into gp_keywords values ('location');
insert into gp_keywords values ('lock');
insert into gp_keywords values ('log');
insert into gp_keywords values ('login');
insert into gp_keywords values ('master');
insert into gp_keywords values ('match');
insert into gp_keywords values ('maxvalue');
insert into gp_keywords values ('merge');
insert into gp_keywords values ('minute');
insert into gp_keywords values ('minvalue');
insert into gp_keywords values ('mirror');
insert into gp_keywords values ('missing');
insert into gp_keywords values ('mode');
insert into gp_keywords values ('modify');
insert into gp_keywords values ('month');
insert into gp_keywords values ('move');
insert into gp_keywords values ('names');
insert into gp_keywords values ('national');
insert into gp_keywords values ('natural');
insert into gp_keywords values ('nchar');
insert into gp_keywords values ('new');
insert into gp_keywords values ('next');
insert into gp_keywords values ('no');
insert into gp_keywords values ('nocreatedb');
insert into gp_keywords values ('nocreaterole');
insert into gp_keywords values ('nocreateuser');
insert into gp_keywords values ('noinherit');
insert into gp_keywords values ('nologin');
insert into gp_keywords values ('none');
insert into gp_keywords values ('noovercommit');
insert into gp_keywords values ('nosuperuser');
insert into gp_keywords values ('not');
insert into gp_keywords values ('nothing');
insert into gp_keywords values ('notify');
insert into gp_keywords values ('notnull');
insert into gp_keywords values ('nowait');
insert into gp_keywords values ('null');
insert into gp_keywords values ('nullif');
insert into gp_keywords values ('numeric');
insert into gp_keywords values ('object');
insert into gp_keywords values ('of');
insert into gp_keywords values ('off');
insert into gp_keywords values ('offset');
insert into gp_keywords values ('oids');
insert into gp_keywords values ('old');
insert into gp_keywords values ('on');
insert into gp_keywords values ('only');
insert into gp_keywords values ('operator');
insert into gp_keywords values ('option');
insert into gp_keywords values ('or');
insert into gp_keywords values ('order');
insert into gp_keywords values ('others');
insert into gp_keywords values ('out');
insert into gp_keywords values ('outer');
insert into gp_keywords values ('over');
insert into gp_keywords values ('overcommit');
insert into gp_keywords values ('overlaps');
insert into gp_keywords values ('overlay');
insert into gp_keywords values ('owned');
insert into gp_keywords values ('owner');
insert into gp_keywords values ('partial');
insert into gp_keywords values ('partition');
insert into gp_keywords values ('partitions');
insert into gp_keywords values ('password');
insert into gp_keywords values ('percent');
insert into gp_keywords values ('placing');
insert into gp_keywords values ('position');
insert into gp_keywords values ('preceding');
insert into gp_keywords values ('precision');
insert into gp_keywords values ('prepare');
insert into gp_keywords values ('prepared');
insert into gp_keywords values ('preserve');
insert into gp_keywords values ('primary');
insert into gp_keywords values ('prior');
insert into gp_keywords values ('privileges');
insert into gp_keywords values ('procedural');
insert into gp_keywords values ('procedure');
insert into gp_keywords values ('queue');
insert into gp_keywords values ('quote');
insert into gp_keywords values ('randomly');
insert into gp_keywords values ('range');
insert into gp_keywords values ('read');
insert into gp_keywords values ('real');
insert into gp_keywords values ('reassign');
insert into gp_keywords values ('recheck');
insert into gp_keywords values ('references');
insert into gp_keywords values ('reindex');
insert into gp_keywords values ('reject');
insert into gp_keywords values ('relative');
insert into gp_keywords values ('release');
insert into gp_keywords values ('rename');
insert into gp_keywords values ('repeatable');
insert into gp_keywords values ('replace');
insert into gp_keywords values ('reset');
insert into gp_keywords values ('resource');
insert into gp_keywords values ('restart');
insert into gp_keywords values ('restrict');
insert into gp_keywords values ('returning');
insert into gp_keywords values ('returns');
insert into gp_keywords values ('revoke');
insert into gp_keywords values ('right');
insert into gp_keywords values ('role');
insert into gp_keywords values ('rollback');
insert into gp_keywords values ('rollup');
insert into gp_keywords values ('row');
insert into gp_keywords values ('rows');
insert into gp_keywords values ('rule');
insert into gp_keywords values ('savepoint');
insert into gp_keywords values ('schema');
insert into gp_keywords values ('scroll');
insert into gp_keywords values ('second');
insert into gp_keywords values ('security');
insert into gp_keywords values ('segment');
insert into gp_keywords values ('select');
insert into gp_keywords values ('sequence');
insert into gp_keywords values ('serializable');
insert into gp_keywords values ('session');
insert into gp_keywords values ('session_user');
insert into gp_keywords values ('set');
insert into gp_keywords values ('setof');
insert into gp_keywords values ('sets');
insert into gp_keywords values ('share');
insert into gp_keywords values ('show');
insert into gp_keywords values ('similar');
insert into gp_keywords values ('simple');
insert into gp_keywords values ('smallint');
insert into gp_keywords values ('some');
insert into gp_keywords values ('split');
insert into gp_keywords values ('stable');
insert into gp_keywords values ('start');
insert into gp_keywords values ('statement');
insert into gp_keywords values ('statistics');
insert into gp_keywords values ('stdin');
insert into gp_keywords values ('stdout');
insert into gp_keywords values ('storage');
insert into gp_keywords values ('strict');
insert into gp_keywords values ('subpartition');
insert into gp_keywords values ('subpartitions');
insert into gp_keywords values ('substring');
insert into gp_keywords values ('superuser');
insert into gp_keywords values ('symmetric');
insert into gp_keywords values ('sysid');
insert into gp_keywords values ('system');
insert into gp_keywords values ('table');
insert into gp_keywords values ('tablespace');
insert into gp_keywords values ('temp');
insert into gp_keywords values ('template');
insert into gp_keywords values ('temporary');
insert into gp_keywords values ('then');
insert into gp_keywords values ('threshold');
insert into gp_keywords values ('ties');
insert into gp_keywords values ('time');
insert into gp_keywords values ('timestamp');
insert into gp_keywords values ('to');
insert into gp_keywords values ('trailing');
insert into gp_keywords values ('transaction');
insert into gp_keywords values ('transform');
insert into gp_keywords values ('treat');
insert into gp_keywords values ('trigger');
insert into gp_keywords values ('trim');
insert into gp_keywords values ('true');
insert into gp_keywords values ('truncate');
insert into gp_keywords values ('trusted');
insert into gp_keywords values ('type');
insert into gp_keywords values ('unbounded');
insert into gp_keywords values ('uncommitted');
insert into gp_keywords values ('unencrypted');
insert into gp_keywords values ('union');
insert into gp_keywords values ('unique');
insert into gp_keywords values ('unknown');
insert into gp_keywords values ('unlisten');
insert into gp_keywords values ('until');
insert into gp_keywords values ('update');
insert into gp_keywords values ('user');
insert into gp_keywords values ('using');
insert into gp_keywords values ('vacuum');
insert into gp_keywords values ('valid');
insert into gp_keywords values ('validation');
insert into gp_keywords values ('validator');
insert into gp_keywords values ('values');
insert into gp_keywords values ('varchar');
insert into gp_keywords values ('varying');
insert into gp_keywords values ('verbose');
insert into gp_keywords values ('view');
insert into gp_keywords values ('volatile');
insert into gp_keywords values ('web');
insert into gp_keywords values ('when');
insert into gp_keywords values ('where');
insert into gp_keywords values ('window');
insert into gp_keywords values ('with');
insert into gp_keywords values ('without');
insert into gp_keywords values ('work');
insert into gp_keywords values ('write');
insert into gp_keywords values ('year');
insert into gp_keywords values ('zone');

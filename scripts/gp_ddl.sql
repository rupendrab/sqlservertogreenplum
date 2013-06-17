create schema sales;

create sequence sales.seq_salesorderheadertest_1
  start with 31466
  increment by 1
  cache 100;

create table sales.salesorderheadertest
(
  salesorderid                             bigint                         not null   default nextval('sales.seq_salesorderheadertest_1'),
  revisionnumber                           smallint                       not null  ,
  orderdate                                timestamp                      not null  ,
  duedate                                  timestamp                      not null  ,
  shipdate                                 timestamp                                ,
  status                                   smallint                       not null  ,
  onlineorderflag                          bit                            not null  ,
  salesordernumber                         varchar(25)                    not null   /* Attn Compute Def=(isnull(N'SO'+CONVERT([nvarchar](23),[SalesOrderID],(0)),N'*** ERROR ***')) */,
  purchaseordernumber                      varchar(25)                              ,
  accountnumber                            varchar(15)                              ,
  customerid                               int                            not null  ,
  salespersonid                            int                                      ,
  territoryid                              int                                      ,
  billtoaddressid                          int                            not null  ,
  shiptoaddressid                          int                            not null  ,
  shipmethodid                             int                            not null  ,
  creditcardid                             int                                      ,
  creditcardapprovalcode                   varchar(15)                              ,
  currencyrateid                           int                                      ,
  subtotal                                 real                           not null  ,
  taxamt                                   numeric(19,4)                  not null  ,
  freight                                  double precision               not null  ,
  totaldue                                 double precision               not null   /* Attn Compute Def=(isnull(([SubTotal]+[TaxAmt])+[Freight],(0))) */,
  "comment"                                varchar(128)                             ,
  --Attn rowguid, uniqueidentifier(uniqueidentifier), not null , default=(newid()), computed=null
  modifieddate                             timestamp                      not null  
)
distributed by (salesorderid)
;



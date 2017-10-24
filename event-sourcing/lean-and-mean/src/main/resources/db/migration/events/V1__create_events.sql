create table events (
   site_id binary(16),
   version int,
   user binary(16),
   timestamp timestamp,
   payload blob,
   primary key (site_id, version desc)
)
engine=innodb;

create table snapshots (              
   site_id binary(16),                
   version int,                       
   owner binary(16),                  
   `blob` blob,                       
   deleted boolean,                   
   primary key (site_id, version desc)
)                                     
engine=innodb;

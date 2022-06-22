/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2022/5/10 17:31:41                           */
/*==============================================================*/


drop table if exists AI_dictionary;

drop table if exists Multiplayer_dictionary;

drop table if exists picture;

drop table if exists user;

/*==============================================================*/
/* Table: AI_dictionary                                         */
/*==============================================================*/
create table AI_dictionary
(
   id                   bigint not null,
   name                 varchar(20),
   primary key (id)
);

/*==============================================================*/
/* Table: Multiplayer_dictionary                                */
/*==============================================================*/
create table Multiplayer_dictionary
(
   id                   bigint not null,
   name                 varchar(20),
   prompt               varchar(50),
   primary key (id)
);

/*==============================================================*/
/* Table: picture                                               */
/*==============================================================*/
create table picture
(
   id                   bigint not null,
   user_id              bigint,
   url                  varchar(200),
   title                varchar(20),
   primary key (id)
);

/*==============================================================*/
/* Table: user                                                  */
/*==============================================================*/
create table user
(
   id                   bigint not null,
   name                 varchar(30),
   openid               varchar(100),
   pic_url              varchar(200),
   hig                  int,
   primary key (id)
);

alter table picture add constraint FK_user_pic foreign key (user_id)
      references user (id) on delete restrict on update restrict;


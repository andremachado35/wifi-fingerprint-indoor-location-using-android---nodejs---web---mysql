drop database if exists PositionbyFingerprint;
create database PositionbyFingerprint;
use PositionbyFingerprint;

create table user_type
(
	id_user_type int not null auto_increment,
	description varchar(255) not null,
	primary key(id_user_type)
);

create table user
(
	id_user int not null auto_increment,
	name varchar(255) not null,
	username varchar(255) not null,
	password varchar(255) not null,
	email varchar(255) not null,
	address varchar(255) not null,
	birthday date not null,
	date_registration date not null,
	id_user_type int not null,
	primary key(id_user),
	foreign key(id_user_type) references user_type(id_user_type) on update cascade on delete cascade
);

create table space
(
	id_space int not null auto_increment,
	name varchar(255) not null,
	description varchar(255) not null,
	map_path varchar(255) not null,
	map_width int not null,
	map_length int not null,
	id_user int not null,
	primary key(id_space),
	foreign key(id_user) references user(id_user) on update cascade on delete cascade
);

create table interest_point
(
	id_interest_point int not null auto_increment,
	name varchar(255) not null,
	coordx int not null,
	coordy int not null,
	id_space int not null,
	primary key(id_interest_point),
	foreign key(id_space) references space(id_space) on update cascade on delete cascade
);

create table reference_point
(
	id_reference_point int not null auto_increment,
	name varchar(255) not null,
	coordx int not null,
	coordy int not null,
	isOffline boolean,
	id_space int not null,
	primary key(id_reference_point),
	foreign key(id_space) references space(id_space) on update cascade on delete cascade
);


create table contribution_history
(
	id_contribution_history int not null auto_increment,
	date timestamp not null,
	coordx_offset int not null,
	coordy_offset int not null,
	id_reference_point int not null,
	id_user int not null,
	primary key(id_contribution_history),
	foreign key(id_reference_point) references reference_point(id_reference_point) on update cascade on delete cascade,
	foreign key(id_user) references user(id_user) on update cascade on delete cascade
);

create table fingerprint
(
	id_fingerprint int not null auto_increment,
	date timestamp not null,
	id_reference_point int not null,
	id_user int not null,
	primary key(id_fingerprint),
	foreign key(id_reference_point) references reference_point(id_reference_point) on update cascade on delete cascade,
	foreign key(id_user) references user(id_user) on update cascade on delete cascade
);

create table ap
(
	id_ap int not null auto_increment,
	ssid varchar(255) not null,
	mac varchar(255) not null,
	primary key(id_ap)
);

create table fingerprint_ap
(
	id_fingerprint_ap int not null auto_increment,
	id_fingerprint int not null,
	id_ap int not null,
	primary key(id_fingerprint_ap),
	foreign key(id_fingerprint) references fingerprint(id_fingerprint) on update cascade on delete cascade,
	foreign key(id_ap) references ap(id_ap) on update cascade on delete cascade
);

create table rssi
(
	id_rssi int not null auto_increment,
	value int not null,
	id_ap int not null,
	id_fingerprint int not null,
	primary key(id_rssi),
	foreign key(id_ap) references ap(id_ap) on update cascade on delete cascade,
	foreign key(id_fingerprint) references fingerprint(id_fingerprint) on update cascade on delete cascade
);

insert into user_type
	values(null,'dono do espaço');

insert into user_type
	values(null,'utilizador premium');

insert into user_type
	values(null,'utilizador basic');

insert into user
	values(null,'admin','admin','pass','email@gmail.com','address','1995-04-25','2018-06-21',2);

insert into space
	values(null,'espaço nulo','desc','path','400','400','1');

insert into interest_point
	values(null,'porta','33','42',1);

insert into reference_point
	values(null,'rf1','42','56',false,1);
	
insert into contribution_history
	values(null,'2018-10-26 09:00:00','56','22',1,1);

insert into fingerprint
	values(null,'2018-10-30 18:00:00',1,1);

insert into ap
	values(null,'ssid','mac');

insert into fingerprint_ap
	values(null,1,1);

insert into rssi
	values(null,78,1,1);

insert into space
	values(null,'s2','d2','path2','500','500','1');

insert into reference_point
	values(null,'rf2','2','2',true,2);

insert into fingerprint
	values(null,'2018-10-31 18:00:00',2,1);

insert into ap
	values(null,'ssid2','mac2');

insert into fingerprint_ap
	values(null,2,2);

insert into rssi
	values(null,82,2,2);

insert into reference_point
	values(null,'rf3','3','3',false,2);

insert into fingerprint
	values(null,'2018-10-31 18:00:00',3,1);

insert into fingerprint_ap
	values(null,3,2);

insert into rssi
	values(null,25,2,3);

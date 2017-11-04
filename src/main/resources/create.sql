create table if not exists messages(
	id int not null auto_increment primary key,
	topic varchar(255),
	payload varchar(10000),
	ts datetime
)
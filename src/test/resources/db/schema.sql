
    create table garbageschedule (
        facilityCategory int(11) check (facilityCategory between 0 and 1),
        id bigint not null auto_increment,
        additionalInformation varchar(255),
        city varchar(255),
        driveSchedule varchar(255),
        houseNumber varchar(255),
        municipality_id varchar(255),
        postalCode varchar(255),
        street varchar(255),
        primary key (id)
    ) engine=InnoDB;

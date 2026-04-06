create table if not exists house
(
    house_id         bigint auto_increment primary key comment '주택 ID',
    name             varchar(128) not null comment '주택명',
    manage_number    bigint       not null comment '주택관리번호',
    apply_date_start date         not null comment '청약 접수 시작일',
    apply_date_end   date         not null comment '청약 접수 종료일',
    house_type       varchar(16)  not null comment '주택구분코드',
    location_type    varchar(16)  not null comment '공급지역코드',
    address          varchar(255) not null comment '공급위치',
    url              varchar(255) not null comment '분양정보 URL'
) comment '주택 청약 정보 테이블';

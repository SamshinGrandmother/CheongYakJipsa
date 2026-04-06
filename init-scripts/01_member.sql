create table if not exists member
(
    member_id    bigint auto_increment comment '사용자 및 관리자의 식별자',
    user_id      varchar(16)  not null comment '사용자 및 관리자가 인증 시 사용하는 ID',
    name         varchar(32)  not null comment '사용자 및 관리자의 이름',
    email        varchar(32)  not null comment '사용자 및 관리자의 이메일',
    password     varchar(128) not null comment '사용자 및 관리자의 패스워드',
    role         varchar(8)   not null comment '사용자 및 관리자의 권한(Enum:Role)',
    phone_number varchar(20) default null comment '사용자 및 관리자의 전화번호',
    delete_type  varchar(6)   not null comment '사용자 및 관리자의 삭제 여부(Enum:DeleteType)',
    updated_at   timestamp    not null comment '사용자 및 관리자의 변경 시각',
    created_at   timestamp    not null comment '사용자 및 관리자의 생성 시각',
    PRIMARY KEY (member_id),
    UNIQUE KEY `member_unique` (`user_id`)
) comment '사용자 및 관리자 테이블';

create table if not exists member_access_history
(
    member_access_history_id             bigint auto_increment primary key comment '사용자 및 관리자 접속 기록 식별자',
    result_type                          varchar(8)  not null comment '사용자 및 관리자 접속 시도 결과',
    member_access_history_member_id      bigint comment '접속 시도한 사용자 및 관리자',
    member_access_history_member_user_id varchar(16) not null comment '존재하지 않는 ID로 로그인 시 기록용 컬럼',
    failure_type                         varchar(32) comment '접속 실패 시 실패 분류 (Enum:AuthenticationFailureType)',
    access_at                            timestamp   not null comment '접속 시도 시각'
) comment '사용자 및 관리자 접속 기록 테이블';

-- 비밀번호 : 1q2w3e4r
INSERT INTO member (user_id, name, email, password, role, delete_type, updated_at, created_at)
VALUES ('gkszm63', '윤정민', 'gkszm636@gmail.com', '$2a$12$ymHIJPOaQQP2pu/275AKaejFRyLQTht./9wZPbPyMqrr5n41uvkuW', 'ADMIN',
        'ACTIVE', now(), now());

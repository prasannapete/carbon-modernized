--liquibase formatted sql

-- Local Variables:
-- mode: sql
-- sql-product: postgres
-- End:
CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

--changeset sharanya:create-oauth2_registered_client
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='oauth2_registered_client')
create table oauth2_registered_client
(
    id                            varchar(100)                            not null
        primary key,
    client_id                     varchar(100)                            not null,
    client_id_issued_at           timestamp default CURRENT_TIMESTAMP     not null,
    client_secret                 varchar(200) null,
    client_secret_expires_at      timestamp default '2038-01-19 03:14:07' not null,
    client_name                   varchar(200)                            not null,
    client_authentication_methods varchar(1000)                           not null,
    authorization_grant_types     varchar(1000)                           not null,
    redirect_uris                 varchar(1000) null,
    scopes                        varchar(1000)                           not null,
    client_settings               varchar(2000)                           not null,
    token_settings                varchar(2000)                           not null,
    post_logout_redirect_uris     varchar(1000) null
);

--changeset sharanya:create-oauth2_authorization
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='oauth2_authorization')
create table oauth2_authorization
(
    id                            varchar(100)                        not null
        primary key,
    registered_client_id          varchar(100)                        not null,
    principal_name                varchar(200)                        not null,
    authorization_grant_type      varchar(100)                        not null,
    authorized_scopes             varchar(1000) null,
    attributes                    bytea null,
    state                         varchar(500) null,
    authorization_code_value      bytea null,
    authorization_code_issued_at  timestamp default CURRENT_TIMESTAMP not null,
    authorization_code_expires_at timestamp default CURRENT_TIMESTAMP not null,
    authorization_code_metadata   bytea null,
    access_token_value            bytea null,
    access_token_issued_at        timestamp default CURRENT_TIMESTAMP not null,
    access_token_expires_at       timestamp default CURRENT_TIMESTAMP not null,
    access_token_metadata         bytea null,
    access_token_type             varchar(100) null,
    access_token_scopes           varchar(1000) null,
    oidc_id_token_value           bytea null,
    oidc_id_token_issued_at       timestamp default CURRENT_TIMESTAMP not null,
    oidc_id_token_expires_at      timestamp default CURRENT_TIMESTAMP not null,
    oidc_id_token_metadata        bytea null,
    refresh_token_value           bytea null,
    refresh_token_issued_at       timestamp default CURRENT_TIMESTAMP not null,
    refresh_token_expires_at      timestamp default CURRENT_TIMESTAMP not null,
    refresh_token_metadata        bytea null
);

--changeset sharanya:create-oauth2_authorization_consent
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='oauth2_authorization_consent')
create table oauth2_authorization_consent
(
    registered_client_id varchar(100)  not null,
    principal_name       varchar(200)  not null,
    authorities          varchar(1000) not null,
    primary key (registered_client_id, principal_name)
);

CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS cb_app_features_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS cb_role_accounts_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS cb_role_grants_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS cb_roles_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS cb_user_seq START WITH 1 INCREMENT BY 50;

--changeset sharanya:create-revinfo
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='revinfo')
CREATE TABLE revinfo
(
    rev      INTEGER NOT NULL,
    revtstmp BIGINT,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

--changeset sharanya:create-cb_app_features
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='cb_app_features')
CREATE TABLE cb_app_features
(
    id                 BIGINT NOT NULL,
    parent_id          BIGINT,
    mib_id             VARCHAR(40),
    feature_name       VARCHAR(255),
    tooltip            VARCHAR(255),
    system_role        VARCHAR(1000),
    url                VARCHAR(1000),
    sequence           INTEGER,
    is_system          INTEGER,
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_cb_app_features PRIMARY KEY (id)
);

--changeset sharanya:create-cb_app_features_aud
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='cb_app_features_aud')
CREATE TABLE cb_app_features_aud
(
    rev                INTEGER NOT NULL,
    revtype            SMALLINT,
    id                 BIGINT  NOT NULL,
    parent_id          BIGINT,
    mib_id             VARCHAR(40),
    feature_name       VARCHAR(255),
    tooltip            VARCHAR(255),
    system_role        VARCHAR(1000),
    url                VARCHAR(1000),
    sequence           INTEGER,
    is_system          INTEGER,
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_cb_app_features_aud PRIMARY KEY (rev, id)
);


--changeset sharanya:create-cb_role_accounts
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='cb_role_accounts')
CREATE TABLE cb_role_accounts
(
    id         BIGINT NOT NULL,
    account_id BIGINT,
    role_id    BIGINT,
    CONSTRAINT pk_cb_role_accounts PRIMARY KEY (id)
);

--changeset sharanya:create-cb_role_grants
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='cb_role_grants')
CREATE TABLE cb_role_grants
(
    id             BIGINT NOT NULL,
    role_id        BIGINT,
    app_feature_id BIGINT,
    CONSTRAINT pk_cb_role_grants PRIMARY KEY (id)
);

--changeset sharanya:create-cb_roles
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='cb_roles')
CREATE TABLE cb_roles
(
    id                 BIGINT       NOT NULL,
    client_id          VARCHAR(40),
    name               VARCHAR(255) NOT NULL,
    home_page          VARCHAR(255),
    description        VARCHAR(1000),
    created_by         VARCHAR(40),
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   VARCHAR(40),
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         VARCHAR(40),
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_cb_roles PRIMARY KEY (id)
);

ALTER TABLE cb_app_features_aud
    ADD CONSTRAINT FK_CB_APP_FEATURES_AUD_ON_REV FOREIGN KEY (rev) REFERENCES revinfo (rev);

ALTER TABLE cb_app_features
    ADD CONSTRAINT FK_CB_APP_FEATURES_ON_PARENT FOREIGN KEY (parent_id) REFERENCES cb_app_features (id);

--changeset sharanya:create-cb_tenant
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='cb_tenant')
create table cb_tenant
(

    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_name        VARCHAR(100)                            NOT NULL,
    tenant_type        BIGINT       NOT NULL,
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    status             INTEGER,
    CONSTRAINT pk_cb_tenant PRIMARY KEY (id)
);

--changeset sharanya:create-srl_user
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='cb_user')
CREATE TABLE cb_user
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id          BIGINT,
    photo_id           BIGINT,
    first_name         VARCHAR(100)                            NOT NULL,
    last_name          VARCHAR(255),
    mobile_number      VARCHAR(30),
    alternative_phone  VARCHAR(30),
    email_address      VARCHAR(255)                            NOT NULL,
    user_name          VARCHAR(255)                            NOT NULL,
    password           VARCHAR(1000),
    account_status     INTEGER,
    is_god             INTEGER,
    is_play_mobil_user integer default 0,
    created_by         VARCHAR(40),
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   VARCHAR(40),
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         VARCHAR(40),
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_cb_user PRIMARY KEY (id)
);

---

--changeset sharanya:create-srl_client_details
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_client_details')
CREATE TABLE srl_client_details
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    client_id          VARCHAR(255),
    tenant_id          BIGINT,
    client_secret      VARCHAR(255),
    country_code       VARCHAR(255),
    market             VARCHAR(255),
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_client_details PRIMARY KEY (id)
);

--changeset sharanya:create-srl_app_launched
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_app_launched')
CREATE TABLE srl_app_launched
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    game_id            VARCHAR(200),
    tenant_id          BIGINT,
    country            VARCHAR(100),
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_app_launched PRIMARY KEY (id)
);

--changeset sharanya:create-srl_app_launched_aud
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_app_launched_aud')
CREATE TABLE srl_app_launched_aud
(
    rev     INTEGER NOT NULL,
    revtype SMALLINT,
    id      BIGINT  NOT NULL,
    game_id  VARCHAR(200),
    tenant_id          BIGINT,
    country            VARCHAR(100),
    CONSTRAINT pk_srl_app_launched_aud PRIMARY KEY (rev, id)
);

--changeset sharanya:create-srl_brand_viewed_garage
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_brand_viewed_garage')
CREATE TABLE srl_brand_viewed_garage
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id          BIGINT,
    brand_count        INTEGER,
    country            VARCHAR(100),
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_brand_viewed_garage PRIMARY KEY (id)
);

--changeset sharanya:create-srl_brand_viewed_garage_aud
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_brand_viewed_garage_aud')
CREATE TABLE srl_brand_viewed_garage_aud
(
    rev         INTEGER NOT NULL,
    revtype     SMALLINT,
    id          BIGINT  NOT NULL,
    tenant_id          BIGINT,
    brand_count INTEGER,
    country     VARCHAR(100),
    CONSTRAINT pk_srl_brand_viewed_garage_aud PRIMARY KEY (rev, id)
);

--changeset sharanya:create-srl_car_unlocked
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_car_unlocked')
CREATE TABLE srl_car_unlocked
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id          BIGINT,
    game_id            VARCHAR(200),
    car_name           VARCHAR(255),
    unlock_method      INTEGER,
    country     VARCHAR(100),
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_car_unlocked PRIMARY KEY (id)
);

--changeset sharanya:create-srl_car_unlocked_aud
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_car_unlocked_aud')
CREATE TABLE srl_car_unlocked_aud
(
    rev                INTEGER NOT NULL,
    revtype            SMALLINT,
    id                 BIGINT  NOT NULL,
    tenant_id          BIGINT,
    car_name           VARCHAR(255),
    unlock_method      INTEGER,
    country            VARCHAR(100),
    game_id            VARCHAR(200),
    CONSTRAINT pk_srl_car_unlocked_aud PRIMARY KEY (rev, id)
);


--changeset sharanya:create-srl_race_played
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_race_played')
CREATE TABLE srl_race_played
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    car_name           VARCHAR(255),
    track_id           INTEGER,
    tenant_id          BIGINT,
    game_id            VARCHAR(200),
    result             INTEGER,
    is_competition     BOOLEAN,
    race_duration        FLOAT,
    brand_count        INTEGER,
    country            VARCHAR(100),
    lap_duration       float default 0.0,
    race_info          varchar(500) default 0.0,
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_race_played PRIMARY KEY (id)
);

--changeset sharanya:create-srl_race_played_aud
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_race_played_aud')
CREATE TABLE srl_race_played_aud
(
    rev            INTEGER NOT NULL,
    revtype        SMALLINT,
    id             BIGINT  NOT NULL,
    tenant_id      BIGINT,
    game_id        VARCHAR(200),
    car_name       VARCHAR(255),
    track_id       INTEGER,
    result         INTEGER,
    is_competition BOOLEAN,
    race_duration    FLOAT,
    brand_count    INTEGER,
    country        VARCHAR(100),
    lap_duration   float default 0.0,
    race_info      varchar(500) default 0.0,
    CONSTRAINT pk_srl_race_played_aud PRIMARY KEY (rev, id)
);

--changeset sharanya:create-srl_rc_played
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_rc_played')
CREATE TABLE srl_rc_played
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id          BIGINT,
    game_id            VARCHAR(200),
    car_name           VARCHAR(255),
    rc_duration        FLOAT,
    country            VARCHAR(100),
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_rc_played PRIMARY KEY (id)
);

--changeset sharanya:create-srl_rc_played_aud
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_rc_played_aud')
CREATE TABLE srl_rc_played_aud
(
    rev         INTEGER NOT NULL,
    revtype     SMALLINT,
    id          BIGINT  NOT NULL,
    tenant_id          BIGINT,
    game_id     VARCHAR(200),
    car_name    VARCHAR(255),
    rc_duration FLOAT,
    country     VARCHAR(100),
    CONSTRAINT pk_srl_rc_played_aud PRIMARY KEY (rev, id)
);


ALTER TABLE srl_app_launched_aud
    ADD CONSTRAINT FK_CB_APP_LAUNCHED_AUD_ON_REV FOREIGN KEY (rev) REFERENCES revinfo (rev);

ALTER TABLE srl_brand_viewed_garage_aud
    ADD CONSTRAINT FK_CB_BRAND_VIEWED_GARAGE_AUD_ON_REV FOREIGN KEY (rev) REFERENCES revinfo (rev);

ALTER TABLE srl_car_unlocked_aud
    ADD CONSTRAINT FK_CB_CAR_UNLOCKED_AUD_ON_REV FOREIGN KEY (rev) REFERENCES revinfo (rev);

ALTER TABLE srl_race_played_aud
    ADD CONSTRAINT FK_CB_RACE_PLAYED_AUD_ON_REV FOREIGN KEY (rev) REFERENCES revinfo (rev);

ALTER TABLE srl_rc_played_aud
    ADD CONSTRAINT FK_CB_RC_PLAYED_AUD_ON_REV FOREIGN KEY (rev) REFERENCES revinfo (rev);


--changeset sharanya:create-srl_leaderboards
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_leaderboards')
CREATE TABLE srl_leaderboards
(
    id                        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id                 BIGINT,
    is_active                 INTEGER,
    created_by                BIGINT,
    creation_time             TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by          BIGINT,
    last_modified_time        TIMESTAMP WITHOUT TIME ZONE,
    is_deleted                INTEGER,
    deleted_by                BIGINT,
    deleted_time              TIMESTAMP WITHOUT TIME ZONE,
    leaderboard_name          VARCHAR(255),
    leaderboard_internal_name VARCHAR(255),
    leaderboard_id            VARCHAR(255),
    CONSTRAINT pk_srl_leaderboards PRIMARY KEY (id)
);

--changeset sharanya:create-srl_offer_code_configuration
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_offer_code_configuration')
CREATE TABLE srl_offer_code_configuration
(
    id                        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id          BIGINT,
    country_code              VARCHAR(255),
    county                    VARCHAR(255),
    event_type                INTEGER,
    offer_code                VARCHAR(255),
    valid_from                TIMESTAMP WITHOUT TIME ZONE,
    valid_until               TIMESTAMP WITHOUT TIME ZONE,
    is_active                 INTEGER,
    validity_period           INTEGER,
    points                    INTEGER,
    created_by                BIGINT,
    creation_time             TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by          BIGINT,
    last_modified_time        TIMESTAMP WITHOUT TIME ZONE,
    is_deleted                INTEGER,
    deleted_by                BIGINT,
    deleted_time              TIMESTAMP WITHOUT TIME ZONE,
    leaderboard_name          VARCHAR(255),
    leaderboard_internal_name VARCHAR(255),
    leaderboard_id            VARCHAR(255),
    CONSTRAINT pk_srl_offer_code_configuration PRIMARY KEY (id)
);


--changeset sharanya:create-srl_offer_code_configuration_aud
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_offer_code_configuration_aud')
CREATE TABLE srl_offer_code_configuration_aud
(
    id                        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id                 BIGINT,
    country_code              VARCHAR(255),
    county                    VARCHAR(255),
    event_type                INTEGER,
    offer_code                VARCHAR(255),
    valid_from                TIMESTAMP WITHOUT TIME ZONE,
    valid_until               TIMESTAMP WITHOUT TIME ZONE,
    is_active                 INTEGER,
    validity_period           INTEGER,
    points                    INTEGER,
    created_by                BIGINT,
    creation_time             TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by          BIGINT,
    last_modified_time        TIMESTAMP WITHOUT TIME ZONE,
    is_deleted                INTEGER,
    deleted_by                BIGINT,
    deleted_time              TIMESTAMP WITHOUT TIME ZONE,
    leaderboard_name          VARCHAR(255),
    leaderboard_internal_name VARCHAR(255),
    leaderboard_id            VARCHAR(255),
    CONSTRAINT pk_srl_offer_code_configuration_aud PRIMARY KEY (id)
);



--changeset sharanya:create-srl_offer_issue_details
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_offer_issue_details')
CREATE TABLE srl_offer_issue_details
(
    id                        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    consumer_uuid             VARCHAR(255),
    tenant_id                 BIGINT,
    game_id                   VARCHAR(255),
    event_type                INTEGER,
    offer_code                VARCHAR(255),
    country_code              VARCHAR(255),
    issued_date               TIMESTAMP WITHOUT TIME ZONE,
    created_by                BIGINT,
    creation_time             TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by          BIGINT,
    last_modified_time        TIMESTAMP WITHOUT TIME ZONE,
    is_deleted                INTEGER,
    deleted_by                BIGINT,
    deleted_time              TIMESTAMP WITHOUT TIME ZONE,
    leaderboard_name          VARCHAR(255),
    leaderboard_internal_name VARCHAR(255),
    leaderboard_id            VARCHAR(255),
    CONSTRAINT pk_srl_offer_issue_details PRIMARY KEY (id)
);

--changeset sharanya:create-srl_offer_issue_details_aud
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_offer_issue_details_aud')
CREATE TABLE srl_offer_issue_details_aud
(
    id                        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id                 BIGINT,
    consumer_uuid             VARCHAR(255),
    game_id                   VARCHAR(255),
    event_type                INTEGER,
    offer_code                VARCHAR(255),
    country_code              VARCHAR(255),
    issued_date               TIMESTAMP WITHOUT TIME ZONE,
    created_by                BIGINT,
    creation_time             TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by          BIGINT,
    last_modified_time        TIMESTAMP WITHOUT TIME ZONE,
    is_deleted                INTEGER,
    deleted_by                BIGINT,
    deleted_time              TIMESTAMP WITHOUT TIME ZONE,
    leaderboard_name          VARCHAR(255),
    leaderboard_internal_name VARCHAR(255),
    leaderboard_id            VARCHAR(255),
    CONSTRAINT pk_srl_offer_issue_details_aud PRIMARY KEY (id)
);

--changeset sharanya:create-srl_user_token
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_user_token')
CREATE TABLE srl_user_token
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    consumer_uuid      VARCHAR(255),
    tenant_id          BIGINT,
    refresh_token      VARCHAR(1000),
    first_name         VARCHAR(500),
    last_name          VARCHAR(500),
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_user_token PRIMARY KEY (id)
);

--changeset sharanya:create-srl_user_token_aud
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_user_token_aud')
CREATE TABLE srl_user_token_aud
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    consumer_uuid      VARCHAR(255),
    tenant_id          BIGINT,
    refresh_token      VARCHAR(1000),
    first_name         VARCHAR(500),
    last_name          VARCHAR(500),
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_user_token_aud PRIMARY KEY (id)
);


--changeset sharanya:create-srl_loyalty_oauth_credentials
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_loyalty_oauth_credentials')
CREATE TABLE srl_loyalty_oauth_credentials
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    country            VARCHAR(255),
    client_id          VARCHAR(255),
    tenant_id          BIGINT,
    client_secret      VARCHAR(255),
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_loyalty_oauth_credentials PRIMARY KEY (id)
);

--changeset sharanya:create-srl_country
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_country')
CREATE TABLE srl_country
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id          BIGINT,
    country_name       VARCHAR(255),
    country            VARCHAR(255),
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_country PRIMARY KEY (id)
);

--changeset sharanya:create-srl_country_aud
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_country_aud')
CREATE TABLE srl_country_aud
(
    rev                INTEGER NOT NULL,
    revtype            SMALLINT,
    id                 BIGINT  NOT NULL,
    tenant_id          BIGINT,
    country_name       VARCHAR(255),
    country            VARCHAR(255),
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_country_aud PRIMARY KEY (rev, id)
);

--changeset sharanya:create-srl_country_car_status
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_country_car_status')
CREATE TABLE srl_country_car_status
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id          BIGINT,
    country            VARCHAR(255),
    car_name           VARCHAR(255),
    is_active          INTEGER,
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_country_car_status PRIMARY KEY (id)
);

--changeset sharanya:create-srl_country_car_status_aud
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_country_car_status_aud')
CREATE TABLE srl_country_car_status_aud
(
    rev                INTEGER NOT NULL,
    revtype            SMALLINT,
    id                 BIGINT  NOT NULL,
    tenant_id          BIGINT,
    country            VARCHAR(255),
    car_name           VARCHAR(255),
    is_active          INTEGER,
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_country_car_status_aud PRIMARY KEY (rev, id)
);

--changeset sharanya:create-srl_app_store_sales_report
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_app_store_sales_report')
CREATE TABLE srl_app_store_sales_report
(
    id                      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id               BIGINT,
    provider                VARCHAR(255),
    provider_country        VARCHAR(255),
    sku                     VARCHAR(255),
    developer               VARCHAR(255),
    title                   VARCHAR(255),
    version                 VARCHAR(255),
    product_type_identifier VARCHAR(255),
    units                   INTEGER,
    developer_proceeds      DOUBLE PRECISION,
    begin_date              TIMESTAMP WITHOUT TIME ZONE,
    end_date                TIMESTAMP WITHOUT TIME ZONE,
    customer_currency       VARCHAR(255),
    country_code            VARCHAR(255),
    currency_of_proceeds    VARCHAR(255),
    apple_identifier        VARCHAR(255),
    customer_price          DOUBLE PRECISION,
    promo_code              VARCHAR(255),
    parent_identifier       VARCHAR(255),
    subscription            VARCHAR(255),
    period VARCHAR (255),
    category                VARCHAR(255),
    cmb                     VARCHAR(255),
    device                  VARCHAR(255),
    supported_platforms     VARCHAR(255),
    proceeds_reason         VARCHAR(255),
    preserved_pricing       VARCHAR(255),
    client                  VARCHAR(255),
    order_type              VARCHAR(255),
    created_by              BIGINT,
    creation_time           TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by        BIGINT,
    last_modified_time      TIMESTAMP WITHOUT TIME ZONE,
    is_deleted              INTEGER,
    deleted_by              BIGINT,
    deleted_time            TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_app_store_sales_report PRIMARY KEY (id)
);

--changeset sharanya:create-srl_app_store_sales_report_aud
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_app_store_sales_report_aud')
CREATE TABLE srl_app_store_sales_report_aud
(
    rev                     INTEGER NOT NULL,
    revtype                 SMALLINT,
    id                      BIGINT  NOT NULL,
    tenant_id               BIGINT,
    provider                VARCHAR(255),
    provider_country        VARCHAR(255),
    sku                     VARCHAR(255),
    developer               VARCHAR(255),
    title                   VARCHAR(255),
    version                 VARCHAR(255),
    product_type_identifier VARCHAR(255),
    units                   INTEGER,
    developer_proceeds      DOUBLE PRECISION,
    begin_date              TIMESTAMP WITHOUT TIME ZONE,
    end_date                TIMESTAMP WITHOUT TIME ZONE,
    customer_currency       VARCHAR(255),
    country_code            VARCHAR(255),
    currency_of_proceeds    VARCHAR(255),
    apple_identifier        VARCHAR(255),
    customer_price          DOUBLE PRECISION,
    promo_code              VARCHAR(255),
    parent_identifier       VARCHAR(255),
    subscription            VARCHAR(255),
    period VARCHAR (255),
    category                VARCHAR(255),
    cmb                     VARCHAR(255),
    device                  VARCHAR(255),
    supported_platforms     VARCHAR(255),
    proceeds_reason         VARCHAR(255),
    preserved_pricing       VARCHAR(255),
    client                  VARCHAR(255),
    order_type              VARCHAR(255),
    created_by              BIGINT,
    creation_time           TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by        BIGINT,
    last_modified_time      TIMESTAMP WITHOUT TIME ZONE,
    is_deleted              INTEGER,
    deleted_by              BIGINT,
    deleted_time            TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_app_store_sales_report_aud PRIMARY KEY (rev, id)
);

--changeset sharanya:create-srl_play_store_report
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_play_store_report')
CREATE TABLE srl_play_store_report
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id          BIGINT,
    date               TIMESTAMP WITHOUT TIME ZONE,
    country            VARCHAR(255),
    country_code       VARCHAR(255),
    installs           BIGINT,
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_play_store_report PRIMARY KEY (id)
);

--changeset sharanya:create-srl_play_store_report_aud
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_play_store_report_aud')
CREATE TABLE srl_play_store_report_aud
(
    rev                INTEGER NOT NULL,
    revtype            SMALLINT,
    id                 BIGINT  NOT NULL,
    tenant_id          BIGINT,
    date               TIMESTAMP WITHOUT TIME ZONE,
    country            VARCHAR(255),
    country_code       VARCHAR(255),
    installs           BIGINT,
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_play_store_report_aud PRIMARY KEY (rev, id)
);

--changeset sharanya:create-srl_master_cars
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_master_cars')
CREATE TABLE srl_master_cars
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id          BIGINT,
    country            VARCHAR(255),
    country_code       VARCHAR(255),
    is_active          INTEGER,
    car_name           VARCHAR(255),
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_master_cars PRIMARY KEY (id)
);

--changeset sharanya:create-srl_master_cars_aud
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_master_cars_aud')
CREATE TABLE srl_master_cars_aud
(
    rev                INTEGER NOT NULL,
    revtype            SMALLINT,
    id                 BIGINT  NOT NULL,
    tenant_id          BIGINT,
    country            VARCHAR(255),
    country_code       VARCHAR(255),
    is_active          INTEGER,
    car_name           VARCHAR(255),
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_master_cars_aud PRIMARY KEY (rev, id)
);

--changeset sharanya:create-srl_code_configuration_translation
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_code_configuration_translation')
CREATE TABLE srl_code_configuration_translation
(
    id                    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    code_configuration_id BIGINT,
    tenant_id             BIGINT,
    lang_code             VARCHAR(255),
    event_title           VARCHAR(255),
    event_reward          TEXT,
    event_description     TEXT,
    event_how_to_claim    TEXT,
    created_by            BIGINT,
    creation_time         TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by      BIGINT,
    last_modified_time    TIMESTAMP WITHOUT TIME ZONE,
    is_deleted            INTEGER,
    deleted_by            BIGINT,
    deleted_time          TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_code_configuration_translation PRIMARY KEY (id)
);

--changeset sharanya:create-srl_code_configuration_translation_aud
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_code_configuration_translation_aud')
CREATE TABLE srl_code_configuration_translation_aud
(
    rev                   INTEGER NOT NULL,
    revtype               SMALLINT,
    id                    BIGINT  NOT NULL,
    code_configuration_id BIGINT,
    tenant_id             BIGINT,
    lang_code             VARCHAR(255),
    event_title           VARCHAR(255),
    event_reward          TEXT,
    event_description     TEXT,
    event_how_to_claim    TEXT,
    created_by            BIGINT,
    creation_time         TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by      BIGINT,
    last_modified_time    TIMESTAMP WITHOUT TIME ZONE,
    is_deleted            INTEGER,
    deleted_by            BIGINT,
    deleted_time          TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_srl_code_configuration_translation_aud PRIMARY KEY (rev, id)
);

--changeset sharanya:create-srl_user_countries
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_user_countries')
CREATE TABLE srl_user_countries
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id          BIGINT,
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    user_id            BIGINT,
    country_id         BIGINT,
    CONSTRAINT pk_srl_user_countries PRIMARY KEY (id)
);

--changeset sharanya:create-srl_app_launched_summary
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_app_launched_summary')
CREATE TABLE srl_app_launched_summary
(
    id                            BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id                     BIGINT,
    created_by                    BIGINT,
    creation_time                 TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by              BIGINT,
    last_modified_time            TIMESTAMP WITHOUT TIME ZONE,
    is_deleted                    INTEGER,
    deleted_by                    BIGINT,
    deleted_time                  TIMESTAMP WITHOUT TIME ZONE,
    country                       VARCHAR(255),
    generated_date                TIMESTAMP WITHOUT TIME ZONE,
    app_launched_count            BIGINT,
    cumulative_app_launched_count BIGINT,
    CONSTRAINT pk_srl_app_launched_summary PRIMARY KEY (id)
);

--changeset sharanya:create-srl_brand_viewed_garage_summary
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_brand_viewed_garage_summary')
CREATE TABLE srl_brand_viewed_garage_summary
(
    id                            BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id                     BIGINT,
    created_by                    BIGINT,
    creation_time                 TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by              BIGINT,
    last_modified_time            TIMESTAMP WITHOUT TIME ZONE,
    is_deleted                    INTEGER,
    deleted_by                    BIGINT,
    deleted_time                  TIMESTAMP WITHOUT TIME ZONE,
    country                       VARCHAR(255),
    generated_date                TIMESTAMP WITHOUT TIME ZONE,
    brand_viewed_count            BIGINT,
    cumulative_brand_viewed_count BIGINT,
    CONSTRAINT pk_srl_brand_viewed_garage_summary PRIMARY KEY (id)
);

--changeset sharanya:create-srl_car_unlocked_summary
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_car_unlocked_summary')
CREATE TABLE srl_car_unlocked_summary
(
    id                        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id                 BIGINT,
    created_by                BIGINT,
    creation_time             TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by          BIGINT,
    last_modified_time        TIMESTAMP WITHOUT TIME ZONE,
    is_deleted                INTEGER,
    deleted_by                BIGINT,
    deleted_time              TIMESTAMP WITHOUT TIME ZONE,
    car_name                  VARCHAR(255),
    country                   VARCHAR(255),
    generated_date            TIMESTAMP WITHOUT TIME ZONE,
    unlocked_count            BIGINT,
    cumulative_unlocked_count BIGINT,
    CONSTRAINT pk_srl_car_unlocked_summary PRIMARY KEY (id)
);

--changeset sharanya:create-srl_race_played_summary
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_race_played_summary')
CREATE TABLE srl_race_played_summary
(
    id                       BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id                BIGINT,
    created_by               BIGINT,
    creation_time            TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by         BIGINT,
    last_modified_time       TIMESTAMP WITHOUT TIME ZONE,
    is_deleted               INTEGER,
    deleted_by               BIGINT,
    deleted_time             TIMESTAMP WITHOUT TIME ZONE,
    car_name                 VARCHAR(255),
    country                  VARCHAR(255),
    generated_date           TIMESTAMP WITHOUT TIME ZONE,
    race_duration            FLOAT,
    cumulative_race_duration VARCHAR(255),
    CONSTRAINT pk_srl_race_played_summary PRIMARY KEY (id)
);

--changeset sharanya:create-srl_rc_played_summary
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='srl_rc_played_summary')
CREATE TABLE srl_rc_played_summary
(
    id                     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id              BIGINT,
    created_by             BIGINT,
    creation_time          TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by       BIGINT,
    last_modified_time     TIMESTAMP WITHOUT TIME ZONE,
    is_deleted             INTEGER,
    deleted_by             BIGINT,
    deleted_time           TIMESTAMP WITHOUT TIME ZONE,
    car_name               VARCHAR(255),
    country                VARCHAR(255),
    generated_date         TIMESTAMP WITHOUT TIME ZONE,
    rc_duration            FLOAT,
    cumulative_rc_duration VARCHAR(255),
    CONSTRAINT pk_srl_rc_played_summary PRIMARY KEY (id)
);

--changeset sharanya:create-kk_app_launched
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='kk_app_launched')
CREATE TABLE kk_app_launched
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    game_id            VARCHAR(255),
    console_id         INTEGER,
    tenant_id          BIGINT,
    player_name        VARCHAR(255),
    country            VARCHAR(255),
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_kk_app_launched PRIMARY KEY (id)
);

--changeset sharanya:create-kk_car_unlocked
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='kk_car_unlocked')
CREATE TABLE kk_car_unlocked
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    game_id            VARCHAR(255),
    console_id         INTEGER,
    tenant_id          BIGINT,
    player_name        VARCHAR(255),
    country            VARCHAR(255),
    car_name           VARCHAR(255),
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_kk_car_unlocked PRIMARY KEY (id)
);

--changeset sharanya:create-kk_brand_viewed_garage
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='kk_brand_viewed_garage')
CREATE TABLE kk_brand_viewed_garage
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    game_id            VARCHAR(255),
    console_id         INTEGER,
    tenant_id          BIGINT,
    player_name        VARCHAR(255),
    country            VARCHAR(255),
    brand_count        INTEGER,
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_kk_brand_viewed_garage PRIMARY KEY (id)
);

--changeset sharanya:create-kk_race_played
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='kk_race_played')
CREATE TABLE kk_race_played
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    game_id            VARCHAR(255),
    console_id         INTEGER,
    tenant_id          BIGINT,
    player_name        VARCHAR(255),
    country            VARCHAR(255),
    car_name           VARCHAR(255),
    track_id           INTEGER,
    result             INTEGER,
    race_duration      FLOAT,
    lap_duration       FLOAT,
    brand_count        INTEGER,
    race_info          VARCHAR(255),
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_kk_race_played PRIMARY KEY (id)
);

--changeset sharanya:create-cb_carbon_event_participants
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='cb_carbon_event_participants')
CREATE TABLE cb_carbon_event_participants
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id           BIGINT,
    tenant_id          BIGINT,
    name               VARCHAR(255),
    age                INTEGER,
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_carbon_event_participants PRIMARY KEY (id)
);

--changeset sharanya:create-cb_carbon_events
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='cb_carbon_events')
CREATE TABLE cb_carbon_events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id          BIGINT,
    event_name         VARCHAR(255),
    description        VARCHAR(255),
    start_date         TIMESTAMP WITHOUT TIME ZONE,
    end_date           TIMESTAMP WITHOUT TIME ZONE,
    score_type         INTEGER DEFAULT 0,
    is_live            INTEGER DEFAULT 0,
    logo_path          VARCHAR(255),
    primary_color      VARCHAR(255),
    event_type         VARCHAR(255),
    console_id         INTEGER,
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_cb_carbon_events PRIMARY KEY (id)
);

--changeset sharanya:create-cb_carbon_event_scores
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='cb_carbon_event_scores')
CREATE TABLE cb_carbon_event_scores
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id           BIGINT,
    participant_id     BIGINT,
    tenant_id          BIGINT,
    score              DOUBLE PRECISION,
    time               VARCHAR(255),
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_cb_carbon_event_scores PRIMARY KEY (id)
);

--changeset sharanya:create-cb_schema_metadata
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='cb_schema_metadata')
CREATE TABLE cb_schema_metadata
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    tenant_id          BIGINT,
    schema_version     DOUBLE PRECISION NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INT ,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_cb_schema_metadata PRIMARY KEY (id)
);

--changeset sharanya:create-cb_players
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='cb_players')
CREATE TABLE cb_players
(
    id                 VARCHAR(100) NOT NULL,
    opta_id            VARCHAR(100) NULL,
    tenant_id          BIGINT,
    name               VARCHAR(255) NOT NULL,
    shirt_name         VARCHAR(255) NULL,
    number             INTEGER NULL,
    position           INTEGER,
    team               VARCHAR(255) NULL,
    dob                TIMESTAMP WITHOUT TIME ZONE NULL,
    nationality        VARCHAR(100) NULL,
    home_town          VARCHAR(255) NULL,
    kit                VARCHAR(100) NULL,
    schema_metadata_id BIGINT,
    features           JSONB NULL,
    is_published       boolean default false,
    is_edited          boolean default false,
    height             bigint,
    weight             bigint,
    preferred_foot     int,
    captain            boolean,
    vice_captain       boolean,
    country_of_birth   text,
    age                int,
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE ,
    CONSTRAINT pk_cb_players PRIMARY KEY (id)
);

--changeset sharanya:create-cb_player_feature_mapping
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='cb_player_feature_mapping')
CREATE TABLE cb_player_feature_mapping
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    feature_type_id    VARCHAR(50) NULL,
    tenant_id          BIGINT,
    feature_type_name  VARCHAR(255) NULL,
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE ,
    CONSTRAINT pk_cb_player_feature_mapping PRIMARY KEY (id)
);

CREATE INDEX idx_player_feature_mapping_code
    ON cb_player_feature_mapping (feature_type_id, is_deleted);


--changeset sharanya:create-cb_players_copy_data
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.tables where table_name='cb_players_copy_data')
CREATE TABLE cb_players_copy_data
(
    id                 VARCHAR(255) NOT NULL,
    opta_id            VARCHAR(100),
    tenant_id          BIGINT,
    name               VARCHAR(255) NOT NULL,
    shirt_name         VARCHAR(255),
    number             INTEGER,
    position           INTEGER,
    team               VARCHAR(255),
    dob                date,
    nationality        VARCHAR(100),
    home_town          VARCHAR(255),
    kit                VARCHAR(100),
    schema_metadata_id BIGINT,
    features           JSONB,
    height             DOUBLE PRECISION,
    weight             DOUBLE PRECISION,
    preferred_foot     INTEGER,
    captain            BOOLEAN,
    vice_captain       BOOLEAN,
    country_of_birth   VARCHAR(255),
    age                INTEGER,
    created_by         BIGINT,
    creation_time      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by   BIGINT,
    last_modified_time TIMESTAMP WITHOUT TIME ZONE,
    is_deleted         INTEGER,
    is_published       BOOLEAN,
    deleted_by         BIGINT,
    deleted_time       TIMESTAMP WITHOUT TIME ZONE,
    is_edited          BOOLEAN,
    CONSTRAINT pk_cb_players_copy_data PRIMARY KEY (id)
);

--changeset sharanya:cb_user-add-is_play_mobil_user
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.columns where table_name='cb_user' and column_name='is_play_mobil_user')
alter table cb_user
    add is_play_mobil_user integer default 0;

--changeset sharanya:cb_roles-add-tenant_id
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.columns where table_name='cb_roles' and column_name='tenant_id')
alter table cb_roles
    add tenant_id BIGINT;

--changeset sharanya:cb_app_features-add-mib_id
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.columns where table_name='cb_app_features' and column_name='mib_id')
ALTER TABLE cb_app_features ADD COLUMN mib_id VARCHAR(40);

--changeset sharanya:cb_app_features_aud-add-mib_id
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.columns where table_name='cb_app_features_aud' and column_name='mib_id')
ALTER TABLE cb_app_features_aud ADD COLUMN mib_id VARCHAR(40);

--changeset sharanya:cb_app_features-add-tenant_id
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.columns where table_name='cb_app_features' and column_name='tenant_id')
ALTER TABLE cb_app_features add tenant_id BIGINT;

--changeset sharanya:cb_app_features_aud-add-tenant_id
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:f select exists(select 1 from information_schema.columns where table_name='cb_app_features_aud' and column_name='tenant_id')
ALTER TABLE cb_app_features_aud add tenant_id BIGINT;


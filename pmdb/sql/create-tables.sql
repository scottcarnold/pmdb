// the structure of this table is generally set up to match default Spring security ddl; however password type changed to store bcrypt passwords
CREATE TABLE users(
    username      VARCHAR(50) NOT NULL PRIMARY KEY,
    password      BINARY(60) NOT NULL,
    enabled       TINYINT(1) NOT NULL
);

// the structure of this table is set up to match default Spring security ddl
CREATE TABLE authorities(
    username      VARCHAR(50) NOT NULL,
    authority     VARCHAR(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(username),
    CONSTRAINT ix_auth_username UNIQUE INDEX (username, authority)
);

CREATE TABLE user_details(
    username      VARCHAR(50) NOT NULL,
    firstName     VARCHAR(50),
    lastName      VARCHAR(50),
    email         VARCHAR(200),
    createdTs     TIMESTAMP NOT NULL,
    updatedTs     TIMESTAMP NOT NULL,
    lastAccessTs  TIMESTAMP,
    CONSTRAINT fk_user_details_users FOREIGN KEY(username) REFERENCES users(username)
);
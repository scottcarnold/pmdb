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

CREATE TABLE collection(
    id            INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(100) NOT NULL,
    owner         VARCHAR(50) NOT NULL,
    CONSTRAINT fk_collection_owner FOREIGN KEY(owner) REFERENCES users(username)
);

CREATE TABLE collection_permission(
    collection_id INTEGER NOT NULL,
    username      VARCHAR(50) NOT NULL,
    allowEdit     TINYINT(1) NOT NULL,
    accepted      TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_collection_permission_users FOREIGN KEY(username) REFERENCES users(username),
    CONSTRAINT fk_collection_permission_collection FOREIGN KEY(collection_id) REFERENCES collection(id),
    CONSTRAINT ix_collection_id_username UNIQUE INDEX (collection_id, username)
);

CREATE TABLE collection_default(
    username      VARCHAR(50) NOT NULL PRIMARY KEY,
    collection_id INTEGER NOT NULL,
    CONSTRAINT fk_collection_default_users FOREIGN KEY(username) REFERENCES users(username),
    CONSTRAINT fk_collection_default_collection FOREIGN KEY(collection_id) REFERENCES collection(id)
);

CREATE TABLE movie(
    id            INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    title         VARCHAR(200) NOT NULL,
    collection_id INTEGER NOT NULL,
    CONSTRAINT fk_movie_collection FOREIGN KEY(collection_id) REFERENCES collection(id)
);

CREATE TABLE movie_attributes(
    movie_id      INTEGER NOT NULL,
    attribute_name VARCHAR(50),
    attribute_value VARCHAR(200),
    CONSTRAINT fk_movie_attributes_movie FOREIGN KEY(movie_id) REFERENCES movie(id)	ON DELETE CASCADE,
    CONSTRAINT ix_movie_attr_name UNIQUE INDEX (movie_id, attribute_name)
);

CREATE TABLE movie_attributes_table_columns(
    username      VARCHAR(50) NOT NULL,
    idx           INTEGER NOT NULL,
    attribute_name VARCHAR(50),
    CONSTRAINT fk_movie_attributes_order_users FOREIGN KEY(username) REFERENCES users(username),
    CONSTRAINT ix_mao_username_idx UNIQUE INDEX (username, idx) 
);

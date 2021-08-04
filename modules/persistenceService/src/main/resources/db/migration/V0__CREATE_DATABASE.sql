create table userdb(
    id uuid PRIMARY KEY NOT NULL,
    username VARCHAR UNIQUE NOT NULL,
    name     VARCHAR NOT NULL
);
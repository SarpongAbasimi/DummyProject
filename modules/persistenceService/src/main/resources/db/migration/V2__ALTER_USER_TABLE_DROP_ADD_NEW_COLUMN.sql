ALTER TABLE userdb
DROP COLUMN username,
DROP COLUMN name,
ADD COLUMN slack_user_id VARCHAR UNIQUE NOT NULL,
ADD COLUMN slack_channel_id uuid UNIQUE NOT NULL;

CREATE TABLE repositories(
    repository_id uuid PRIMARY KEY NOT NULL,
    owner VARCHAR NOT NULL,
    repository VARCHAR NOT NULL,
    CONSTRAINT unique_repos UNIQUE (owner, repository)
);

CREATE TABLE subscriptions(
    id uuid NOT NULL,
    repository_id uuid NOT NULL,
    PRIMARY KEY(id, repository_id),
    FOREIGN KEY(id) REFERENCES userdb(id) ON UPDATE CASCADE,
    FOREIGN KEY(repository_id) REFERENCES repositories(repository_id) ON UPDATE CASCADE
);
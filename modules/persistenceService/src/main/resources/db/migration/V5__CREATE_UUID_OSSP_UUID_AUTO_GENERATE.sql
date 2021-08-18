CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
ALTER TABLE userdb ALTER column id SET DEFAULT uuid_generate_v4();
ALTER TABLE repositories ALTER column repository_id SET DEFAULT uuid_generate_v4();
ALTER TABLE subscriptions
ADD COLUMN subscribed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
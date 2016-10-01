# --- !Ups

create table "users" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"provider_id" VARCHAR(254) NOT NULL,"provider_key" VARCHAR(254) NOT NULL,"first_name" VARCHAR(254) NOT NULL,"last_name" VARCHAR(254) NOT NULL,"full_name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL,"avatar_url" VARCHAR(254) NOT NULL,"activated" BOOLEAN,"explicit_content_auth" BOOLEAN,"created_at" TIMESTAMP DEFAULT now() NOT NULL,"edited_at" TIMESTAMP DEFAULT now() NOT NULL);

create table "auth_tokens" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"user_id" BIGINT NOT NULL,"expiry" TIMESTAMP NOT NULL,"created_at" TIMESTAMP DEFAULT now() NOT NULL,"edited_at" TIMESTAMP DEFAULT now() NOT NULL);

# --- !Downs
drop table "users";
drop table "auth_tokens";
# --- !Ups

create table "users" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"provider_id" VARCHAR(254) NOT NULL,"provider_key" VARCHAR(254) NOT NULL,"first_name" VARCHAR(254) NOT NULL,"last_name" VARCHAR(254) NOT NULL,"full_name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL,"avatar_url" VARCHAR(254) NOT NULL,"activated" BOOLEAN,"explicit_content_auth" BOOLEAN,"created_at" TIMESTAMP DEFAULT now() NOT NULL,"edited_at" TIMESTAMP DEFAULT now() NOT NULL);
create unique index "IDX_EMAIL" on "users" ("email");

create table "auth_tokens" ("id" BIGSERIAL NOT NULL PRIMARY KEY, "token" UUID NOT NULL, "user_id" BIGINT NOT NULL,"expiry" TIMESTAMP NOT NULL,"created_at" TIMESTAMP DEFAULT now() NOT NULL,"edited_at" TIMESTAMP DEFAULT now() NOT NULL);
alter table "auth_tokens" add constraint "oauth_tokens_users_fk" foreign key("user_id") references "users"("id") on update NO ACTION on delete NO ACTION;

# --- !Downs
drop table "users";
drop table "auth_tokens";
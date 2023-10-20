CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table if not exists shards (
      shard_id integer primary key,
      created_date timestamp without time zone not null default (now() at time zone 'utc')
);

create table if not exists dialog_shards (
     key_hash integer primary key,
     shard_id integer not null references shards (shard_id),
     resharding boolean default false,
     created_date timestamp without time zone not null default (now() at time zone 'utc')
);

create table if not exists dialog_messages (
   uuid uuid primary key default uuid_generate_v4(),
   key_hash integer references dialog_shards(key_hash),
   shard_id integer not null, -- not constraint here for resharding
   from_user_id integer not null,
   to_user_id integer not null,
   message text not null,
   created_date timestamp without time zone not null default (now() at time zone 'utc')
);

insert into shards (shard_id)
values (1), (2), (3), (88);
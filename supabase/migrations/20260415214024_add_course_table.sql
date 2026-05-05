create table courses (
  id          uuid primary key default gen_random_uuid(),
  subject     text not null,
  number      integer not null,
  section     text not null,
  name        text not null,
  credits     integer not null default 0,
  location    text,
  semester    text not null,
  is_lab      boolean not null default false,
  is_open     boolean not null default false,
  open_seats  integer not null default 0,
  total_seats integer not null default 0,
  faculty     text[] not null default '{}',
  created_at  timestamptz default now(),

  unique (subject, number, section, semester)
);
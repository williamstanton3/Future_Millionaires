create table students (
  id         integer primary key,
  created_at timestamptz default now()
);

create table schedules (
  id               uuid primary key default gen_random_uuid(),
  student_id       integer not null references students(id) on delete cascade,
  semester         text not null,
  is_finalized     boolean not null default false,
  active_semester  boolean not null default false,
  credits          integer not null default 0,
  created_at       timestamptz default now(),
  unique (student_id, semester)
);

create table schedule_courses (
  id          uuid primary key default gen_random_uuid(),
  schedule_id uuid not null references schedules(id) on delete cascade,
  course_id   uuid not null references courses(id) on delete cascade,
  unique (schedule_id, course_id)
);
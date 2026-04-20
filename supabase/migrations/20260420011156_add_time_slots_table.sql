create table time_slots (
  id         uuid primary key default gen_random_uuid(),
  course_id  uuid not null references courses(id) on delete cascade,
  day        text not null,
  start_time time not null,
  end_time   time not null
);
export default function ClassBlock({ course }) {
  return (
    <div className="class-block">
      <strong>{course.name}</strong>
      <div>{course.start} - {course.end}</div>
    </div>
  );
}
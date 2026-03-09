export default function ClassBlock({ course, style }) {
  return (
    <div className="class-block" style={style}>
      <strong>{course.name}</strong>
      <div>{course.start} - {course.end}</div>
    </div>
  );
}
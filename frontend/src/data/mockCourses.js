// Mock courses array
export const courses = [
  {
    id: 1,
    subject: "CS",
    number: 101,
    name: "Intro to Computer Science",
    section: "A",
    semester: "2024_Fall",
    credits: 3,
    faculty: ["Dr. Smith"],
    location: "Room 101",
    isLab: false,
    isOpen: true,
    openSeats: 10,
    totalSeats: 30,
    times: [
      { day: "M", start_time: "09:00", end_time: "10:15" },
      { day: "W", start_time: "09:00", end_time: "10:15" }
    ]
  },
  {
    id: 2,
    subject: "MATH",
    number: 201,
    name: "Calculus I",
    section: "B",
    semester: "2024_Fall",
    credits: 4,
    faculty: ["Prof. Johnson"],
    location: "Room 203",
    isLab: false,
    isOpen: true,
    openSeats: 5,
    totalSeats: 25,
    times: [
      { day: "T", start_time: "10:30", end_time: "11:45" },
      { day: "R", start_time: "10:30", end_time: "11:45" }
    ]
  },
  {
    id: 3,
    subject: "ENG",
    number: 150,
    name: "English Literature",
    section: "C",
    semester: "2024_Fall",
    credits: 3,
    faculty: ["Dr. Lee"],
    location: "Room 305",
    isLab: false,
    isOpen: true,
    openSeats: 12,
    totalSeats: 30,
    times: [
      { day: "M", start_time: "11:00", end_time: "12:15" },
      { day: "W", start_time: "11:00", end_time: "12:15" },
      { day: "F", start_time: "11:00", end_time: "12:15" }
    ]
  },
  {
    id: 4,
    subject: "PHYS",
    number: 101,
    name: "General Physics",
    section: "A",
    semester: "2024_Fall",
    credits: 4,
    faculty: ["Dr. Brown"],
    location: "Lab 2",
    isLab: true,
    isOpen: true,
    openSeats: 8,
    totalSeats: 20,
    times: [
      { day: "T", start_time: "13:00", end_time: "14:15" },
      { day: "R", start_time: "13:00", end_time: "14:15" }
    ]
  }
];
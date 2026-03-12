// src/App.jsx
import { useState } from "react";
import WeeklySchedule from "./components/Schedule/WeeklySchedule";
import { Input } from "./components/ui/input";

function App() {
  const [search, setSearch] = useState("");

  return (
    <div className="p-4 flex flex-col gap-2 w-full max-w-4xl mx-auto">
      <WeeklySchedule />
      <Input
        placeholder="Search..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        className="bg-gray-800 text-white placeholder-gray-400 rounded-md shadow-md w-full"
      />
    </div>
  );
}

export default App;
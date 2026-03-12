import { useState } from "react";
import WeeklySchedule from "./components/Schedule/WeeklySchedule";
import FilterSection from "./components/Filter/FilterSection";
import SearchBar from "./components/Search/SearchBar";

function App() {
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState({});

  const handleFilter = (newFilter) => {
    console.log("Filter applied:", newFilter);
    setFilter(newFilter);
    // TODO: filter courses in WeeklySchedule
  };

  return (
    <div className="p-4 flex flex-col gap-4 w-full max-w-5xl mx-auto">
      <WeeklySchedule />

      {/* Search */}
      <SearchBar value={search} onChange={(e) => setSearch(e.target.value)} />

      {/* Filter Section */}
      <FilterSection onFilter={handleFilter} />
    </div>
  );
}

export default App;
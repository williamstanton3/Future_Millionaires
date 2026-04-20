import React, { useState } from "react";
import { Combobox, ComboboxInput, ComboboxContent, ComboboxList, ComboboxItem, ComboboxEmpty } from "../../ui/combobox";

export default function CourseNumberCombobox({ numbers = [], value, onChange }) {
  const [search, setSearch] = useState("");

  const filtered = numbers.filter(n =>
    n.toString().toLowerCase().includes((search ?? "").toString().toLowerCase())
  );

  return (
    <Combobox
      value={value}
      onValueChange={(val) => {
        onChange(val);
        setSearch(val != null ? String(val) : "");
      }}
    >
      <ComboboxInput
        placeholder="Course #"
        value={search}
        onChange={(e) => {
          setSearch(e.target.value);
          onChange(null);
        }}
        onBlur={() => {
          setSearch(value != null ? String(value) : "");
        }}
        showClear={!!value}
        className="w-32"
      />
      <ComboboxContent>
        <ComboboxList>
          {filtered.length === 0 ? (
            <ComboboxEmpty>No courses found.</ComboboxEmpty>
          ) : (
            filtered.map((n) => (
              <ComboboxItem key={n} value={n}>{n}</ComboboxItem>
            ))
          )}
        </ComboboxList>
      </ComboboxContent>
    </Combobox>
  );
}

import React, { useState } from "react";
import { Combobox, ComboboxInput, ComboboxContent, ComboboxList, ComboboxItem, ComboboxEmpty } from "../../ui/combobox";

export default function DepartmentCombobox({ departments = [], value, onChange }) {
  const [search, setSearch] = useState("");

  const filtered = departments.filter(d =>
    d.toLowerCase().includes((search ?? "").toLowerCase())
  );

  return (
    <Combobox
      value={value}
      onValueChange={(val) => {
        onChange(val);
        setSearch(val ?? "");
      }}
    >
      <ComboboxInput
        placeholder="Department"
        value={search}
        onChange={(e) => {
          setSearch(e.target.value);
          onChange(null);
        }}
        onBlur={() => {
          setSearch(value ?? "");
        }}
        showClear={!!value}
        className="w-full"
      />
      <ComboboxContent>
        <ComboboxList>
          {filtered.length === 0 ? (
            <ComboboxEmpty>No departments found.</ComboboxEmpty>
          ) : (
            filtered.map((d) => (
              <ComboboxItem key={d} value={d}>{d}</ComboboxItem>
            ))
          )}
        </ComboboxList>
      </ComboboxContent>
    </Combobox>
  );
}

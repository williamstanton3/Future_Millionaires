import React, { useState } from "react";
import { Combobox, ComboboxInput, ComboboxContent, ComboboxList, ComboboxItem, ComboboxEmpty } from "../../ui/combobox";

export default function ProfessorCombobox({ professors = [], value, onChange }) {
  const [search, setSearch] = useState("");

  const filtered = professors.filter(p =>
    p.toLowerCase().includes((search ?? "").toLowerCase())
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
        placeholder="Professor"
        value={search}
        onChange={(e) => {
          setSearch(e.target.value);
          onChange(null);
        }}
        onBlur={() => {
          setSearch(value ?? "");
        }}
        showClear={!!value}
        className="w-48"
      />
      <ComboboxContent>
        <ComboboxList>
          {filtered.length === 0 ? (
            <ComboboxEmpty>No professors found.</ComboboxEmpty>
          ) : (
            filtered.map((p) => (
              <ComboboxItem key={p} value={p}>{p}</ComboboxItem>
            ))
          )}
        </ComboboxList>
      </ComboboxContent>
    </Combobox>
  );
}

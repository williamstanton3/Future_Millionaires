import React from "react";
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "../../ui/select";

export default function CreditsSelect({ creditOptions = [], value, onChange }) {
  return (
    <Select value={value} onValueChange={(val) => onChange(val === "all" ? "" : val)}>
      <SelectTrigger className="w-full">
        <SelectValue placeholder="Credits" />
      </SelectTrigger>
      <SelectContent>
        <SelectItem value="all">Any</SelectItem>
        {creditOptions.map(c => (
          <SelectItem key={c} value={c}>{c}</SelectItem>
        ))}
      </SelectContent>
    </Select>
  );
}

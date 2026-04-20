import React from "react";
import { Input } from "../../ui/input";

export default function KeywordInput({ keyword, onChange }) {
  return (
    <Input
      placeholder="Keyword"
      value={keyword}
      onChange={e => onChange(e.target.value)}
      className="bg-gray-800 text-white placeholder-gray-400 w-96"
    />
  );
}

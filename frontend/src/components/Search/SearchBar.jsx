import React from "react";
import { Input } from "../ui/input";

export default function SearchBar({ value, onChange }) {
  return (
    <Input
      placeholder="Search..."
      value={value}
      onChange={onChange}
      className="bg-gray-800 text-white placeholder-gray-400 rounded-md shadow-md w-full"
    />
  );
}
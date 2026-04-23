import React from "react";
import {
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";

export default function ConflictTitle({ message }) {
  return (
    <DialogHeader className="px-8 pt-6 border-b border-gray-800">
      <DialogTitle className="text-red-400 text-2xl">
        Time Conflict Detected
      </DialogTitle>
      <DialogDescription className="text-gray-300 text-base mt-1">
        {message}
      </DialogDescription>
    </DialogHeader>
  );
}
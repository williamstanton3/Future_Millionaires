import React, { useState, useEffect } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselPrevious,
  CarouselNext,
} from "@/components/ui/carousel";

import "./Schedule/Schedule.css";

const DAYS = ["Mon", "Tue", "Wed", "Thu", "Fri"];
const DAY_MAP = { M: "Mon", T: "Tue", W: "Wed", R: "Thu", F: "Fri" };

/* ---------------- NORMALIZATION ---------------- */

function normalizeCourse(course) {
  if (!course) return null;

  const toStr = (v) => {
    if (v == null) return null;
    if (Array.isArray(v))
      return `${String(v[0]).padStart(2, "0")}:${String(v[1]).padStart(2, "0")}`;
    return String(v);
  };

  const times = (course.times ?? [])
    .map((t) => {
      const start = toStr(t.start_time ?? t.start);
      const end = toStr(t.end_time ?? t.end);
      const day = DAY_MAP[t.day] ?? t.day;

      if (!day || !start || !end) return null;
      return { day, start, end };
    })
    .filter(Boolean);

  return {
    ...course,
    times,
    color: course.color ?? "#3b82f6",
  };
}

function extractSchedule(s) {
  if (!s) return [];
  const raw =
    s.schedule ?? s.courses ?? s.getSchedule?.() ?? (Array.isArray(s) ? s : []);
  return Array.isArray(raw) ? raw : [];
}

function extractCredits(s) {
  return s?.credits ?? s?.getCredits?.() ?? 0;
}

/* ---------------- MAIN ---------------- */

export default function ConflictModal({
  open,
  message,
  suggestedSchedules,
  onAccept,
  onClose,
}) {
  const [carouselApi, setCarouselApi] = useState(null);
  const [selectedIdx, setSelectedIdx] = useState(0);

  useEffect(() => {
    if (!carouselApi) return;

    const update = () => setSelectedIdx(carouselApi.selectedScrollSnap());
    update();
    carouselApi.on("select", update);

    return () => carouselApi.off("select", update);
  }, [carouselApi]);

  if (!suggestedSchedules?.length) return null;

  const total = suggestedSchedules.length;
  const currentSchedule = suggestedSchedules[selectedIdx];

  const handleAccept = () => {
    onAccept(currentSchedule);
    onClose();
  };

  return (
    <Dialog open={open} onOpenChange={(o) => !o && onClose()}>
      <DialogContent className="!w-[50vw] !max-w-none h-[92vh] bg-gray-900 text-white border border-gray-700 p-0 flex flex-col overflow-hidden">
        {/* Header */}
        <DialogHeader className="px-8 pt-6 pb-4 border-b border-gray-800">
          <DialogTitle className="text-red-400 text-2xl">
            Time Conflict Detected
          </DialogTitle>
          <DialogDescription className="text-gray-300 text-base mt-1">
            {message}
          </DialogDescription>
        </DialogHeader>

        {/* Carousel Container */}
        <div className="flex-1 flex flex-col min-h-0 px-8 py-6">
          <Carousel
            setApi={setCarouselApi}
            opts={{ loop: false, align: "start" }}
            className="w-full flex-1 flex flex-col"
          >
            {/* Top navigation */}
            <div className="flex items-center justify-center gap-6 mb-6 text-sm text-gray-400">
              <CarouselPrevious className="h-9 w-9" />
              <span className="font-medium">
                {selectedIdx + 1} of {total}
              </span>
              <CarouselNext className="h-9 w-9" />
            </div>

            <CarouselContent className="flex-1 min-h-0">
              {suggestedSchedules.map((suggestion, i) => {
                const courses = extractSchedule(suggestion)
                  .map(normalizeCourse)
                  .filter(Boolean);

                const credits = extractCredits(suggestion);

                return (
                  <CarouselItem key={i} className="h-full">
                    <div className="flex flex-col h-full gap-5">
                      {/* Info bar */}
                      <div className="flex-shrink-0 text-sm text-gray-400">
                        {courses.length} course{courses.length !== 1 ? "s" : ""}{" "}
                        · {credits} credits
                      </div>

                      {/* IMPROVED SCHEDULE GRID */}
                      <div className="flex-1 min-h-0 border border-gray-700 rounded-xl bg-gray-950 overflow-hidden grid grid-cols-[70px_repeat(5,1fr)]">
                        {/* Time labels column */}
                        <div className="border-r border-gray-700 flex flex-col text-[10px] text-gray-500 relative">
                          {Array.from({ length: 13 }, (_, i) => {
                            const hour = 8 + i;
                            return (
                              <div
                                key={i}
                                className="flex-1 flex items-start justify-end pr-2 border-b border-gray-800 last:border-b-0"
                              >
                                <span className="translate-y-[-4px]">
                                  {hour}:00
                                </span>
                              </div>
                            );
                          })}
                        </div>

                        {/* Day columns */}
                        {DAYS.map((day) => (
                          <div
                            key={day}
                            className="relative border-r border-gray-700 last:border-r-0 flex flex-col"
                          >
                            {/* Day header */}
                            <div className="h-10 bg-gray-900 border-b border-gray-700 flex items-center justify-center text-xs font-semibold text-gray-400">
                              {day}
                            </div>

                            {/* Time blocks container */}
                            <div className="flex-1 relative">
                              {courses.flatMap((c, cIdx) =>
                                c.times
                                  .filter((t) => t.day === day)
                                  .map((t, tIdx) => {
                                    const toHour = (time) => {
                                      const [h, m] = time.split(":").map(Number);
                                      return h + m / 60;
                                    };

                                    const startHour = toHour(t.start);
                                    const endHour = toHour(t.end);

                                    // 8am–8pm = 12 hours
                                    const startPct = Math.max(
                                      0,
                                      ((startHour - 8) / 12) * 100
                                    );
                                    const heightPct = Math.max(
                                      6,
                                      ((endHour - startHour) / 12) * 100
                                    );

                                    return (
                                      <div
                                        key={`${cIdx}-${tIdx}`}
                                        className="absolute left-2 right-2 rounded-md text-white text-[10px] px-2 py-1 shadow-md overflow-hidden flex flex-col justify-center"
                                        style={{
                                          top: `${startPct}%`,
                                          height: `${heightPct}%`,
                                          backgroundColor: c.color,
                                          minHeight: "22px",
                                        }}
                                      >
                                        <div className="font-medium leading-none truncate">
                                          {c.subject} {c.number}
                                        </div>
                                      </div>
                                    );
                                  })
                              )}
                            </div>
                          </div>
                        ))}
                      </div>

                      {/* Course tags */}
                      <div className="flex flex-wrap gap-2 flex-shrink-0">
                        {courses.map((c, j) => (
                          <span
                            key={j}
                            className="px-3 py-1 rounded text-xs font-medium text-white"
                            style={{ backgroundColor: c.color }}
                          >
                            {c.subject} {c.number}
                          </span>
                        ))}
                      </div>
                    </div>
                  </CarouselItem>
                );
              })}
            </CarouselContent>
          </Carousel>
        </div>

        {/* Footer buttons */}
        <div className="flex justify-end gap-3 px-8 py-5 border-t border-gray-800">
          <Button
            variant="outline"
            onClick={onClose}
            className="border-gray-600 text-gray-300 hover:bg-gray-800 px-6"
          >
            Cancel
          </Button>
          <Button
            onClick={handleAccept}
            className="bg-blue-600 hover:bg-blue-700 px-8"
          >
            Accept This Schedule
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
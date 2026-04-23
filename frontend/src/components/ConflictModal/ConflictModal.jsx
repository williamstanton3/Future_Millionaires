import React, { useState, useEffect } from "react";
import {
  Dialog,
  DialogContent,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselPrevious,
  CarouselNext,
} from "@/components/ui/carousel";

import "../Schedule/Schedule.css";

import ConflictTitle from "./ConflictTitle";
import ConflictSchedule from "./ConflictSchedule";
import ConflictCourseList from "./ConflictCourseList";

/* ---------------- NORMALIZATION ---------------- */

const DAY_MAP = { M: "Mon", T: "Tue", W: "Wed", R: "Thu", F: "Fri" };

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
  const raw = s.schedule ?? s.courses ?? s.getSchedule?.() ?? (Array.isArray(s) ? s : []);
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
      <DialogContent className="!w-[65vw] !max-w-none bg-gray-900 text-white border border-gray-700 p-0 flex flex-col" style={{ maxHeight: "90vh" }}>
        {/* Header — fixed */}
        <ConflictTitle message={message} />

        {/* Scrollable middle — schedule + course list */}
        <div className="flex-1 overflow-y-auto px-8 py-4 min-h-0">
          <Carousel
            setApi={setCarouselApi}
            opts={{ loop: false, align: "start" }}
            className="w-full"
          >
            <div className="flex items-center justify-center gap-4 mb-0 text-gray-400">
              <CarouselPrevious className="h-6 w-6" inline />
              <span className="text-gray-300 text-base">
                {selectedIdx + 1} of {total}
              </span>
              <CarouselNext className="h-6 w-6" inline />
            </div>

            <CarouselContent>
              {suggestedSchedules.map((suggestion, i) => {
                const courses = extractSchedule(suggestion)
                  .map(normalizeCourse)
                  .filter(Boolean);
                const credits = extractCredits(suggestion);

                return (
                  <CarouselItem key={i}>
                    <div className="flex flex-col gap-4">
                      <ConflictSchedule courses={courses} credits={credits} />
                      <ConflictCourseList courses={courses} />
                    </div>
                  </CarouselItem>
                );
              })}
            </CarouselContent>
          </Carousel>
        </div>

        {/* Footer — fixed at bottom */}
        <div className="flex-shrink-0 flex justify-end gap-3 px-8 py-5 border-t border-gray-800">
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

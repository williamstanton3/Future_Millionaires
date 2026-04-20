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
  useCarousel,
} from "@/components/ui/carousel";

import TimeColumn from "./Schedule/TimeColumn";
import DayColumn from "./Schedule/DayColumn";
import "./Schedule/Schedule.css";

const DAYS = ["Mon", "Tue", "Wed", "Thu", "Fri"];

/* ---------------- SAFE NORMALIZATION ---------------- */

function normalizeCourse(course) {
  if (!course) return null;

  const times = (course.times ?? []).map((t) => {
    const toStr = (v) =>
      Array.isArray(v)
        ? `${String(v[0]).padStart(2, "0")}:${String(v[1]).padStart(2, "0")}`
        : v;

    return { day: t.day, start: toStr(t.start), end: toStr(t.end) };
  });

  return {
    ...course,
    times,
    color: course.color ?? "#3b82f6",
  };
}

/* ---------------- SAFE EXTRACTORS ---------------- */

function extractSchedule(s) {
  return s?.schedule || s?.courses || s?.getSchedule?.() || [];
}

function extractCredits(s) {
  return s?.credits ?? s?.getCredits?.() ?? 0;
}

/* ---------------- CAROUSEL COUNTER ---------------- */

function CarouselCounter({ total }) {
  const { api } = useCarousel();
  const [current, setCurrent] = useState(1);

  useEffect(() => {
    if (!api) return;

    const update = () => {
      setCurrent(api.selectedScrollSnap() + 1);
    };

    update();
    api.on("select", update);

    return () => api.off("select", update);
  }, [api]);

  return (
    <span className="text-sm text-gray-400">
      {current} of {total}
    </span>
  );
}

/* ---------------- MAIN COMPONENT ---------------- */

export default function ConflictModal({
  open,
  message,
  suggestedSchedules,
  onAccept,
  onClose,
}) {
  const [carouselApi, setCarouselApi] = useState(null);
  const [selectedIdx, setSelectedIdx] = useState(0);

  /* sync index with carousel */
  useEffect(() => {
    if (!carouselApi) return;

    const update = () => {
      setSelectedIdx(carouselApi.selectedScrollSnap());
    };

    update();
    carouselApi.on("select", update);

    return () => carouselApi.off("select", update);
  }, [carouselApi]);

  if (!suggestedSchedules?.length) return null;

  const total = suggestedSchedules.length;
  const current = suggestedSchedules[selectedIdx];

  const courses = extractSchedule(current).map(normalizeCourse);
  const credits = extractCredits(current);

  const handleAccept = () => {
    onAccept(current);
    onClose();
  };

  return (
    <Dialog open={open} onOpenChange={(o) => !o && onClose()}>
      {/* 🔥 FIX 1: FLEX + HEIGHT CONSTRAINT */}
      <DialogContent className="
        !max-w-[95vw]
        w-full
        max-h-[90vh]
        overflow-hidden
        bg-gray-900 text-white border border-gray-700
        flex flex-col
      ">
        <DialogHeader>
          <DialogTitle className="text-red-400 text-lg">
            Time Conflict Detected
          </DialogTitle>
          <DialogDescription className="text-gray-300">
            {message}
          </DialogDescription>
        </DialogHeader>

        {/* 🔥 FIX 2: FLEX CONTAINER FOR CAROUSEL */}
        <div className="flex flex-col flex-1 min-h-0">

          <Carousel
            setApi={setCarouselApi}
            opts={{ loop: false }}
            className="w-full px-8 flex-1 min-h-0"
          >
            <div className="flex items-center justify-center gap-4 mb-2 text-sm text-gray-300 leading-none">
              <CarouselPrevious inline className="w-7 h-7 flex items-center justify-center p-0 shrink-0" />
              <CarouselCounter total={total} />
              <CarouselNext inline className="w-7 h-7 flex items-center justify-center p-0 shrink-0" />
            </div>

            <CarouselContent>
              {suggestedSchedules.map((suggestion, i) => {
                const slideCourses = extractSchedule(suggestion).map(normalizeCourse);
                const slideCredits = extractCredits(suggestion);

                return (
                  <CarouselItem key={i} className="basis-full min-h-0">
                    {/* 🔥 FIX 3: INTERNAL SCROLL AREA */}
                    <div className="flex flex-col gap-3 h-full overflow-y-auto pr-2">

                      {/* header */}
                      <div className="text-sm text-gray-400">
                        {slideCourses.length} course
                        {slideCourses.length !== 1 ? "s" : ""} · {slideCredits} credits
                      </div>

                      {/* schedule grid */}
                      <div className="schedule-wrapper rounded-md overflow-hidden border border-gray-700 flex-shrink-0">
                        <div className="schedule max-h-[40vh] overflow-y-auto">
                          <TimeColumn />
                          {DAYS.map((day) => (
                            <DayColumn
                              key={day}
                              day={day}
                              courses={slideCourses}
                              onCourseClick={() => {}}
                            />
                          ))}
                        </div>
                      </div>

                      {/* course list */}
                      <div className="flex flex-col gap-1 max-h-32 overflow-y-auto">
                        {slideCourses.map((c, j) => (
                          <div
                            key={j}
                            className="flex items-center gap-2 text-sm text-gray-300"
                          >
                            <span
                              className="w-2.5 h-2.5 rounded-full flex-shrink-0"
                              style={{ backgroundColor: c.color }}
                            />
                            <span className="font-medium">
                              {c.subject} {c.number} {c.section}
                            </span>
                            <span className="text-gray-500">—</span>
                            <span>{c.name}</span>
                          </div>
                        ))}
                      </div>

                    </div>
                  </CarouselItem>
                );
              })}
            </CarouselContent>
          </Carousel>

        </div>

        {/* actions */}
        <div className="flex justify-end gap-3 pt-3">
          <Button
            variant="outline"
            onClick={onClose}
            className="border-gray-600 text-gray-300 hover:bg-gray-800"
          >
            Cancel
          </Button>

          <Button
            onClick={handleAccept}
            className="bg-blue-600 hover:bg-blue-700 text-white"
          >
            Accept This Schedule
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
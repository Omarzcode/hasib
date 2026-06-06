# Digital Muhasabah — Complete Project Reference Document
*Paste this in full at the start of every Claude Code session*

---

## 1. Product Vision

A local-first, privacy-focused Android app for daily spiritual habit tracking (Muhasabah). No social features, no cloud sync, no continuous notifications. Users engage once or twice a day to log their spiritual habits (wirds). The core value is a smart analytics engine that reads patterns and acts as an empathetic spiritual coach.

---

## 2. Core Architecture Decisions

- **Platform:** Native Android only
- **Database:** Room (SQLite wrapper)
- **Architecture:** Local-first, zero data sent to any server
- **Language support:** Arabic (RTL primary) + English
- **Profiles:** Single user profile per device (user_id field included for future-proofing)

---

## 3. The Wird Preset System

Three locked tiers. Users select one at onboarding and can change it in settings.

| Tier | Locked Core Wirds |
|------|-------------------|
| Stage 1: Fard Focus | 5 Daily Prayers |
| Stage 2: Sunnah & Adhkar | Stage 1 + Rawatib + Morning/Evening Adhkar |
| Stage 3: Advanced Devotion | Stage 2 + Tahajjud + Daily Quran Reading |

- Locked wirds cannot be deleted by the user
- Users can add unlimited custom wirds on top of any tier
- Downgrading tier hides custom wirds added for that tier — does not delete them

---

## 4. Core Product Decisions

### Group 1 — The Daily Log

| Decision | Answer |
|----------|--------|
| Edit after submission | 24-hour window only — locked after that |
| Missed day (no log) | Neutral flag — not counted as wird failure, used only in app consistency metrics |
| Prayer logging | 3 states per prayer: on-time / late / missed |

### Group 2 — Wird Structure

| Decision | Answer |
|----------|--------|
| Wird frequency | User-defined at creation: daily / weekly / custom days |
| Deleting a custom wird | Soft delete — historical data preserved and shown in reports, new failures stop counting from deletion date |
| Wird type | User chooses at creation: boolean (done/not done) OR quantitative (user sets numeric target + unit) |

### Group 3 — The Insights Engine

| Decision | Answer |
|----------|--------|
| Day boundaries | Fajr to Fajr — full 24 hours. User sets Fajr time in onboarding, default 5:00 AM |
| Burnout trigger scope | Custom wirds only — locked wirds receive motivational ayah/hadith instead |
| Coaching trigger timing | Shown immediately on next app open after trigger fires |

### Group 4 — Streaks and Scoring

| Decision | Answer |
|----------|--------|
| Complete day definition | 100% of all wirds — used only for the level-up trigger |
| Streak definition | Logging consistency only — not completion rate |
| Streak display | One master logging streak + lifetime stats. No per-wird streaks |
| Insights format | Cumulative totals in reports (e.g. 1200 pages read, 45000 adhkar this month) |

---

## 5. The Insights Engine Logic

### Burnout Prevention Trigger
- Condition: any custom wird completion drops below 40% over a rolling 10-day window
- Action: show a gentle bottom sheet prompt on next app open
- Options presented to user: pause for 1 week / reduce frequency / continue as-is
- Does not fire for locked wirds — those show an ayah or hadith instead
- After user dismisses, does not re-fire for the same wird for 30 days

### Level-Up Trigger
- Condition: 100% completion across all wirds for 14 consecutive days
- Action: encouraging milestone message asking if user wants to add a new custom wird

### Timing Bottleneck Trigger
- Condition: engine detects correlation between late log submission times and lower completion rates
- Action: targeted suggestion (e.g. "When you log past 11PM, your Adhkar completion drops 50%")

---

## 6. Database Schema (Room)

### Table 1: user_settings
```
user_id          INTEGER  (always 1, future-proofing only)
fajr_time        TEXT     (e.g. "05:00")
preset_tier      INTEGER  (1, 2, or 3)
language         TEXT     ("ar" or "en")
notification_time TEXT    (e.g. "22:00")
created_at       TEXT     (ISO timestamp)
```

### Table 2: wirds
```
wird_id          INTEGER  PRIMARY KEY
user_id          INTEGER
name             TEXT
is_locked        BOOLEAN  (true = core preset wird, cannot be deleted)
type             TEXT     ("boolean" or "quantitative")
target_value     INTEGER  (null if boolean)
target_unit      TEXT     (e.g. "pages", "times" — null if boolean)
frequency        TEXT     ("daily", "weekly", or "custom")
frequency_detail TEXT     (e.g. "MON,THU" if custom — null if daily)
tier_required    INTEGER  (1, 2, or 3 — which preset unlocks this wird)
is_deleted       BOOLEAN  (soft delete flag)
deleted_at       TEXT     (null if active)
created_at       TEXT
```

### Table 3: daily_logs
```
log_id           INTEGER  PRIMARY KEY
user_id          INTEGER
log_date         TEXT     (the Fajr-cycle date, e.g. "2024-03-15")
submitted_at     TEXT     (actual device timestamp of first entry)
last_edited_at   TEXT     (null if never edited)
is_missed        BOOLEAN  (true if zero wirds marked when day closed)
```

### Table 4: wird_entries
```
entry_id         INTEGER  PRIMARY KEY
log_id           INTEGER  FOREIGN KEY → daily_logs
wird_id          INTEGER  FOREIGN KEY → wirds
status           TEXT     ("done", "missed", "skipped")
value_completed  INTEGER  (null if boolean wird)
recorded_at      TEXT     (timestamp of last update)
```

### Table 5: prayer_entries
```
prayer_id        INTEGER  PRIMARY KEY
log_id           INTEGER  FOREIGN KEY → daily_logs
prayer_name      TEXT     ("fajr", "dhuhr", "asr", "maghrib", "isha")
status           TEXT     ("on_time", "late", "missed")
recorded_at      TEXT
```

### Table 6: coaching_events
```
event_id         INTEGER  PRIMARY KEY
user_id          INTEGER
event_type       TEXT     ("burnout", "level_up", "timing")
wird_id          INTEGER  (null if not wird-specific)
triggered_at     TEXT
seen_at          TEXT     (null until user dismisses it)
action_taken     TEXT     ("paused", "reduced", "dismissed", "ignored")
```

### Relationships
- One user → many wirds
- One wird → many wird_entries
- One daily_log → many wird_entries + many prayer_entries
- One daily_log = one Fajr cycle day
- Coaching events reference a wird when relevant

---

## 7. Screen Wireframes

### Screen 1 — التسجيل اليومي (Daily Check-in)
```
- Header: Gregorian date + Hijri date
- Section 1 — locked wirds:
    - 5 prayers, each with 3-state toggle
      (on-time / late / missed)
    - Other locked wirds below prayers
- Section 2 — custom wirds:
    - Boolean wirds: tap once to mark done,
      tap again to unmark
    - Quantitative wirds: tap to open
      number input
- No save button
- Data records automatically at Fajr
- If zero wirds marked at Fajr = missed day flag
- Day stays editable until next Fajr
- New day rolls over silently in background
```

### Screen 2 — السلاسل (Streaks)
```
- Hero element: current logging streak (large number)
- Longest streak ever (all time record)
- Total days logged (lifetime count)
```

### Screen 3 — التحليلات (Reports)
```
- Tabs: أسبوعي / شهري / سنوي

Weekly tab:
    - Completion % per wird
    - Cumulative totals (pages read,
      adhkar count, etc.)
    - Most missed wird + miss frequency bar

Monthly tab:
    - Growth % vs previous month per wird
    - Category balance wheel
      (Prayer vs Quran vs Remembrance)

Yearly tab:
    - Aggregate lifetime totals
    - Macro spiritual seasons overview
```

### Screen 4 — السجل (History)
```
- Full month calendar view
- Each day color-coded:
    complete / partial / missed / empty
- Tap a day → summary bottom sheet
    (completion % + list of missed wirds)
- Tap expand → full wird-by-wird detail
- Edit icon visible only within 24h window
- Beyond 24h = read only, no edit icon
```

### Screen 5 — الإعدادات (Settings)
```
- Fajr time picker
- Preset tier selector
    (shows warning on downgrade)
- App language: Arabic / English
- Notification time picker
- Export data (JSON or CSV file)
- Reset app:
    user must type RESET to confirm
    full wipe of all data and settings
```

---

## 8. Key Rules to Never Break

1. A missed day (no log) is NEVER counted as a wird failure — it is only a logging consistency flag
2. Soft-deleted wirds keep all historical data but stop accumulating new entries
3. Locked wirds never receive burnout suggestions — only motivational Islamic content
4. The streak measures logging consistency, not wird completion
5. All data stays on device — nothing is ever transmitted externally
6. The day boundary is always Fajr to Fajr, not midnight to midnight
7. Past logs beyond 24 hours are read-only and cannot be edited

---

*End of reference document*

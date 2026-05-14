# CityBrain Pilot Evaluation Metrics

## 1. Purpose

This document defines how to evaluate a limited CityBrain cafeteria congestion pilot.

The goal is not to prove that the system is perfect.  
The goal is to check whether people-count-based congestion statistics can support cafeteria operation and student guidance without storing original video.

---

## 2. Pilot Scope

Recommended pilot scope:

| Item | Recommended Setting |
|---|---|
| Pilot period | 1 week |
| Operation days | Monday to Friday |
| Operation time | Lunch peak period |
| Example time window | 11:30 to 13:30 |
| Measurement interval | 1 minute or 5 minutes |
| Main data | Person count, congestion level, timestamp |
| Excluded data | Original video, face image, student identity, movement path |

---

## 3. Core Evaluation Questions

The pilot should answer the following questions.

1. Can students check cafeteria congestion before visiting?
2. Can operators review congestion changes by time period?
3. Can congestion data support operation decisions?
4. Can camera-based person counting work as an auxiliary data source without POS or kiosk integration?
5. Can the system run without storing original video?
6. Can admin-only pages and CSV exports be protected from public access?

---

## 4. Technical Metrics

| Metric | Description | Success Direction |
|---|---|---|
| Data collection count | Number of saved congestion records | More records during operation time |
| Collection success rate | Ratio of successful records to total attempts | Higher is better |
| Failure count | Failed records caused by server/camera/network issues | Lower is better |
| Average person count | Average detected people during pilot period | Used for trend analysis |
| Maximum person count | Highest detected people count | Used to identify peak congestion |
| Congestion distribution | Count by congestion level | Used to understand overall flow |
| Hourly average count | Average people count by hour | Used for lunch peak analysis |
| CSV export availability | Whether data can be downloaded | Must work |
| Admin guard behavior | Admin pages require admin key | Must work |
| Student page availability | Student-facing page remains public | Must work |

---

## 5. Operational Metrics

| Metric | Description |
|---|---|
| Peak time identification | Whether the system identifies crowded time periods |
| Operator usefulness | Whether the report helps cafeteria operation review |
| Student usefulness | Whether the congestion page helps students decide when to visit |
| Manual comparison | Difference between YOLO count and human observation |
| Recovery handling | Whether operators can understand failure states |
| Privacy notice readiness | Whether pilot notice text and camera purpose are prepared |

---

## 6. Manual Observation Comparison

During the pilot, a human observer can record approximate people count at selected times.

Example:

| Time | Manual Count | YOLO Count | Difference | Note |
|---|---:|---:|---:|---|
| 11:30 | 5 | 4 | 1 | Entrance partially blocked |
| 12:00 | 12 | 10 | 2 | Crowd overlap |
| 12:30 | 8 | 8 | 0 | Clear view |
| 13:00 | 3 | 3 | 0 | Low congestion |

The purpose is not to demand perfect accuracy.  
The purpose is to check whether the trend is useful enough for student guidance and operation review.

---

## 7. Success Criteria

A limited pilot can be considered successful if the following conditions are met.

| Area | Success Criteria |
|---|---|
| Data collection | Congestion records are saved during the configured time window |
| Privacy | Original video and face images are not stored |
| Admin control | Admin pages and CSV export require an admin key |
| Student access | Student congestion page remains accessible |
| Report | Pilot report page summarizes records correctly |
| Operation | CSV and report can support weekly review |
| Feasibility | School can decide whether to continue, stop, or expand |

---

## 8. Failure Criteria

The pilot should be reviewed or stopped if the following issues occur.

| Issue | Reason |
|---|---|
| Original video is stored unintentionally | Privacy risk |
| Admin pages are publicly accessible | Operation/security risk |
| Camera angle captures unnecessary private activity | Privacy risk |
| Data is too inaccurate for trend analysis | Low operational value |
| Server frequently stops during operation time | Reliability issue |
| Students are not notified about the pilot | Transparency issue |

---

## 9. Report Output

CityBrain v9.7 provides a pilot report page.

Admin report page:

    http://127.0.0.1:8080/admin/vision-pilot-report?admin_key=citybrain-local-admin

The report includes:

- total log count
- average detected people
- max/min detected people
- congestion-level distribution
- hourly people-count summary
- recent logs
- school-facing summary text

---

## 10. Conclusion

CityBrain should be evaluated as a privacy-minimized campus operation support MVP.

The most important question is not whether YOLO is perfectly accurate.  
The most important question is whether lightweight congestion statistics can help students and operators make better decisions without storing original video.

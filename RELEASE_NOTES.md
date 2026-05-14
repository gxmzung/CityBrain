# CityBrain v9.0 Campus Pilot Release Notes

## Summary

CityBrain v9.0 is a campus pilot-ready MVP for student cafeteria congestion visibility and operation data reporting.

The project now supports:

- YOLO-based person detection for congestion estimation
- CityBrain backend proxy API integration
- Student-facing AI congestion status page
- Admin-facing AI congestion report page
- Congestion history logging
- CSV export for operation review
- Campus pilot proposal documentation
- Vision privacy handling draft
- Operator runbook

---

## Version History

### v8.3 - YOLO Congestion Demo

Added a standalone YOLO-based congestion estimation demo.

### v8.4 - Vision API Integration

Connected the YOLO congestion module to the CityBrain backend through `/api/vision/congestion`.

### v8.5 - Student Vision Status Page

Added `/student/vision-status` for student-facing AI congestion display.

### v8.6 - Admin Vision Report

Added `/admin/vision-report` for administrator-facing congestion report and operation guidance.

### v8.7 - Vision History Logging

Added SQLite-based congestion history logging and history APIs.

### v8.8 - CSV Export

Added CSV export for recorded vision congestion data.

### v8.9 - Campus Pilot Documents

Added campus pilot proposal, privacy/video handling draft, and operator runbook.

### v9.0 - Campus Pilot Ready Package

Reorganized README and release documentation for school pilot review.

---

## Key URLs

```text
http://127.0.0.1:8080/student/vision-status
http://127.0.0.1:8080/admin/vision-report
http://127.0.0.1:8080/admin/vision-history
http://127.0.0.1:8080/api/vision/congestion
http://127.0.0.1:8080/api/vision/history/export.csv
Honest Limits

This is still a local MVP.

Before real school deployment, the following must be reviewed:

CCTV access permission
privacy and video processing policy
authentication and role-based access control
server operation responsibility
camera position and notice signage
accuracy under real cafeteria conditions
failure handling and manual operation mode

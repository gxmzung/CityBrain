# CityBrain CCTV Privacy and Congestion Research

## 1. Research Purpose

This document summarizes the legal, operational, and technical considerations for using a camera-based congestion estimation module in the CityBrain campus cafeteria MVP.

CityBrain uses a YOLO-based vision module to estimate cafeteria congestion by counting people from a webcam or RTSP camera stream. The goal is not to identify individuals, but to generate operational statistics such as people count, congestion level, and measurement time.

This research is intended to support a limited campus pilot proposal, not a final legal judgment. Before real deployment, the school privacy officer, facility management department, and cafeteria operator should review the plan.

---

## 2. Core Position

CityBrain should be positioned as:

    A people-count-based congestion statistics module,
    not a facial recognition or personal tracking system.

The safest operating principle is:

    No original video storage
    No face recognition
    No individual tracking
    No student identity collection
    Only people count, congestion level, and timestamp

---

## 3. Relevant CCTV and Video Processing Considerations

CCTV or fixed cameras require careful review because a person's image, movement, and activity information may be captured regardless of the person's intent.

However, camera use for statistical purposes such as visitor counting can be considered differently when the video is not stored and is only temporarily processed to calculate statistical values.

For CityBrain, this means the vision module should be designed around the following conditions:

- The camera input is used only for temporary person detection.
- Original video frames are not stored.
- Face images are not stored.
- The result is converted into statistics immediately.
- The stored data contains only people count, congestion level, and time.
- The system purpose is limited to cafeteria congestion guidance and operation review.

---

## 4. CityBrain Privacy-Minimization Principle

CityBrain's privacy-minimization design should follow these principles.

| Principle | CityBrain Design Direction |
|---|---|
| Purpose limitation | Use only for cafeteria congestion guidance and operation improvement |
| Data minimization | Store only people count, congestion status, timestamp, and source |
| No original video storage | Do not save video files or image captures |
| No identification | Do not perform face recognition or student identification |
| No tracking | Do not track individuals across time or locations |
| Limited scope | Use camera angle focused on entrance or queue area only |
| Limited operation | Start with a short pilot period and limited time window |
| Access control | Restrict admin pages and CSV access in future versions |
| Transparency | Provide notice signage before real-world pilot operation |

---

## 5. Data Handled by CityBrain Vision Module

CityBrain may handle the following data during a pilot.

| Data | Description | Stored? |
|---|---|---|
| Camera frame | Temporary input for YOLO person detection | No |
| person_count | Number of detected people | Yes |
| congestion | Status such as low, normal, crowded | Yes |
| updated_at | Time when the vision module updated the result | Yes |
| saved_at | Time when CityBrain saved the record | Yes |
| source | Webcam, RTSP camera, or module source | Yes |
| method | Detection method description | Yes |
| raw image/video | Original visual data | No |
| face image | Face image or feature | No |
| student identity | Student ID or personal identifier | No |

---

## 6. Pilot Notice Signage Draft

If CityBrain is tested in an actual cafeteria space, a notice should be prepared so students can recognize the camera-based pilot.

Example notice:

    CityBrain 학생식당 혼잡도 안내 파일럿 운영 안내

    본 구역에서는 학생식당 혼잡도 안내 및 운영 개선 가능성 검토를 위해
    AI 기반 사람 수 통계 측정 파일럿이 진행될 수 있습니다.

    - 설치 목적: 학생식당 혼잡도 안내 및 운영 개선 검토
    - 처리 방식: 카메라 영상에서 사람 수 통계값만 산출
    - 저장 데이터: 사람 수, 혼잡도, 측정 시각
    - 저장하지 않는 데이터: 영상 원본, 얼굴 이미지, 개인 식별 정보
    - 촬영 범위: 학생식당 입구 또는 대기열 일부
    - 운영 시간: 파일럿 기간 중 지정 시간대
    - 관리 부서/담당자: 학교 협의 후 지정
    - 문의: 학교 협의 후 지정

This notice should be reviewed by the relevant school department before real operation.

---

## 7. Congestion Measurement Method Comparison

CityBrain should not claim that YOLO is the only or final solution. For real campus operation, multiple congestion measurement methods should be compared.

| Method | Strength | Limitation | CityBrain Position |
|---|---|---|---|
| Manual input | Simple and low privacy risk | Requires operator effort | Good for early MVP |
| POS or kiosk data | Reliable meal count and sales signal | Requires vendor or operator integration | Good for production stage |
| CCTV or RTSP plus YOLO | Real-time congestion estimation without POS integration | Requires privacy and camera policy review | Good as pilot support module |
| Wi-Fi or Bluetooth estimation | Can estimate occupancy patterns | May create device tracking concerns | Not recommended for early pilot |
| App check-in or QR | Student-driven data collection | Low participation risk | Optional support feature |

Recommended CityBrain approach:

    Start with manual input plus YOLO-based auxiliary measurement.
    After operational validation, consider POS or kiosk data integration.

---

## 8. Pilot Operation Conditions

A limited campus pilot should be designed with the following conditions.

| Area | Recommended Condition |
|---|---|
| Pilot period | 1 week |
| Operation time | Lunch peak period, for example 11:30 to 13:30 |
| Camera scope | Entrance or queue area only |
| Video storage | Disabled |
| Stored data | People count, congestion, timestamp |
| Admin access | Limited to operator or project owner |
| Student notice | Required before real-world testing |
| Evaluation | Compare YOLO count with manual observation |
| Output | CSV report and weekly summary |

---

## 9. Pilot Evaluation Metrics

The pilot should be evaluated with measurable indicators.

| Metric | Description |
|---|---|
| Data collection success rate | Percentage of successful congestion records |
| Average detected people by time slot | Average person count per time interval |
| YOLO vs manual count gap | Difference between AI count and human observation |
| Congestion peak time | Time range with the highest congestion |
| Student page usefulness | Whether students find the congestion page useful |
| Operator report usefulness | Whether admin report supports operational decisions |
| CSV usability | Whether exported data can support weekly review |
| Failure cases | Camera error, low light, occlusion, server downtime |

---

## 10. Key Risks and Mitigation

| Risk | Why It Matters | Mitigation |
|---|---|---|
| Purpose mismatch | Existing CCTV may have a different purpose | Define pilot purpose separately |
| Lack of notice | Students may not know about camera-based analysis | Use signage and web notice |
| Original video storage | Increases privacy risk | Disable storage |
| Face recognition concern | AI camera can be misunderstood as surveillance | Clearly state no face recognition |
| Wide camera angle | May capture unnecessary private activity | Limit angle to queue or entrance |
| Admin access exposure | CSV and reports may contain operational data | Add login and role-based access later |
| Accuracy limitation | YOLO count may be wrong in crowded scenes | Compare with manual observations |

---

## 11. Recommended Next Development

The next technical feature should be automatic congestion logging.

Current state:

    Admin can manually save current congestion data.

Recommended next state:

    CityBrain automatically saves congestion statistics every 1 or 5 minutes
    during selected operating hours, without storing original video.

This improves the pilot value because the system can produce a time-series dataset for CSV export and weekly operation review.

Recommended branch:

    v9.4-auto-vision-logging

Recommended feature scope:

- Auto-save ON/OFF
- Save interval setting
- Operating time window
- Duplicate save prevention
- Admin status display
- CSV compatibility

---

## 12. Conclusion

CityBrain's camera-based congestion estimation is technically meaningful as a campus cafeteria pilot feature, but its real value depends on privacy-aware operation.

The project should be presented as:

    A privacy-minimized congestion statistics MVP
    for cafeteria operation support,
    not a surveillance or personal identification system.

For school-facing communication, the strongest position is:

    CityBrain stores no original video, performs no face recognition,
    and uses camera input only to generate people-count-based congestion statistics.

Before real deployment, CCTV purpose, signage, camera angle, access control, and school privacy review must be completed.

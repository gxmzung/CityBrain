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

---

## 13. School CCTV and Video Information Processing Review

### 13.1 Purpose

This section reviews whether CityBrain's camera-based congestion measurement can be applied in an actual university cafeteria environment.

The purpose is to examine a real-time congestion measurement system within the scope of privacy protection guidelines and to support a safer campus environment.

CityBrain should be positioned as a congestion statistics system, not a personal identification or surveillance system.

---

### 13.2 University CCTV Operation Criteria

CCTV or fixed video information processing devices in a university are generally operated for the following purposes.

- Crime prevention
- Facility management
- Fire prevention
- Safety accident prevention
- Access control
- Facility protection

If CityBrain uses an existing CCTV feed or installs a separate camera, the system purpose must be reviewed carefully.

The key issue is whether cafeteria congestion measurement is compatible with the original installation purpose of the camera.

If the existing CCTV was installed only for crime prevention or facility safety, using it for congestion analysis may require additional review by the school's privacy officer or facility management department.

---

### 13.3 Required Notice Items

Before a real campus pilot, students should be able to recognize that camera-based congestion measurement is being tested.

A notice should include the following items.

- Installation purpose
- Camera location
- Shooting range
- Operating time
- Responsible department or manager
- Contact information
- Stored data
- Data not stored
- Inquiry method

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

---

### 13.4 Statistical Processing Without Original Video Storage

CityBrain should avoid storing original video, image captures, face images, or personal identifiers.

The recommended storage policy is as follows.

| Item | Stored? | Description |
|---|---:|---|
| Person count | Yes | Number of detected people |
| Congestion level | Yes | Low, normal, crowded, etc. |
| Measurement time | Yes | Time when the data was generated |
| Source | Yes | Webcam, RTSP, or module source |
| Original video | No | Video files should not be stored |
| Image capture | No | Frame images should not be saved |
| Face image | No | Face images or features should not be stored |
| Student identity | No | Student ID, name, or individual identifier should not be stored |
| Movement path | No | Individual tracking should not be performed |

CityBrain's stored data should be limited to statistical values such as person count, congestion level, and timestamp.

The stored statistics should not be restorable into original video or individual behavior records.

---

### 13.5 Privacy Protection Measures

CityBrain should apply privacy protection measures from the early pilot stage.

Recommended measures:

- Do not display original video on the operation dashboard.
- Show only person count and congestion level whenever possible.
- Disable video recording and image capture.
- Do not use face recognition.
- Do not track individuals across time or locations.
- Limit the camera angle to the cafeteria entrance or queue area.
- Use real-time mosaic or low-resolution preview only if a visual debugging screen is unavoidable.
- Keep debugging screens local and disable them during real campus operation.

---

### 13.6 Prevention of Purpose Misuse

CityBrain should be used only for cafeteria congestion guidance and operation improvement.

The system should not be used for:

- Student surveillance
- Individual identification
- Attendance checking
- Behavior tracking
- Face recognition
- Personal movement analysis
- Non-cafeteria monitoring purposes

The system purpose should be clearly limited to congestion statistics and cafeteria operation support.

---

### 13.7 Access Control and Security

Admin pages, CSV export, and history logs should be accessible only to authorized users.

Before real deployment, the following items should be reviewed.

- Admin login
- Role-based access control
- CSV download permission
- Server access restriction
- HTTPS for external access
- Environment variable based configuration
- Access log management
- Confirmation that original video storage is disabled

Detailed retention periods and security requirements for access logs should follow the school's privacy policy and applicable legal guidelines.

---

### 13.8 External Vendor or Operator Integration

If camera analysis, server operation, POS data, or kiosk data is handled by an external company, the school should review whether it falls under personal information processing consignment.

External integration should review the following items.

- Consignment contract
- Personal information protection clauses
- Processing purpose and scope
- Access permission limits
- Re-consignment restrictions
- Security incident responsibility
- Data retention and deletion policy

For an early CityBrain pilot, the safest approach is to avoid external vendor integration and run the system locally with test camera input.

---

## 14. Cafeteria Congestion Measurement Method Comparison

CityBrain should not depend on only one measurement method. The system should be designed to expand step by step depending on school and cafeteria operator conditions.

| Method | Description | Strength | Limitation | CityBrain Position |
|---|---|---|---|---|
| Kiosk or POS payment logs | Estimate cafeteria usage based on order count and payment time | High reliability and directly related to operation data | Requires vendor or cafeteria operator cooperation | Suitable for production stage |
| Manual input | Staff manually enters congestion level | Simple and low privacy risk | Requires operator effort | Suitable for early MVP |
| CCTV or RTSP plus YOLO | Estimate people count from camera input using AI | Real-time measurement without POS integration | Requires privacy and camera policy review | Suitable as pilot support module |
| Wi-Fi or Bluetooth estimation | Estimate occupancy using nearby device signals | Can estimate space-level occupancy | May raise device tracking concerns | Not recommended for early pilot |
| Student app check-in | Students submit visit or check-in data through the app | Can collect student-participation data | Low participation can reduce reliability | Optional support feature only |

---

## 15. Practical Conclusion

The most realistic CityBrain deployment path is:

1. Manual input based MVP
2. YOLO-based auxiliary congestion measurement
3. Automatic logging and CSV report
4. Limited school pilot
5. POS or kiosk data integration after operator agreement
6. Meal demand prediction and operation optimization

For the initial school-facing proposal, CityBrain should be described as:

    A privacy-minimized cafeteria congestion statistics system
    that stores person count, congestion level, and timestamp only.

The strongest initial strategy is to combine manual input and YOLO-based auxiliary measurement.

After the school and cafeteria operator agree on data access, CityBrain can expand to POS or kiosk payment log integration for more reliable operation analysis.

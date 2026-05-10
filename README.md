# CityBrain

### Data-driven smart campus cafeteria platform

CityBrain is a smart campus MVP that starts from the university cafeteria.

The project focuses on a concrete campus problem: students do not know menu availability, congestion, waiting time, or sell-out status before visiting the cafeteria. At the same time, the university side often lacks structured operational data for demand, congestion, and menu consumption.

CityBrain turns the cafeteria into a small but realistic data collection and service improvement space.

---

## Why Start from the Cafeteria?

The cafeteria is a strong first target for smart campus validation because:

- students feel the inconvenience directly
- demand repeats every day
- congestion and sell-out patterns are observable
- operational data can be collected quickly
- the impact is easy to demonstrate
- the MVP can stay small and safe

The goal is not to build a full smart campus at once.  
The goal is to prove that one high-frequency campus space can be improved through data.

---

## Core Idea

```text
Student inconvenience
→ operational data
→ admin dashboard
→ better campus service decisions
```

CityBrain treats cafeteria inconvenience not only as a complaint issue, but as an operations and data problem.

---

## Main Features

### Student View

Students can check cafeteria status before visiting.

- today’s menu
- remaining quantity
- congestion level
- estimated waiting time
- estimated sell-out time
- student feedback / preference response

### Admin View

Operators can update and monitor cafeteria status.

- set total quantity
- update remaining quantity
- reflect sales
- change congestion status
- view operational data
- manage dashboard information

### Operation Data

CityBrain collects data that can support future decision-making.

- menu demand
- congestion pattern
- sell-out flow
- student response
- operational history

---

## Demo Flow

1. Student checks the current cafeteria state
   - remaining quantity
   - congestion level
   - estimated waiting time

2. Admin updates operation status
   - sales count
   - congestion level
   - menu status

3. Student view reflects the updated state

This demonstrates both student-facing value and admin-side operational value in a small MVP.

---

## Current Main Version

```text
Current main implementation: CityBrain_V8.2
Previous iteration:          CityBrain_V8.1
```

`CityBrain_V8.2` includes:

- FastAPI backend
- admin web dashboard
- student web interface
- Android app scaffold
- Korean UI/menu iteration
- Jarvis assistant route prototype
- privacy/security/release checklist documents
- demo connection and backup scripts
- UI/UX screenshots and concept assets

---

## Repository Structure

```text
CityBrain/
├─ README.md
├─ CityBrain_V8.1/
│  ├─ backend/
│  ├─ android/
│  ├─ docs/
│  └─ scripts/
│
├─ CityBrain_V8.2/
│  ├─ backend/
│  │  ├─ app/
│  │  ├─ data/
│  │  ├─ tests/
│  │  └─ requirements.txt
│  │
│  ├─ android/
│  │  ├─ app/
│  │  ├─ build.gradle.kts
│  │  └─ settings.gradle.kts
│  │
│  ├─ docs/
│  │  ├─ 00_CONNECTION_MAP.md
│  │  ├─ 01_TECH_STACK_AND_ROADMAP.md
│  │  ├─ 02_DESIGN_SYSTEM.md
│  │  ├─ 03_PRIVACY_POLICY_DRAFT.md
│  │  ├─ 04_SECURITY_BACKUP_INCIDENT.md
│  │  ├─ 05_STORE_RELEASE_CHECKLIST.md
│  │  ├─ 10_V8_UI_UX_IMPLEMENTATION.md
│  │  ├─ 11_V8_2_PIXEL_CLONE_NOTES.md
│  │  └─ 12_CITYBRAIN_JARVIS_STUDY_GUIDE.md
│  │
│  ├─ assets/
│  └─ scripts/
│
└─ 이미지/
   └─ IMAGES/
```

---

## Tech Stack

### Backend

- Python
- FastAPI
- SQLite
- Uvicorn
- pytest

### Web

- HTML
- CSS
- JavaScript
- Jinja templates

### Android

- Kotlin
- Jetpack Compose
- Gradle Kotlin DSL

### Documentation / Operations

- privacy policy draft
- security / backup / incident notes
- store release checklist
- demo account policy
- connection map
- production gap document

---

## Quick Start

### Backend demo

```bash
cd CityBrain_V8.2/backend
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload
```

Open:

```text
Student web: http://127.0.0.1:8000/
Admin login: http://127.0.0.1:8000/admin/login
API docs:    http://127.0.0.1:8000/docs
```

### Android app

Open the Android project:

```text
CityBrain_V8.2/android
```

Configure the backend URL in the Android build configuration before running on a device.

Example:

```text
CITYBRAIN_BASE_URL=http://YOUR_LOCAL_IP:8000/
```

---

## Implementation Evidence

This repository includes implementation and operation evidence, not only planning documents.

Examples:

- backend route structure
- admin/student templates
- Android screens
- UI/UX implementation notes
- connection map
- release checklist
- security and backup notes
- demo scripts
- screenshots and concept images

---

## Production Gap

CityBrain is an MVP and demo-stage project.

Known gaps before real deployment:

- official student authentication is not implemented
- privacy policy requires institutional review
- production database and backup policy need hardening
- accessibility and responsive QA are incomplete
- app store release process is not finished
- real cafeteria POS/kiosk integration is not connected
- admin authorization should be strengthened

These gaps are documented intentionally because the project is positioned as a realistic campus MVP, not as a finished production service.

---

## Future Work

- connect kiosk or sales log data
- improve student identity verification
- add menu demand prediction
- improve Android UI consistency
- add operational analytics dashboard
- add admin audit logs
- prepare AAB signing and store policy documents
- run a limited pilot with cafeteria operators

---

## Status

CityBrain is a smart campus cafeteria MVP.

The current main version is `CityBrain_V8.2`, with backend, Android, documentation, and UI/UX evidence included.
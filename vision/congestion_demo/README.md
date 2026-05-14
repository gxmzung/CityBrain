# CityBrain Vision Congestion Demo

CityBrain의 키오스크/OBU 연동 대안으로 구성한 YOLO 기반 혼잡도 추정 데모입니다.

## Purpose

키오스크 업체 연동이 어렵거나 결제 로그 접근이 제한될 경우를 대비해,  
카메라 영상에서 사람 수와 줄 길이를 추정하여 혼잡도 통계값을 산출하는 대안 모듈입니다.

## Flow

```text
Webcam / RTSP Camera
→ YOLO Person Detection
→ People Count / Queue Length
→ Congestion Level
→ CityBrain Student Screen 연동 가능
python3 -m venv .venv
source .venv/bin/activate

pip install -r requirements.txt

python app.py
http://127.0.0.1:8081
http://127.0.0.1:8081/api/congestion/latest
Notes
현재는 맥 카메라 또는 웹캠 기반 데모입니다.
RTSP CCTV/IP 카메라 스트림으로 확장할 수 있습니다.
영상 원본 저장이나 개인 식별이 아니라 사람 수 기반 혼잡도 통계값 산출을 목표로 합니다.
정식 도입 전에는 학교 CCTV 운영 정책, 안내 문구, 접근 권한, 영상 저장 여부 검토가 필요합니다.

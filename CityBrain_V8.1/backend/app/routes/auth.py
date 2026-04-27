from pydantic import BaseModel, Field
from fastapi import APIRouter, Header, HTTPException
from app.security import create_token, verify_token
from app.db import verify_student_user, get_student_profile, add_log

router = APIRouter(prefix="/api/student/auth", tags=["student-auth"])


class StudentLoginIn(BaseModel):
    student_no: str = Field(..., min_length=4, max_length=20)
    password: str = Field(..., min_length=4, max_length=80)


@router.post("/login")
def student_login(payload: StudentLoginIn):
    student = verify_student_user(payload.student_no, payload.password)
    if not student:
        raise HTTPException(status_code=401, detail="학번 또는 비밀번호가 올바르지 않습니다.")
    token = create_token({"type": "student", "student_no": student["student_no"], "name": student["name"]})
    add_log(student["student_no"], "STUDENT_LOGIN", "학생 앱 로그인")
    return {
        "access_token": token,
        "token_type": "bearer",
        "student": student,
    }


@router.get("/me")
def student_me(authorization: str | None = Header(default=None)):
    if not authorization or not authorization.lower().startswith("bearer "):
        raise HTTPException(status_code=401, detail="Bearer token이 필요합니다.")
    token = authorization.split(" ", 1)[1]
    payload = verify_token(token)
    if not payload or payload.get("type") != "student":
        raise HTTPException(status_code=401, detail="유효하지 않은 토큰입니다.")
    student = get_student_profile(payload["student_no"])
    if not student:
        raise HTTPException(status_code=404, detail="학생 정보를 찾을 수 없습니다.")
    return student

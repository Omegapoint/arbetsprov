from pydantic import BaseModel
from typing import Optional


class CalculationRequest(BaseModel):
    numberOne: float
    numberTwo: float


class CalculationResult(BaseModel):
    numberOne: float
    numberTwo: float
    result: float


class CalculationResponse(BaseModel):
    asyncId: str
    result: Optional[CalculationResult] = None


class CombinedResponse(BaseModel):
    asyncId: str
    numberOne: float
    numberTwo: float
    result: float
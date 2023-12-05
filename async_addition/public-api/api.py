import httpx
import uvicorn
import threading
from consumer import start_kafka_consumer, message_cache, thread_lock, wait_for_result
from components import *
from typing import List, Optional
from fastapi import FastAPI, HTTPException, Query


app = FastAPI(title="Public API for Async Addition")


# FastAPI endpoint to a calucation request with two numbers and forward them to the internal API. 
# The add function returns the asyncId and optionally, the calculation result.
@app.post("/add", response_model=CalculationResponse, response_model_exclude_none=True)
async def add_numbers(calc_request: CalculationRequest, syncResult: Optional[bool] = Query(False)):
    try:
        async with httpx.AsyncClient() as client:
            response = await client.post("http://localhost:3000/add", json=calc_request.dict())
            async_id = response.json()['asyncId']

            if syncResult:
                # Starts looking for results in the message cache
                result = await wait_for_result(async_id)

                if result is not None:
                    return CalculationResponse(asyncId=async_id, result=result)
                else:
                    raise HTTPException(
                        status_code=408, detail="Result not available within timeout period")

            return CalculationResponse(asyncId=async_id)
    except httpx.ConnectError:
        raise HTTPException(
            status_code=503, detail="Unable to connect to addition-service")
    except httpx.HTTPStatusError as e:
        # Handle specific HTTP errors (e.g., 4xx, 5xx responses)
        raise HTTPException(status_code=e.response.status_code, detail=str(e))
    except Exception as e:
        # Catch other exceptions and handle them
        raise HTTPException(
            status_code=500, detail="An unexpected error occurred")


# FastAPI endpoint to get all calculation results from the internal Kafka topic.
@app.get("/list-results", response_model=List[CombinedResponse])
async def list_results():
    with thread_lock:
        return [CombinedResponse(asyncId=async_id, **result.dict())
                for async_id, result in message_cache.items()]


# Starts the Kafka consumer in its own thread
def run_kafka_consumer_thread():
    kafka_thread = threading.Thread(target=start_kafka_consumer)
    kafka_thread.start()


# Starts everything up
if __name__ == "__main__":
    run_kafka_consumer_thread()
    uvicorn.run(app, host="0.0.0.0", port=8443, log_level="debug")

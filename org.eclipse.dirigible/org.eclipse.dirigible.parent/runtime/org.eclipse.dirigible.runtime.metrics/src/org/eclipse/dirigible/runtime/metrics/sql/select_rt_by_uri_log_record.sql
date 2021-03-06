SELECT ACCLOG_REQUEST_URI, ACCLOG_PERIOD, AVG(ACCLOG_RESPONSE_TIME) AS RESPONSE_TIME 
FROM DGB_ACCESS_LOG 
WHERE ACCLOG_PERIOD > ? 
GROUP BY ACCLOG_REQUEST_URI, ACCLOG_PERIOD 
ORDER BY ACCLOG_REQUEST_URI, ACCLOG_PERIOD
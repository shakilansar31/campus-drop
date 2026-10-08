-- =============================================================================
-- CAMPUS DROP: USEFUL ORACLE SQL TEST QUERIES
-- Executable in Oracle SQL Developer / SQL*Plus / Command Line
-- =============================================================================

-- 1. View all reported items in ITEMS table
SELECT * FROM ITEMS ORDER BY ITEM_ID ASC;

-- 2. View only LOST items
SELECT ITEM_ID, ITEM_NAME, CATEGORY, LOCATION, REPORTED_BY, STATUS 
FROM ITEMS 
WHERE ITEM_TYPE = 'LOST';

-- 3. View only FOUND items
SELECT ITEM_ID, ITEM_NAME, CATEGORY, LOCATION, REPORTED_BY, STATUS 
FROM ITEMS 
WHERE ITEM_TYPE = 'FOUND';

-- 4. View active items available for matching or claim
SELECT * FROM ITEMS WHERE STATUS = 'ACTIVE';

-- 5. Search item by exact ID
SELECT * FROM ITEMS WHERE ITEM_ID = 101;

-- 6. Search items by keyword in name (Case Insensitive)
SELECT * FROM ITEMS WHERE LOWER(ITEM_NAME) LIKE '%wallet%';

-- 7. Search items by campus location
SELECT * FROM ITEMS WHERE LOWER(LOCATION) LIKE '%library%';

-- 8. View all pending claims in CLAIMS table
SELECT C.CLAIM_ID, C.ITEM_ID, I.ITEM_NAME, C.STUDENT_NAME, C.STUDENT_ID, C.CLAIM_REASON, C.CLAIM_STATUS
FROM CLAIMS C
JOIN ITEMS I ON C.ITEM_ID = I.ITEM_ID
WHERE C.CLAIM_STATUS = 'PENDING';

-- 9. View recovery history audit trail
SELECT H.HISTORY_ID, H.ITEM_ID, H.ITEM_NAME, H.STUDENT_NAME, H.RETURN_DATE, H.STATUS
FROM RECOVERY_HISTORY H
ORDER BY H.HISTORY_ID DESC;

-- 10. Summary count statistics
SELECT 
    COUNT(*) AS TOTAL_ITEMS,
    SUM(CASE WHEN ITEM_TYPE = 'LOST' THEN 1 ELSE 0 END) AS LOST_COUNT,
    SUM(CASE WHEN ITEM_TYPE = 'FOUND' THEN 1 ELSE 0 END) AS FOUND_COUNT,
    SUM(CASE WHEN STATUS = 'ACTIVE' THEN 1 ELSE 0 END) AS ACTIVE_COUNT,
    SUM(CASE WHEN STATUS = 'CLAIMED' THEN 1 ELSE 0 END) AS CLAIMED_COUNT,
    SUM(CASE WHEN STATUS = 'RETURNED' THEN 1 ELSE 0 END) AS RETURNED_COUNT
FROM ITEMS;

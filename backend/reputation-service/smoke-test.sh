#!/usr/bin/env bash
# End-to-end check of the CRUD endpoints against a running service with an empty database.
# Usage: ./smoke-test.sh [base-url]   (default http://localhost:8084/api/v1)
set -u
API=${1:-http://localhost:8084/api/v1}
FAILS=0
RUN=$RANDOM   # unique ids so the script can be re-run on the same database

# call METHOD PATH EXPECTED_STATUS [JSON_BODY] -> prints body into $BODY
call() {
  local out
  out=$(curl -s -w '\n%{http_code}' -X "$1" "$API$2" -H 'Content-Type: application/json' ${4:+-d "$4"})
  BODY=$(sed '$d' <<<"$out"); local status=$(tail -n1 <<<"$out")
  if [[ $status == "$3" ]]; then echo "ok   $1 $2 -> $status"; else echo "FAIL $1 $2 -> $status (expected $3) $BODY"; FAILS=$((FAILS+1)); fi
}
field() { grep -o "\"$1\":\"[^\"]*\"" <<<"$BODY" | head -1 | cut -d'"' -f4; }

T=trip-$RUN; D=driver-$RUN; P1=pass1-$RUN; P2=pass2-$RUN
NOW=$(date -u +%Y-%m-%dT%H:%M:%SZ)

# Completed trips
call POST /completed-trips 201 "{\"tripId\":\"$T\",\"driverId\":\"$D\",\"passengerIds\":[\"$P1\",\"$P2\"],\"completedAt\":\"$NOW\"}"
call POST /completed-trips 409 "{\"tripId\":\"$T\",\"driverId\":\"$D\",\"passengerIds\":[\"$P1\"],\"completedAt\":\"$NOW\"}"
call POST /completed-trips 400 "{\"tripId\":\"x\",\"driverId\":\"$D\",\"passengerIds\":[],\"completedAt\":\"$NOW\"}"
call GET  /completed-trips/$T 200
call POST /completed-trips 201 "{\"tripId\":\"old-$T\",\"driverId\":\"$D\",\"passengerIds\":[\"$P1\"],\"completedAt\":\"2020-01-01T00:00:00Z\"}"

# Reviews and score
call POST /reviews 201 "{\"tripId\":\"$T\",\"authorId\":\"$P1\",\"targetId\":\"$D\",\"rating\":4,\"comment\":\"Good\"}"
R1=$(field id)
call POST /reviews 201 "{\"tripId\":\"$T\",\"authorId\":\"$P2\",\"targetId\":\"$D\",\"rating\":2}"
R2=$(field id)
call POST /reviews 409 "{\"tripId\":\"$T\",\"authorId\":\"$P1\",\"targetId\":\"$D\",\"rating\":5}"
call POST /reviews 403 "{\"tripId\":\"$T\",\"authorId\":\"$P1\",\"targetId\":\"$P2\",\"rating\":5}"   # passenger -> passenger
call POST /reviews 403 "{\"tripId\":\"$T\",\"authorId\":\"stranger\",\"targetId\":\"$D\",\"rating\":5}"
call POST /reviews 403 "{\"tripId\":\"unknown\",\"authorId\":\"$P1\",\"targetId\":\"$D\",\"rating\":5}"
call POST /reviews 403 "{\"tripId\":\"old-$T\",\"authorId\":\"$P1\",\"targetId\":\"$D\",\"rating\":5}" # period over
call POST /reviews 400 "{\"tripId\":\"$T\",\"authorId\":\"$D\",\"targetId\":\"$P1\",\"rating\":9}"
call GET  /users/$D/score 200; [[ $BODY == *'"averageRating":3.0'* ]] || { echo "FAIL average should be 3.0: $BODY"; FAILS=$((FAILS+1)); }
call PUT  /reviews/$R2 200 '{"rating":4,"comment":"Better on second thought"}'
call GET  /users/$D/score 200; [[ $BODY == *'"averageRating":4.0'* ]] || { echo "FAIL average should be 4.0: $BODY"; FAILS=$((FAILS+1)); }
call GET  "/users/$D/reviews?size=1" 200
[[ $BODY == *'"totalElements":2'* && $BODY == *'"size":1'* ]] || { echo "FAIL pagination: $BODY"; FAILS=$((FAILS+1)); }
call DELETE /reviews/$R1 204
call GET  /reviews/$R1 404
call GET  /users/$D/score 200; [[ $BODY == *'"reviewCount":1'* ]] || { echo "FAIL reviewCount should be 1: $BODY"; FAILS=$((FAILS+1)); }
call GET  /users/nobody-$RUN/score 404

# Reports and sanctions
call POST /reports 201 "{\"reporterId\":\"$P1\",\"reportedUserId\":\"$D\",\"tripId\":\"$T\",\"reason\":\"DANGEROUS_DRIVING\",\"description\":\"Speeding\"}"
REP=$(field id)
call POST /reports 400 "{\"reporterId\":\"$P1\",\"reportedUserId\":\"$P1\",\"reason\":\"OTHER\"}"
call POST /reports 400 "{\"reporterId\":\"$P1\",\"reportedUserId\":\"$D\",\"reason\":\"NOT_A_REASON\"}"
call GET  "/reports?status=PENDING" 200
call PATCH /reports/$REP 200 '{"status":"UNDER_REVIEW","moderatorId":"admin"}'
call PATCH /reports/$REP 400 '{"status":"ACCEPTED","moderatorId":"admin","sanction":{"type":"SUSPENSION","reason":"x"}}'  # no end date
call GET  /reports/$REP 200; [[ $(field status) == UNDER_REVIEW ]] || { echo "FAIL invalid sanction must not change the report"; FAILS=$((FAILS+1)); }
call PATCH /reports/$REP 200 '{"status":"ACCEPTED","moderatorId":"admin","sanction":{"type":"SUSPENSION","reason":"Dangerous driving","endDate":"2099-01-01T00:00:00Z"}}'
call PATCH /reports/$REP 409 '{"status":"REJECTED","moderatorId":"admin"}'
call DELETE /reports/$REP 409
call GET  /users/$D/sanctions 200
call GET  /users/$D/score 200; [[ $BODY == *'"validatedReports":1'* ]] || { echo "FAIL validatedReports should be 1: $BODY"; FAILS=$((FAILS+1)); }
call PATCH /reports/missing 404 '{"status":"REJECTED","moderatorId":"admin"}'
call DELETE /completed-trips/old-$T 204

echo; [[ $FAILS == 0 ]] && echo "All checks passed" || { echo "$FAILS check(s) failed"; exit 1; }

SELECT *
FROM issues i
         INNER JOIN versions v ON i.fixed_version_id = v.id
         INNER JOIN issue_statuses ist ON i.status_id = ist.id
WHERE fixed_version_id = ${version_id}
  AND ist.is_closed = 0
;



SELECT issues.id, UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(updated_on), updated_on
FROM issues
         INNER JOIN issue_statuses ON status_id = issue_statuses.id
WHERE fixed_version_id = ${version_id}
  AND issue_statuses.is_closed != 1
ORDER BY updated_on DESC;

# AVG time of last change for all issues except slowest 3
# SELECT SUM(UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(updated_on))
SELECT AVG(last_change)
FROM (
         SELECT issues.id, UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(updated_on) last_change, updated_on
         FROM issues
                  INNER JOIN issue_statuses ON status_id = issue_statuses.id
         WHERE fixed_version_id = ${version_id}
           AND issue_statuses.is_closed != 1
         ORDER BY updated_on ASC
         LIMIT 3, 10000000) sub



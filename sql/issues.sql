SELECT *
FROM issues i
         INNER JOIN versions v ON i.fixed_version_id = v.id
         INNER JOIN issue_statuses ist ON i.status_id = ist.id
WHERE fixed_version_id = ${version_id}
  AND ist.is_closed = 0
;



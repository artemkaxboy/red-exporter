SELECT activity_id, a.name, sum(hours) FROM time_entries te
INNER JOIN enumerations a ON te.activity_id = a.id AND a.type = 'TimeEntryActivity' AND a.project_id IS NULL
WHERE user_id = ${userId} AND tyear = ${year} AND tmonth = ${month}
GROUP BY activity_id;

SELECT DISTINCT(activity_id) FROM time_entries;

# Redmine Exporter for Prometheus

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/8d6d0f583417417c86d2ffa226afbf05)](https://www.codacy.com/gh/artemkaxboy/red-exporter/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=artemkaxboy/red-exporter&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/8d6d0f583417417c86d2ffa226afbf05)](https://www.codacy.com/gh/artemkaxboy/red-exporter/dashboard?utm_source=github.com&utm_medium=referral&utm_content=artemkaxboy/red-exporter&utm_campaign=Badge_Coverage)

Exposes Redmine issues and time entries as Prometheus metrics.

## How it works

The app runs as docker container and exposes the metrics on the given port. Due to weak ability of Redmine API it requires to connect to redmine DB (read-only access is enough).

## Limitations

It **works with MySQL** DB backend only for now.

## Metrics

### Issue metrics

Issue metrics provide the information about issues in the project in 4 different dimensions, which helps to understand better how the work is going. Dimensions are:

* `Status`, e.g. closed, open, reopened
* `Category`, e.g. Backend, Frontend, Other
* `Tracker`, e.g. Bug, Feature, Question
* `Priority`, e.g. P1, P2, P3

Each dimension has opened and closed counters. Each meter has information about the project, version and version's planned date.

Example of issue metric:

```
# HELP redmine_project_issues
# TYPE redmine_project_issues gauge
redmine_project_issues{application="red-exporter",closed="1",project="Good Application",status="Closed",version="1.0.2",version_date="2021-12-31",} 25.0

# HELP redmine_project_issues_category
# TYPE redmine_project_issues_category gauge
redmine_project_issues_category{application="red-exporter",category="Backend",closed="1",project="Good Application",version="1.0.2",version_date="2021-12-31",} 5.0
redmine_project_issues_category{application="red-exporter",category="Frontend",closed="1",project="Good Application",version="1.0.2",version_date="2021-12-31",} 10.0
redmine_project_issues_category{application="red-exporter",category="",closed="1",project="Good Application",version="1.0.2",version_date="2021-12-31",} 10.0

# HELP redmine_project_issues_tracker
# TYPE redmine_project_issues_tracker gauge
redmine_project_issues_tracker{application="red-exporter",closed="1",project="Good Application",tracker="Bug",version="1.0.2",version_date="2021-12-31",} 5.0
redmine_project_issues_tracker{application="red-exporter",closed="1",project="Good Application",tracker="Feature",version="1.0.2",version_date="2021-12-31",} 20.0

# HELP redmine_project_issues_priority  
# TYPE redmine_project_issues_priority gauge
redmine_project_issues_priority{application="red-exporter",closed="1",priority="P1",project="Good Application",version="1.0.2",version_date="2021-12-31",} 2.0
redmine_project_issues_priority{application="red-exporter",closed="1",priority="P2",project="Good Application",version="1.0.2",version_date="2021-12-31",} 15.0
redmine_project_issues_priority{application="red-exporter",closed="1",priority="P3",project="Good Application",version="1.0.2",version_date="2021-12-31",} 8.0
```

### Time entry metrics

Time entry metrics provide the information about time spent by user on different activities. Counters **reset every month**.

Example of time entry metric:

```
# HELP redmine_user_activities
# TYPE redmine_user_activities gauge
redmine_user_activities{activity="Code",application="red-exporter",login="artem.kolin",name="Artem Kolin",} 1000.0
redmine_user_activities{activity="Code Review",application="red-exporter",login="artem.kolin",name="Artem Kolin",} 500.0
redmine_user_activities{activity="Test",application="red-exporter",login="artem.kolin",name="Artem Kolin",} 100.0
```

## Installation

The application is distributed as docker image and do not require any installation. 

It can be run directly from command line:

```
docker run -d -p 8080:8080 --restart always --name redmine-exporter \
  --env DATABASE_HOST=<redmine.host> --env DATABASE_NAME=<redmine> \
  --env DATABASE_USERNAME=<redmine-read-user> --env DATABASE_PASSWORD=<password> \
  --env REDMINE_PROJECTS=<project1-number-id>,<project2-number-id> \
  --env REDMINE_USERS=<user1-id>,<user2-id> \
  artemkaxboy/red-exporter
```

Or as a docker compose project:

```yaml
services:
  redmine-exporter:
    image: artemkaxboy/red-exporter
    environment:
      DATABASE_HOST: ${DATABASE_HOST:?}
      DATABASE_NAME: ${DATABASE_NAME:?}
      DATABASE_USERNAME: ${DATABASE_USERNAME:?}
      DATABASE_PASSWORD: ${DATABASE_PASSWORD:?}
      REDMINE_PROJECTS: ${REDMINE_PROJECTS:-}
      REDMINE_USERS: ${REDMINE_USERS:-}
    deploy:
      restart_policy:
        delay: 600s
    ports:
      - "8080:8080"
```

Compose project example can be found in [examples/compose](https://github.com/artemkaxboy/red-exporter/tree/main/examples/compose).

## Configuration

The image takes parameters from environment variables:

* `DATABASE_HOST` - Redmine DB host
* `DATABASE_PORT` - Redmine DB port
* `DATABASE_NAME` - Redmine DB name
* `DATABASE_USERNAME` - Redmine DB username
* `DATABASE_PASSWORD` - Redmine DB password
* `REDMINE_PROJECTS` - Comma-separated list of number IDs of Redmine projects to export issue metrics for. If not set, no issues will be exported.
* `REDMINE_USERS` - Comma-separated list of number IDs of Redmine users to export time entry metrics for. If not set, no time entries will be exported.

## Questions

For any questions, please use [Issues](https://github.com/artemkaxboy/red-exporter/issues)

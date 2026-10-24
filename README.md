# 🧠 Smart Productivity Tracker — Backend

A robust and scalable **Spring Boot backend** for the **Smart Productivity Tracker** application.  
This service provides secure REST APIs to manage tasks, track productivity, and synchronize task states for individual users.

---

## ✨ Features

- 🗂️ Create, read, update, and delete tasks
- 👤 User-specific task management
- 🔄 Task status workflow  
  **PENDING → IN_PROGRESS → COMPLETED**
- 📅 Due date and last sync tracking
- 📢 Event publishing for task lifecycle changes
- 📊 Weekly and monthly time-allocation analytics
- ✨ Personalized AI suggestions with a metric-based fallback
- 🎉 Automatic completion congratulations through Kafka events
- 🧱 Clean layered architecture (Controller → Service → Repository)

---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **Hibernate**
- **PostgreSQL**
- **Apache Kafka (KRaft)**
- **Eureka Service Discovery and Spring Cloud Gateway**
- **Ollama or any OpenAI-compatible chat-completions API**
- **RESTful APIs**
- **Lombok**

## Local development setup (Windows)

### Prerequisites

- JDK 17
- Maven
- PostgreSQL, with a database named `SPT`
- Apache Kafka, using Docker Compose, an approved local installation, or an approved broker reachable at `localhost:9092`
- Docker Desktop (optional; required only for the Docker Kafka option)
- Ollama is optional; use it only if you want locally hosted model suggestions

The local Spring configuration uses PostgreSQL at `localhost:5432`, database `SPT`, username `postgres`, and password `manager`. Update each service's datasource configuration or supply environment overrides if your local database differs. Do not use these sample credentials in production.

### Start Kafka with Docker

Docker Compose starts the Kafka broker defined in the repository's `docker-compose.yml`. From the repository root, run:

```powershell
docker compose up -d kafka
```

The broker is available at `localhost:9092` and is configured with three default partitions. Check its status with `docker compose ps kafka` and view its logs with `docker compose logs -f kafka`. Stop it with `docker compose down`; avoid `docker compose down -v` unless you intentionally want to delete the persisted Kafka data volume.

### Start Kafka locally without Docker

Extract the Kafka **binary distribution** (not the `-src` archive) to a local folder. The commands below use Kafka's Windows-compatible Java classes directly, which avoids Windows command-line-length issues with some Kafka batch scripts. Replace the JDK and Kafka paths with your own:

```powershell
$env:JAVA_HOME = "D:\Project_Resources\JDKs\jdk-17.0.2"
$env:KAFKA_HOME = "D:\Project_Resources\kafka_2.13-4.3.1"

# First-time setup only: create a cluster ID and format Kafka's storage.
$clusterId = & "$env:JAVA_HOME\bin\java.exe" -cp "$env:KAFKA_HOME\libs\*" kafka.tools.StorageTool random-uuid
& "$env:JAVA_HOME\bin\java.exe" -cp "$env:KAFKA_HOME\libs\*" kafka.tools.StorageTool format --standalone -t $clusterId -c "$env:KAFKA_HOME\config\server.properties"

# Start Kafka. Leave this PowerShell window open while developing.
& "$env:JAVA_HOME\bin\java.exe" -Xmx1G "-Dlog4j2.configurationFile=$env:KAFKA_HOME\config\log4j2.yaml" -cp "$env:KAFKA_HOME\libs\*" kafka.Kafka "$env:KAFKA_HOME\config\server.properties"
```

Run the cluster-ID and format commands only once for a given Kafka data directory. To restart Kafka later, run only the final startup command. Press `Ctrl+C` in the Kafka window to stop it. The application services connect to `localhost:9092` by default.

A native Kafka installation uses the `num.partitions` value in its `config/server.properties` (the supplied Kafka default is one), while the Docker Compose broker is configured with three. This setting applies when a topic is first created; changing it does not alter an existing topic.

### Optional: use local Ollama models

Start Ollama with the directory that contains its `manifests` and `blobs` folders as `OLLAMA_MODELS`. For example, if the models are under `D:\Ollama_models\manifests\registry.ollama.ai\library`, set the root to `D:\Ollama_models`:

```powershell
$env:OLLAMA_MODELS = "D:\Ollama_models"
ollama serve
```

If Ollama is already running as a Windows app/service, stop and restart it after changing `OLLAMA_MODELS`. In another terminal, check that the models are available with `ollama list`. A chat model such as `llama3.1:latest` or `mistral-nemo:latest` can generate suggestions; embedding-only models such as `nomic-embed-text` cannot handle this chat request.

The AI client uses Ollama's OpenAI-compatible endpoint. The current local AI configuration is:

| Setting | Local value |
| --- | --- |
| `AI_ENABLED` | `true` |
| `AI_ENDPOINT` | `http://localhost:11434/v1/chat/completions` |
| `AI_API_KEY` | `ollama` (placeholder; local Ollama does not normally authenticate) |
| `AI_MODEL` | `llama3.1:latest` |

Set these as environment variables in the environment that starts AIService (for example, in the IntelliJ run configuration) to override the YAML values. For a different local Ollama model, update `AI_MODEL` to a name shown by `ollama list`. For an OpenAI-compatible hosted provider, use its documented endpoint, model name, and API key. Keep real API keys in environment variables and out of source control. If the AI provider is disabled or unavailable, AIService falls back to metric-based suggestions.

### Start the backend services

Start PostgreSQL and Kafka first, then Eureka, followed by the application services. Run each command in a separate terminal from the repository root:

```powershell
cd .\Eureka-server; mvn spring-boot:run
cd .\AuthServer; mvn spring-boot:run
cd .\UserService; mvn spring-boot:run
cd .\TaskService; mvn spring-boot:run
cd .\AIService; mvn spring-boot:run
cd .\ApiGateway; mvn spring-boot:run
```

The gateway listens on port `8080`; Eureka uses `8761`; AuthServer, UserService, TaskService, and AIService use ports `8081`–`8084`, respectively. If a service cannot resolve the shared `common-events` module, build/install the Maven reactor from the repository root first with `mvn install -DskipTests`.

## AI productivity feature

`AIService` runs on port `8084` and is exposed through the API Gateway under `/ai/**`.
Services publish JSON events to Kafka topics `user.events` and `task.events`. Producers use
the user ID as the record key, so a user's events in the same topic are routed to the same
partition and retain partition ordering. Each consumer group receives its own copy: for
example, AIService and UserService independently consume `task.events`. TaskService publishes
enriched `TASK_CREATED` and `TASK_UPDATED` snapshots; AIService maintains its own analytics
read model, so chart queries do not add load to Task Service. Other events include user
registration/deletion and task deletion. Kafka partitions records; it does not create these
application events.

Task requests support these additional fields:

- `category` (for example `WORK`, `HEALTH`, or `LEARNING`)
- `estimatedMinutes` (planned allocation)
- `actualMinutes` (accepted on update; otherwise calculated from start/completion time)

Authenticated endpoints (send the normal JWT to the gateway):

| Method | Endpoint | Purpose |
| --- | --- | --- |
| GET | `/ai/analytics/weekly?year=2026&week=40` | Seven daily chart points and category allocation |
| GET | `/ai/analytics/monthly?year=2026&month=10` | Monthly daily chart points and totals |
| GET | `/ai/suggestions?limit=5` | Recent suggestions and congratulations |
| POST | `/ai/suggestions/generate` | Generate an insight from current-week metrics |
| PATCH | `/ai/suggestions/{id}/read` | Mark a suggestion as read |

For accounts that already contain tasks, call `POST /tasks/analytics/sync` once after
deploying AIService. It republishes the authenticated user's existing tasks to the analytics
read model. New and edited tasks synchronize automatically.

The API works without a paid AI provider by producing deterministic suggestions from the
user's metrics. The local configuration is set up for Ollama, but it can be overridden with
`AI_ENABLED`, `AI_ENDPOINT`, `AI_API_KEY`, and `AI_MODEL` before starting AIService. Only
aggregate metrics are sent to the provider; task titles and descriptions are not sent. With
Ollama running locally, these requests stay on the local machine.

For the React dashboard, use Recharts (or Chart.js) with `timeline` for a composed
allocated-vs-spent bar chart and `categoryAllocation` for a doughnut chart. Add KPI cards
for completion rate, completed tasks, overdue tasks, and focus time, plus a suggestion card
that calls the generate and mark-read endpoints.

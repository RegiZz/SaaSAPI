# SaaS API

Prosty backend do zarządzania planami subskrypcji, użytkownikami, trialami i limitami (users/projects).
Projekt jest oparty o Spring Boot i PostgreSQL.

**Autor:** @RegiZz

## Technologie użyte w projekcie

### Backend
- **Java 21**
- **Spring Boot 3.2.2**
  - Spring Web (REST API)
  - Spring Validation (walidacja DTO)
  - Spring Data JPA (repozytoria i persystencja)
  - Spring Security (domyślne zabezpieczenie endpointów)
- **PostgreSQL** (główna baza danych)
- **Flyway** (wersjonowanie i migracje schematu)
- **MapStruct** (mapowanie modeli domenowych na DTO)
- **springdoc-openapi** (Swagger UI / OpenAPI)

### Testy
- Spring Boot Test
- Spring Security Test
- Testcontainers (PostgreSQL)

### Frontend (przykładowa integracja)
- **React.js** (np. Vite + fetch/axios)

## Wymagania

- Java 21+
- Maven 3.9+ (lub `./mvnw`)
- PostgreSQL 15+ (lokalnie lub w Dockerze)

## Konfiguracja

Domyślna konfiguracja aplikacji (`src/main/resources/application.properties`):

- `spring.datasource.url=jdbc:postgresql://localhost:5432/saasapi`
- `spring.datasource.username=saas`
- `spring.datasource.password=saas`
- `spring.jpa.hibernate.ddl-auto=validate`
- `spring.flyway.enabled=true`

### Szybki start z Dockerem (PostgreSQL)

```bash
docker run --name saasapi-db \
  -e POSTGRES_DB=saasapi \
  -e POSTGRES_USER=saas \
  -e POSTGRES_PASSWORD=saas \
  -p 5432:5432 -d postgres:15
```

## Uruchomienie aplikacji

```bash
./mvnw spring-boot:run
```

Po starcie:

- API: `http://localhost:8080/api/...`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

> Uwaga: ponieważ w projekcie jest dodany starter Spring Security i brak własnej konfiguracji security, endpointy będą domyślnie chronione HTTP Basic. Login to zwykle `user`, a hasło jest generowane przy starcie i wypisywane w logach.

---

## Przykłady użycia API w React.js

Poniżej przykładowa integracja frontendu React z tym API.

### 1) Klient API (`src/api/client.js`)

```js
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const API_USER = import.meta.env.VITE_API_USER || 'user';
const API_PASS = import.meta.env.VITE_API_PASS || 'password';

function authHeader() {
  return `Basic ${btoa(`${API_USER}:${API_PASS}`)}`;
}

export async function apiRequest(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      Authorization: authHeader(),
      ...(options.headers || {}),
    },
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`API error ${response.status}: ${text}`);
  }

  return response.status === 204 ? null : response.json();
}
```

### 2) Operacje na planach (`src/api/plans.js`)

```js
import { apiRequest } from './client';

export const getPlans = () => apiRequest('/api/plans');

export const createPlan = (plan) =>
  apiRequest('/api/plans', {
    method: 'POST',
    body: JSON.stringify(plan),
  });

export const updatePlan = (planId, payload) =>
  apiRequest(`/api/plans/${planId}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  });
```

Przykładowy payload planu:

```js
const payload = {
  code: 'PRO',
  price: 99.99,
  billingPeriod: 'MONTHLY', // MONTHLY | YEARLY
  maxUsers: 20,
  maxProjects: 100,
};
```

### 3) Operacje na użytkowniku i subskrypcji (`src/api/subscriptions.js`)

```js
import { apiRequest } from './client';

export const createUser = (email) =>
  apiRequest('/api/users', {
    method: 'POST',
    body: JSON.stringify({ email }),
  });

export const startTrial = ({ userId, planId, trialEndDate, autoRenew }) =>
  apiRequest('/api/subscriptions/start-trial', {
    method: 'POST',
    body: JSON.stringify({ userId, planId, trialEndDate, autoRenew }),
  });

export const changePlan = (subscriptionId, newPlanId) =>
  apiRequest(`/api/subscriptions/${subscriptionId}/change-plan`, {
    method: 'POST',
    body: JSON.stringify({ newPlanId }),
  });

export const cancelSubscription = (subscriptionId) =>
  apiRequest(`/api/subscriptions/${subscriptionId}/cancel`, {
    method: 'POST',
  });

export const getCurrentSubscription = (userId) =>
  apiRequest(`/api/subscriptions/current/${userId}`);
```

### 4) Limity (`src/api/limits.js`)

```js
import { apiRequest } from './client';

export const getLimits = (userId) => apiRequest(`/api/limits/${userId}`);

export const checkLimits = ({ userId, requestedUsers, requestedProjects }) =>
  apiRequest('/api/limits/check', {
    method: 'POST',
    body: JSON.stringify({ userId, requestedUsers, requestedProjects }),
  });
```

### 5) Przykładowy komponent React (`src/components/SubscriptionPanel.jsx`)

```jsx
import { useEffect, useState } from 'react';
import { getPlans } from '../api/plans';
import { createUser, startTrial } from '../api/subscriptions';

export default function SubscriptionPanel() {
  const [plans, setPlans] = useState([]);
  const [email, setEmail] = useState('jan.kowalski@example.com');
  const [selectedPlanId, setSelectedPlanId] = useState(null);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    getPlans()
      .then((data) => {
        setPlans(data);
        if (data.length > 0) setSelectedPlanId(data[0].id);
      })
      .catch((err) => setError(err.message));
  }, []);

  const handleStartTrial = async () => {
    try {
      setError(null);
      const user = await createUser(email);
      const subscription = await startTrial({
        userId: user.id,
        planId: selectedPlanId,
        trialEndDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
        autoRenew: true,
      });
      setResult(subscription);
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <div>
      <h2>Start trial</h2>
      <input value={email} onChange={(e) => setEmail(e.target.value)} />
      <select
        value={selectedPlanId ?? ''}
        onChange={(e) => setSelectedPlanId(Number(e.target.value))}
      >
        {plans.map((p) => (
          <option key={p.id} value={p.id}>
            {p.code} - {p.price}
          </option>
        ))}
      </select>
      <button onClick={handleStartTrial}>Uruchom trial</button>

      {error && <p style={{ color: 'red' }}>{error}</p>}
      {result && <pre>{JSON.stringify(result, null, 2)}</pre>}
    </div>
  );
}
```

### 6) Zmienne środowiskowe w React (`.env`)

```env
VITE_API_URL=http://localhost:8080
VITE_API_USER=user
VITE_API_PASS=<HASŁO_Z_LOGÓW_SPRING_BOOT>
```

## Logika biznesowa (skrót)

- Jeden użytkownik może mieć tylko jedną aktywną/trialową subskrypcję jednocześnie.
- Trial po przekroczeniu `trialEndDate`:
  - przechodzi na ACTIVE, jeśli `autoRenew=true`,
  - wygasa, jeśli `autoRenew=false`.
- Upgrade planu (droższy) jest natychmiastowy.
- Downgrade planu (tańszy) jest planowany na kolejny okres rozliczeniowy.
- Cron uruchamiany codziennie o **01:00** przetwarza triale i zaplanowane zmiany planów.

## Struktura projektu (wysoki poziom)

- `api/` – kontrolery, DTO, mapery
- `domain/` – model domenowy + serwis z logiką biznesową
- `infrastructure/persistence/` – repozytoria JPA
- `infrastructure/scheduler/` – zadania cykliczne
- `db/migration/` – migracje Flyway

## Testy

```bash
./mvnw test
```

# Academy Frontend

React 18 + TypeScript frontend for Academy Management System, with OpenAPI-driven API consumption.

## Features

- **OpenAPI-Driven**: API clients and types are automatically generated from the backend's OpenAPI specification
- **Realtime Updates**: WebSocket integration with STOMP for live data synchronization
- **Type Safety**: Full TypeScript support with generated types matching backend DTOs
- **Modern Stack**: React 18, Vite, Tailwind CSS, React Query
- **JWT Authentication**: Secure token-based authentication

## Prerequisites

- Node.js 20+
- npm or yarn

## Setup

### 1. Install Dependencies

```bash
npm install
```

This will automatically:
- Install all dependencies
- Generate API client from OpenAPI specification (via `postinstall` script)

### 2. Generate API Client (Manual)

If you need to regenerate the API client manually:

```bash
npm run generate
```

This reads the OpenAPI specification from `../modules/academy-api/src/main/resources/openapi.yaml` and generates:
- TypeScript types matching backend DTOs
- API client class (`AcademyApi`) with all endpoints
- Request/response interfaces

### 3. Development

```bash
npm run dev
```

The app will be available at `http://localhost:5173`

### 4. Build for Production

```bash
npm run build
```

Output will be in the `dist/` directory.

## Project Structure

```
frontend/
├── src/
│   ├── api/
│   │   ├── generated/          # Auto-generated API client (gitignored)
│   │   ├── generated-client.ts # Wrapper for generated client with auth
│   │   ├── students.ts         # Student API wrapper
│   │   ├── batches.ts          # Batch API wrapper
│   │   └── auth.ts             # Auth API wrapper
│   ├── components/             # React components
│   ├── pages/                  # Page components
│   ├── hooks/                  # Custom React hooks
│   ├── context/                # React context providers
│   ├── ws/                     # WebSocket client
│   └── ...
├── package.json
└── vite.config.ts
```

## API Client Usage

### Using Generated Client Directly

```typescript
import { apiClient } from './api/generated-client'
import type { Student, StudentInput } from './api/generated'

// All types match backend DTOs exactly
const student: StudentInput = {
  name: 'John Doe',
  email: 'john@example.com',
}

const response = await apiClient.createStudent(student)
const createdStudent: Student = response.data
```

### Using API Wrappers

```typescript
import { studentsApi } from './api/students'
import type { Student } from './api/students'

const students = await studentsApi.getAll()
const student = await studentsApi.getById(1)
```

## Environment Variables

Create a `.env` file:

```bash
VITE_API_URL=http://localhost:8080
VITE_WS_URL=ws://localhost:8080/ws
VITE_APP_NAME=Academy Management System
```

## Regenerating API Client

The API client is automatically regenerated on `npm install` via the `postinstall` script.

To regenerate manually:

```bash
npm run generate
```

**Note**: The generated code is in `src/generated/` and is gitignored. It should be regenerated whenever the backend OpenAPI specification changes.

## Type Safety

All API types are generated from the OpenAPI specification, ensuring:
- Request/response types match backend DTOs exactly
- Type safety across the entire API surface
- Automatic updates when backend changes

## Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run generate` - Generate API client from OpenAPI spec
- `npm run lint` - Run ESLint
- `npm run test` - Run tests
- `npm run format` - Format code with Prettier

## Troubleshooting

### API Client Not Generated

If the generated client is missing:

1. Ensure the OpenAPI file exists at `../modules/academy-api/src/main/resources/openapi.yaml`
2. Run `npm run generate` manually
3. Check for errors in the generation process

### Type Mismatches

If you see type errors after backend changes:

1. Update the backend OpenAPI specification
2. Run `npm run generate` to regenerate types
3. Restart TypeScript server in your IDE

### Build Errors

If build fails:

1. Ensure all dependencies are installed: `npm install`
2. Regenerate API client: `npm run generate`
3. Check TypeScript errors: `npm run build`


# API Client Generation Guide

## Overview

The frontend uses **OpenAPI-driven API consumption** with automatically generated TypeScript clients. All API types and clients are generated from the backend's OpenAPI specification, ensuring type safety and consistency.

## How It Works

1. **Source**: OpenAPI specification at `../modules/academy-api/src/main/resources/openapi.yaml`
2. **Generator**: `swagger-typescript-api` (npm package)
3. **Output**: Generated code in `src/generated/` directory
4. **Auto-generation**: Runs automatically on `npm install` via `postinstall` script

## Generated Files

After running `npm run generate`, you'll get:

```
src/generated/
├── index.ts          # Main API client class (AcademyApi)
├── data-contracts/   # TypeScript interfaces matching backend DTOs
└── http-client/      # HTTP client utilities
```

## Usage

### Direct API Client Usage

```typescript
import { apiClient } from './api/generated-client'
import type { Student, StudentInput } from './api/generated'

// Types match backend DTOs exactly
const student: StudentInput = {
  name: 'John Doe',
  email: 'john@example.com',
}

const response = await apiClient.createStudent(student)
const created: Student = response.data
```

### Using API Wrappers

```typescript
import { studentsApi } from './api/students'
import type { Student } from './api/students'

// Wrapper provides cleaner API
const students = await studentsApi.getAll()
```

## Regenerating API Client

### Automatic (Recommended)

The API client is automatically regenerated when you run:

```bash
npm install
```

This happens via the `postinstall` script in `package.json`.

### Manual

To regenerate manually:

```bash
npm run generate
```

## When to Regenerate

Regenerate the API client whenever:

1. **Backend OpenAPI spec changes** - New endpoints, updated DTOs, etc.
2. **After pulling backend changes** - If OpenAPI spec was updated
3. **Type errors appear** - If types don't match backend anymore

## Configuration

The generation is configured in `package.json`:

```json
{
  "scripts": {
    "generate": "swagger-typescript-api --union-enums --api-class-name AcademyApi --module-name-index Infinity -p ../modules/academy-api/src/main/resources/openapi.yaml -o src/generated -n index.ts --axios"
  }
}
```

### Options Explained

- `--union-enums`: Generate TypeScript union types for enums
- `--api-class-name AcademyApi`: Name of the generated API class
- `-p <path>`: Path to OpenAPI YAML file
- `-o src/generated`: Output directory
- `-n index.ts`: Main output file name
- `--axios`: Generate axios-based client

## Troubleshooting

### Generation Fails

**Error**: Cannot find OpenAPI file

**Solution**: Ensure the path is correct:
```bash
ls ../modules/academy-api/src/main/resources/openapi.yaml
```

### Types Don't Match

**Error**: Type errors after backend changes

**Solution**: 
1. Ensure backend OpenAPI spec is updated
2. Run `npm run generate`
3. Restart TypeScript server in IDE

### Generated Code Issues

**Error**: Generated code has syntax errors

**Solution**:
1. Check OpenAPI spec is valid: `swagger-typescript-api --validate`
2. Update `swagger-typescript-api` version
3. Check for OpenAPI spec syntax errors

## Best Practices

1. **Don't edit generated files** - They will be overwritten
2. **Use wrappers** - Create wrapper functions in `src/api/` for cleaner APIs
3. **Type imports** - Import types from `./api/generated` or wrapper files
4. **Version control** - Generated code is gitignored (see `.gitignore`)
5. **CI/CD** - Regenerate in build pipeline if needed

## Integration with Existing Code

The generated client is integrated via:

1. **`generated-client.ts`**: Wraps generated client with auth interceptors
2. **API wrappers**: `students.ts`, `batches.ts`, `auth.ts` provide clean APIs
3. **Type exports**: Re-export types for convenience

This ensures:
- ✅ JWT authentication works automatically
- ✅ Error handling is consistent
- ✅ Types are always in sync with backend
- ✅ Clean API surface for components


const BASE_URL = '/api';

export class ApiError extends Error {
    status: number;
    constructor(status: number, message: string) {
        super(message);
        this.status = status;
    }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
    const headers: HeadersInit = {
        'Content-Type': 'application/json',
        ...options.headers,
    };
    const response = await fetch(`${BASE_URL}${path}`, { ...options, headers, credentials: 'include' });
    if (!response.ok) {
        throw new ApiError(response.status, `Request to ${path} failed with ${response.status}`);
    }
    if (response.status === 204) {
        return undefined as T;
    }
    const contentType = response.headers.get('content-type');
    if (!contentType || !contentType.includes('application/json')) {
        return undefined as T;
    }
    return response.json() as Promise<T>;
}

export const apiClient = {
    get: <T>(path: string) => request<T>(path),
    post: <T>(path: string, body?: unknown) =>
        request<T>(path, { method: 'POST', body: body !== undefined ? JSON.stringify(body) : undefined }),
    put: <T>(path: string, body: unknown) => request<T>(path, { method: 'PUT', body: JSON.stringify(body) }),
};
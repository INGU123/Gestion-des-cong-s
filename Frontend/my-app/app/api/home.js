"use client";

const API_BASE_URL = "http://localhost:8080";

export async function postData(endpoint, data) {
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    throw new Error(`Erreur API: ${response.status}`);
  }
  return await response.json();
}

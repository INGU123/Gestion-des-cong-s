const API_URL = "http://localhost:8080/utilisateur";

export const loginUser = async (email, motDePass) => {
  const response = await fetch(`${API_URL}/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      email: email ? email.trim() : "",
      password: motDePass,
      mot_de_pass: motDePass,
      motDePass: motDePass,
    }),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "Identifiants invalides");
  }

  return await response.json(); // Retourne { token, utilisateur }
};
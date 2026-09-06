const API_URL = "http://localhost:8080/utilisateur";

export  const loginUser = async (email, motDePass) => {
  const response = await fetch(`${API_URL}/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      email: email,
      mot_de_pass: motDePass,
    }),
  });

  if (!response.ok) {
    throw new Error("Identifiants invalides");
  }

  return await response.json(); // Renvoie les données de l'utilisateur connecté
};
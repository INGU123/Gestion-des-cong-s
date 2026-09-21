import { authFetch } from "@/lib/apiClient";

const API_URL = "http://localhost:8080/historiques";

export const getAllHistorique = async () => {
  try {
    const response = await authFetch(`${API_URL}/all`);
    if (!response.ok) {
      console.warn(`Avertissement HTTP getAllHistorique: ${response.status}`);
      return [];
    }
    const data = await response.json();
    return Array.isArray(data) ? data : [];
  } catch (error) {
    console.warn("Erreur lors de la récupération de l'historique général: ", error.message);
    return [];
  }
};

export const getHistoriqueUtilisateur = async (utilisateurId) => {
  if (!utilisateurId) return [];

  try {
    const response = await authFetch(`${API_URL}/utilisateur/${utilisateurId}`);
    if (!response.ok) {
      console.warn(`Avertissement HTTP getHistoriqueUtilisateur: ${response.status}`);
      return [];
    }
    const data = await response.json();
    return Array.isArray(data) ? data : [];
  } catch (error) {
    console.warn("Erreur lors de la récupération des mouvements: ", error.message);
    return [];
  }
};
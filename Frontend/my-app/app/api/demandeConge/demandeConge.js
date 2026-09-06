const API_URL = "http://localhost:8080/conge";

export const creerDemandeConge = async (utilisateurId, demandeData) => {
  const response = await fetch(`${API_URL}/demander/${utilisateurId}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(demandeData),
  });
  if (!response.ok) throw new Error("Erreur lors de la création de la demande");
  return await response.json();
};

export const traiterDemandeConge = async (
  demandeId,
  managerId,
  statut,
  motifRefus = "",
) => {
  const params = new URLSearchParams({ managerId, statut, motifRefus });
  const response = await fetch(
    `${API_URL}/${demandeId}/traiter?${params.toString()}`,
    {
      method: "PUT",
    },
  );
  if (!response.ok) throw new Error("Erreur lors du traitement de la demande");
  return await response.json();
};

export const annulerDemandeConge = async (demandeId, utilisateurId) => {
  const response = await fetch(
    `${API_URL}/${demandeId}/annuler?utilisateurId=${utilisateurId}`,
    {
      method: "PUT",
    },
  );
  if (!response.ok)
    throw new Error("Erreur lors de l'annulation de la demande");
  return await response.json();
};

export const getMesDemandes = async (utilisateurId) => {
  const response = await fetch(`${API_URL}/mes-demandes/${utilisateurId}`);
  if (!response.ok)
    throw new Error("Erreur lors de la récupération des demandes");
  return await response.json();
};

export const getDemandesEnAttenteManager = async (managerId) => {
  const response = await fetch(`${API_URL}/manager/${managerId}/en-attente`);
  if (!response.ok)
    throw new Error("Erreur lors de la récupération des demandes du manager");
  return await response.json();
};

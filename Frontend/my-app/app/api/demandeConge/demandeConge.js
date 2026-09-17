const API_URL = "http://localhost:8080/conge";

export const creerDemandeConge = async (utilisateurId, demandeData) => {
  const formatDate = (date) => {
    try{
      
    if (!date) return "";
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    console.log(`${year}/${month}/${day}`);
    return `${year}/${month}/${day}`;
    }catch(error){
      console.error("Erreur obtenue est: ",error.message);
      throw error;
    }
  };

  const response = await fetch(`${API_URL}/demander/${utilisateurId}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      typeCongeId: Number(demandeData.typeCongeId),
      dateDebut: formatDate(demandeData.debut),
      dateFin: formatDate(demandeData.fin),
      commentaire: demandeData.commentaire.trim(),
    }),
  });

  if (!response.ok) {
    const details = await response.text();
    throw new Error(
      details || `Erreur lors de la création de la demande (${response.status})`,
    );
  }
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

export const getToutesDemandesEnAttente = async () => {
  const response = await fetch(`${API_URL}/en-attente`);
  if (!response.ok)
    throw new Error("Erreur lors de la récupération de toutes les demandes en attente");
  return await response.json();
};

export const getAllDemandes = async () => {
  const response = await fetch(`${API_URL}/all`);
  if (!response.ok)
    throw new Error("Erreur lors de la récupération de toutes les demandes");
  return await response.json();
};


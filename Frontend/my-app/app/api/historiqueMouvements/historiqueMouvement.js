const API_URL = "http://localhost:8080/historique";

export const getAllHistorique = async () => {
  const response = await fetch(`${API_URL}/all`);
  if (!response.ok) throw Error("Erreur de recuperation de l'historique.");
  return await response.json();
};

export const getHistoriqueUtilisateur=async(utilisateurId)=>{
    const response=await fetch(`${API_URL}/utilisateur/${utilisateurId}`);
    if(!response.ok)throw Error("Erreur de recuperation de des mouvement de l'utilisateur");
    return await response.json();
}
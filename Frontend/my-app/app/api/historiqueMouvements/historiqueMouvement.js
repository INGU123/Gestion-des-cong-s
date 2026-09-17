const API_URL = "http://localhost:8080/historiques";

export const getAllHistorique = async () => {
  try{
    const response = await fetch(`${API_URL}/all`);
  if (!response.ok) throw new Error("Erreur de recuperation de l'historique.");
  return await response.json();
  }catch(error){
    console.error("Erreur obtenus: ",error.message);
    throw error;
  }
};

export const getHistoriqueUtilisateur=async(utilisateurId)=>{
    try{
      const response=await fetch(`${API_URL}/utilisateur/${utilisateurId}`);
    if(!response.ok)throw new Error("Erreur de recuperation de des mouvement de l'utilisateur");
    return await response.json();
    }catch(error){
      console.error("Erreur obtenu: ",error.message);
      throw error;
    }
}
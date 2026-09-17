const API_URL="http://localhost:8080/solde";

export const getSoldesUtilisateur=async(utilisateurId)=>{
    try{
        const response=await fetch(`${API_URL}/utilisateur/${utilisateurId}`);
    if(!response.ok)throw new Error("Erreur lors de la recuperation du solde de l'utilisateur");
    return response.json();
    }catch(error){
        console.error("Erreur obtenue: ",error.message);
        throw error;
    }
}

export const initialiserSoldesAnnuels=async(utilisateurId,annee)=>{
    try{
        const response=await fetch(`${API_URL}/initialiser/${utilisateurId}?annee=${annee}`,{
            method:"POST",
        })
            if(!response.ok)throw new Error("Erreur d'initialisation de solde annuelle");
            return true;

    }catch(error){
        console.log("Erreur lors de l'initialisation ",error.message);
        throw error;
    }
}

export const getAllSoldes = async () => {
  try {
    const response = await fetch(`${API_URL}/all`);
    if (!response.ok) throw new Error("Erreur lors de la récupération des soldes");
    return await response.json();
  } catch (error) {
    console.error("Erreur obtenue: ", error.message);
    throw error;
  }
};

export const ajustSolde = async (soldeId, nouveauNombreJoursRestants) => {
  try {
    const response = await fetch(
      `${API_URL}/ajuster/${soldeId}?nouveauNombreJoursRestants=${encodeURIComponent(nouveauNombreJoursRestants)}`,
      {
        method: "PUT",
      }
    );
    if (!response.ok) throw new Error("Erreur de mise à jour du solde");
    return await response.json();
  } catch (error) {
    console.error("Erreur obtenue :", error.message);
    throw error;
  }
};
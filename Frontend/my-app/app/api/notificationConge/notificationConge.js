const API_URL="http://localhost:8080/notification";

export const getNotificationNonLues=async(utilisateurId)=>{
    const response=await fetch(`${API_URL}/utilisateur${utilisateurId}/non-lues`);
    if(!response.ok)throw new Error("Erreur lors de la recuperation des notifications");
    return await response.json();
}

export const marquerNotificaitonNonLue=async(notificaitonId)=>{
    const response=await fetch(`${API_URL}/${notificaitonId}/lire`,{
        method:"PUT",
    });
    if(!response.ok)throw new Error("Erreur lors de la mise a jour du notification");
    return true;
}
const API_URL = "http://localhost:8080/notification";

export const getNotificationNonLues = async (utilisateurId) => {
  try {
    const response = await fetch(`${API_URL}/utilisateur/${utilisateurId}/non-lues`);
    if (!response.ok) throw new Error("Erreur lors de la récupération des notifications non lues");
    return await response.json();
  } catch (error) {
    console.error("Erreur obtenue :", error.message);
    throw error;
  }
};

export const getToutesLesNotifications = async (utilisateurId) => {
  try {
    const response = await fetch(`${API_URL}/utilisateur/${utilisateurId}`);
    if (!response.ok) throw new Error("Erreur lors de la récupération des notifications");
    return await response.json();
  } catch (error) {
    console.error("Erreur obtenue :", error.message);
    throw error;
  }
};

export const marquerNotificationLue = async (notificationId) => {
  try {
    const response = await fetch(`${API_URL}/${notificationId}/lire`, {
      method: "PUT",
    });
    if (!response.ok) throw new Error("Erreur lors de la mise à jour de la notification");
    return true;
  } catch (error) {
    console.error("Erreur obtenue :", error.message);
    throw error;
  }
};

export const marquerNotificaitonNonLue = marquerNotificationLue;
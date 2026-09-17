"use client";

import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { useEffect, useState } from "react";
import {
  annulerDemandeConge,
  creerDemandeConge,
  getMesDemandes,
} from "../../api/demandeConge/demandeConge.js";
import { getAllTypeConge } from "../../api/typeConge/typeConge.js";

export default function DemandesPage() {
  const [utilisateurId, setUtilisateurId] = useState(null);
  const [formData, setFormData] = useState({
    typeCongeId: "",
    debut: null,
    fin: null,
    commentaire: "",
  });
  const [demandes, setDemandes] = useState([]);
  const [typesConge, setTypesConge] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const storedUser = localStorage.getItem("user");
        const user = storedUser ? JSON.parse(storedUser) : null;
        const currentUserId = Number(user?.id);
        if (!currentUserId) {
          throw new Error("Utilisateur connecté introuvable");
        }

        setUtilisateurId(currentUserId);
        const [demandesData, typesData] = await Promise.all([
          getMesDemandes(currentUserId),
          getAllTypeConge(),
        ]);
        setDemandes(demandesData);
        setTypesConge(typesData);
      } catch (err) {
        console.error("Erreur lors du chargement des demandes:", err);
      }
    };

    fetchData();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!utilisateurId) {
      alert("Utilisateur connecté introuvable");
      return;
    }
    if (!formData.typeCongeId || !formData.debut || !formData.fin) {
      alert("Veuillez renseigner le type et les deux dates de congé");
      return;
    }
    if (formData.fin < formData.debut) {
      alert("La date de fin doit être postérieure ou égale à la date de début");
      return;
    }

    setLoading(true);
    try {
      const nouvelleDemande = await creerDemandeConge(utilisateurId, formData);
      setDemandes((currentDemandes) => [...currentDemandes, nouvelleDemande]);
      setFormData({
        typeCongeId: "",
        debut: null,
        fin: null,
        commentaire: "",
      });
    } catch (err) {
      console.error("Erreur création demande:", err);
      alert(`Erreur lors de la création de la demande: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleAnnuler = async (demandeId) => {
    if (!utilisateurId) return;
    try {
      const updated = await annulerDemandeConge(demandeId, utilisateurId);
      setDemandes((currentDemandes) =>
        currentDemandes.map((demande) =>
          demande.id === demandeId ? updated : demande,
        ),
      );
    } catch (err) {
      console.error("Erreur annulation demande:", err);
      alert("Erreur lors de l'annulation");
    }
  };

  const getTypeLabel = (typeCongeId) => {
    const type = typesConge.find((item) => item.id === typeCongeId);
    return type?.libelle || type?.code || `Type #${typeCongeId}`;
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Mes demandes de congé</h1>

      <form
        onSubmit={handleSubmit}
        className="bg-white shadow p-4 rounded mb-6 space-y-4"
      >
        <div>
          <label htmlFor="typeCongeId" className="block font-bold">
            Type de congé
          </label>
          <select
            id="typeCongeId"
            name="typeCongeId"
            value={formData.typeCongeId}
            onChange={(e) =>
              setFormData({ ...formData, typeCongeId: e.target.value })
            }
            className="border rounded p-2 w-full"
            required
          >
            <option value="">Sélectionner un type</option>
            {typesConge
              .filter((type) => type.actif !== false)
              .map((type) => (
                <option key={type.id} value={type.id}>
                  {type.libelle || type.code}
                </option>
              ))}
          </select>
        </div>

        <div className="flex space-x-2 justify-center">
          <div>
            <label className="block text-center underline font-bold">
              Date début
            </label>
            <DatePicker
              selected={formData.debut}
              onChange={(date) => setFormData({ ...formData, debut: date })}
              dateFormat="yyyy-MM-dd"
              inline
            />
          </div>

          <div>
            <label className="block text-center underline font-bold">
              Date fin
            </label>
            <DatePicker
              selected={formData.fin}
              onChange={(date) => setFormData({ ...formData, fin: date })}
              dateFormat="yyyy-MM-dd"
              inline
            />
          </div>
        </div>

        <div>
          <label htmlFor="commentaire" className="block font-bold">
            Commentaire
          </label>
          <textarea
            id="commentaire"
            name="commentaire"
            value={formData.commentaire}
            onChange={(e) =>
              setFormData({ ...formData, commentaire: e.target.value })
            }
            className="border rounded p-2 w-full"
            rows="3"
            required
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          {loading ? "Envoi..." : "Soumettre"}
        </button>
      </form>

      <div className="bg-white shadow p-4 rounded">
        <h2 className="text-xl font-semibold mb-3">Historique</h2>
        {demandes.length === 0 ? (
          <p>Aucune demande pour l’instant.</p>
        ) : (
          <ul className="space-y-2">
            {demandes.map((demande) => (
              <li
                key={demande.id}
                className="flex justify-between items-center border-b py-2"
              >
                <div>
                  <p>
                    <strong>{getTypeLabel(demande.typeCongeId)}</strong> du{" "}
                    {demande.dateDebut} au {demande.dateFin}
                  </p>
                  <p>
                    Statut: <span className="font-bold">{demande.statut}</span>
                  </p>
                </div>
                {demande.statut === "EN_ATTENTE" && (
                  <button
                    onClick={() => handleAnnuler(demande.id)}
                    className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
                  >
                    Annuler
                  </button>
                )}
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}

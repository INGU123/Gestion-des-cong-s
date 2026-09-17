"use client";

import { useEffect, useState } from "react";
import {
  getToutesDemandesEnAttente,
  getAllDemandes,
  traiterDemandeConge,
} from "../../api/demandeConge/demandeConge";
import { getAllTypeConge } from "../../api/typeConge/typeConge";

export default function ValidationPage() {
  const [manager, setManager] = useState(null);
  const [demandes, setDemandes] = useState([]);
  const [typesConge, setTypesConge] = useState([]);
  const [utilisateurs, setUtilisateurs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState("EN_ATTENTE"); // EN_ATTENTE or TOUTES
  const [modalRefus, setModalRefus] = useState({ open: false, demandeId: null, motif: "" });
  const [actionLoading, setActionLoading] = useState(null);
  const [msg, setMsg] = useState({ type: "", text: "" });

  const loadData = async () => {
    try {
      setLoading(true);
      const [demandesData, typesData, usersData] = await Promise.all([
        filter === "EN_ATTENTE" ? getToutesDemandesEnAttente() : getAllDemandes(),
        getAllTypeConge().catch(() => []),
        fetch("http://localhost:8080/utilisateur/all").then(r => r.json()).catch(() => []),
      ]);
      setDemandes(Array.isArray(demandesData) ? demandesData : []);
      setTypesConge(Array.isArray(typesData) ? typesData : []);
      setUtilisateurs(Array.isArray(usersData) ? usersData : []);
    } catch (err) {
      console.error("Erreur chargement validation:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const stored = localStorage.getItem("user");
    if (stored) {
      try {
        setManager(JSON.parse(stored));
      } catch (e) {
        console.error(e);
      }
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [filter]);

  const getUserName = (userId) => {
    const found = utilisateurs.find((u) => u.id === userId);
    if (!found) return `Utilisateur #${userId}`;
    return found.prenom ? `${found.prenom} ${found.nom}` : found.email;
  };

  const getTypeLabel = (typeId) => {
    const found = typesConge.find((t) => t.id === typeId);
    return found?.libelle || found?.code || `Type #${typeId}`;
  };

  const calculateDays = (debut, fin) => {
    if (!debut || !fin) return 1;
    const d1 = new Date(debut);
    const d2 = new Date(fin);
    const diffTime = Math.abs(d2 - d1);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
    return isNaN(diffDays) ? 1 : diffDays;
  };

  const handleValider = async (demandeId) => {
    if (!manager?.id) {
      alert("Manager non identifié.");
      return;
    }
    setActionLoading(demandeId);
    setMsg({ type: "", text: "" });
    try {
      await traiterDemandeConge(demandeId, manager.id, "VALIDEE");
      setDemandes((prev) =>
        prev.map((d) => (d.id === demandeId ? { ...d, statut: "VALIDEE" } : d))
      );
      setMsg({ type: "success", text: "Demande validée avec succès !" });
    } catch (err) {
      setMsg({ type: "error", text: `Erreur validation : ${err.message}` });
    } finally {
      setActionLoading(null);
    }
  };

  const handleOpenRefus = (demandeId) => {
    setModalRefus({ open: true, demandeId, motif: "" });
  };

  const handleConfirmRefus = async () => {
    if (!manager?.id || !modalRefus.demandeId) return;
    setActionLoading(modalRefus.demandeId);
    try {
      await traiterDemandeConge(
        modalRefus.demandeId,
        manager.id,
        "REFUSEE",
        modalRefus.motif
      );
      setDemandes((prev) =>
        prev.map((d) =>
          d.id === modalRefus.demandeId
            ? { ...d, statut: "REFUSEE", motifRefus: modalRefus.motif }
            : d
        )
      );
      setMsg({ type: "success", text: "Demande refusée." });
      setModalRefus({ open: false, demandeId: null, motif: "" });
    } catch (err) {
      setMsg({ type: "error", text: `Erreur refus : ${err.message}` });
    } finally {
      setActionLoading(null);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-800 flex items-center gap-2">
            <span>✅</span> Validation des Congés
          </h1>
          <p className="text-slate-500 text-sm mt-1">
            Examinez, validez ou refusez les demandes de congés soumises par votre équipe.
          </p>
        </div>

        <div className="join">
          <button
            onClick={() => setFilter("EN_ATTENTE")}
            className={`join-item btn btn-sm ${
              filter === "EN_ATTENTE" ? "btn-primary" : "btn-outline"
            }`}
          >
            En attente uniquement
          </button>
          <button
            onClick={() => setFilter("TOUTES")}
            className={`join-item btn btn-sm ${
              filter === "TOUTES" ? "btn-primary" : "btn-outline"
            }`}
          >
            Toutes les demandes
          </button>
        </div>
      </div>

      {msg.text && (
        <div
          className={`alert ${
            msg.type === "success" ? "alert-success text-white" : "alert-error text-white"
          } shadow-sm`}
        >
          <span>{msg.text}</span>
        </div>
      )}

      <div className="bg-white shadow-sm border border-slate-200 rounded-2xl p-6">
        {loading ? (
          <div className="flex justify-center items-center py-16">
            <span className="loading loading-spinner loading-lg text-primary"></span>
          </div>
        ) : demandes.length === 0 ? (
          <div className="text-center py-16 text-slate-400">
            <span className="text-4xl block mb-2">🎉</span>
            <p className="font-semibold text-slate-600">Aucune demande trouvée pour ce filtre.</p>
            <p className="text-sm mt-1">Toutes les demandes ont été traitées !</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="table w-full">
              <thead>
                <tr className="bg-slate-50 text-slate-600 text-xs uppercase tracking-wider">
                  <th>Collaborateur</th>
                  <th>Type de congé</th>
                  <th>Dates</th>
                  <th>Durée</th>
                  <th>Motif / Commentaire</th>
                  <th>Statut</th>
                  <th className="text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                {demandes.map((demande) => {
                  const days = calculateDays(demande.dateDebut, demande.dateFin);
                  const isPending = demande.statut === "EN_ATTENTE";

                  return (
                    <tr key={demande.id} className="hover:bg-slate-50/60 border-b border-slate-100">
                      <td className="font-bold text-slate-800">
                        {getUserName(demande.utilisateurId)}
                      </td>
                      <td className="font-medium text-slate-700">
                        {getTypeLabel(demande.typeCongeId)}
                      </td>
                      <td className="text-xs text-slate-600">
                        <span className="font-medium">{demande.dateDebut}</span> au{" "}
                        <span className="font-medium">{demande.dateFin}</span>
                      </td>
                      <td>
                        <span className="badge badge-sm badge-info text-white font-bold">
                          {days} j
                        </span>
                      </td>
                      <td className="text-xs text-slate-500 max-w-xs truncate" title={demande.commentaire}>
                        {demande.commentaire || "-"}
                      </td>
                      <td>
                        <span
                          className={`badge badge-sm font-semibold ${
                            demande.statut === "VALIDEE"
                              ? "badge-success text-white"
                              : demande.statut === "REFUSEE"
                              ? "badge-error text-white"
                              : "badge-warning text-slate-800"
                          }`}
                        >
                          {demande.statut}
                        </span>
                      </td>
                      <td className="text-center">
                        {isPending ? (
                          <div className="flex gap-2 justify-center">
                            <button
                              onClick={() => handleValider(demande.id)}
                              disabled={actionLoading === demande.id}
                              className="btn btn-success btn-xs text-white"
                            >
                              {actionLoading === demande.id ? "..." : "Valider"}
                            </button>
                            <button
                              onClick={() => handleOpenRefus(demande.id)}
                              disabled={actionLoading === demande.id}
                              className="btn btn-error btn-xs text-white"
                            >
                              Refuser
                            </button>
                          </div>
                        ) : (
                          <span className="text-xs text-slate-400 italic">Déjà traité</span>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal de motif de refus */}
      {modalRefus.open && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-xl max-w-md w-full p-6 space-y-4">
            <h3 className="text-lg font-bold text-slate-800 flex items-center gap-2">
              <span>❌</span> Refuser la demande
            </h3>
            <p className="text-sm text-slate-500">
              Veuillez indiquer le motif du refus pour informer le collaborateur :
            </p>
            <textarea
              rows={3}
              value={modalRefus.motif}
              onChange={(e) => setModalRefus({ ...modalRefus, motif: e.target.value })}
              placeholder="Ex: Période de forte activité, effectif minimum non atteint..."
              className="textarea textarea-bordered w-full text-slate-800"
            />
            <div className="flex justify-end gap-2 pt-2">
              <button
                onClick={() => setModalRefus({ open: false, demandeId: null, motif: "" })}
                className="btn btn-ghost btn-sm"
              >
                Annuler
              </button>
              <button
                onClick={handleConfirmRefus}
                className="btn btn-error btn-sm text-white"
              >
                Confirmer le refus
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}


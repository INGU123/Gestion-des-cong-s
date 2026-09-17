"use client";

import { useEffect, useState } from "react";
import {
  getAllHistorique,
  getHistoriqueUtilisateur,
} from "../../api/historiqueMouvements/historiqueMouvement";
import { getAllTypeConge } from "../../api/typeConge/typeConge";

export default function HistoriquePage() {
  const [user, setUser] = useState(null);
  const [mouvements, setMouvements] = useState([]);
  const [typesConge, setTypesConge] = useState([]);
  const [utilisateurs, setUtilisateurs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [viewAll, setViewAll] = useState(false);

  useEffect(() => {
    const stored = localStorage.getItem("user");
    if (stored) {
      try {
        const u = JSON.parse(stored);
        setUser(u);
        const isManager = u.role === "ADMIN" || u.role === "MANAGER";
        setViewAll(isManager);
      } catch (e) {
        console.error(e);
      }
    }
  }, []);

  const loadData = async () => {
    if (!user) return;
    try {
      setLoading(true);
      const isManager = user.role === "ADMIN" || user.role === "MANAGER";
      const [mouvementsData, typesData, usersData] = await Promise.all([
        (viewAll && isManager)
          ? getAllHistorique().catch(() => [])
          : getHistoriqueUtilisateur(user.id).catch(() => []),
        getAllTypeConge().catch(() => []),
        fetch("http://localhost:8080/utilisateur/all").then((r) => r.json()).catch(() => []),
      ]);
      setMouvements(Array.isArray(mouvementsData) ? mouvementsData : []);
      setTypesConge(Array.isArray(typesData) ? typesData : []);
      setUtilisateurs(Array.isArray(usersData) ? usersData : []);
    } catch (err) {
      console.error("Erreur chargement historique:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user) {
      loadData();
    }
  }, [user, viewAll]);

  const getUserName = (userId) => {
    const found = utilisateurs.find((u) => u.id === userId);
    if (!found) return `Utilisateur #${userId}`;
    return found.prenom ? `${found.prenom} ${found.nom}` : found.email;
  };

  const getTypeLabel = (typeId) => {
    const found = typesConge.find((t) => t.id === typeId);
    return found?.libelle || found?.code || `Type #${typeId}`;
  };

  const isManager = user?.role === "ADMIN" || user?.role === "MANAGER";

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-800 flex items-center gap-2">
            <span>📜</span> Historique des Mouvements
          </h1>
          <p className="text-slate-500 text-sm mt-1">
            Traçabilité complète des débits, crédits et ajustements de soldes de congés.
          </p>
        </div>

        {isManager && (
          <div className="join">
            <button
              onClick={() => setViewAll(false)}
              className={`join-item btn btn-sm ${!viewAll ? "btn-primary" : "btn-outline"}`}
            >
              Mes mouvements
            </button>
            <button
              onClick={() => setViewAll(true)}
              className={`join-item btn btn-sm ${viewAll ? "btn-primary" : "btn-outline"}`}
            >
              Tous les collaborateurs
            </button>
          </div>
        )}
      </div>

      <div className="bg-white shadow-sm border border-slate-200 rounded-2xl p-6">
        {loading ? (
          <div className="flex justify-center items-center py-16">
            <span className="loading loading-spinner loading-lg text-primary"></span>
          </div>
        ) : mouvements.length === 0 ? (
          <div className="text-center py-16 text-slate-400">
            <span className="text-4xl block mb-2">📜</span>
            <p className="font-semibold text-slate-600">Aucun mouvement enregistré.</p>
            <p className="text-sm mt-1">Les mouvements apparaîtront lors de la validation des congés ou ajustements.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="table w-full">
              <thead>
                <tr className="bg-slate-50 text-slate-600 text-xs uppercase tracking-wider">
                  <th>Date</th>
                  <th>Collaborateur</th>
                  <th>Type de Mouvement</th>
                  <th>Type de Congé</th>
                  <th>Quantité</th>
                  <th>Commentaire</th>
                  <th>Effectué par</th>
                </tr>
              </thead>
              <tbody>
                {mouvements.slice().reverse().map((m, idx) => (
                  <tr key={m.id || idx} className="hover:bg-slate-50/60 border-b border-slate-100">
                    <td className="text-xs text-slate-600 font-medium">
                      {m.date ? new Date(m.date).toLocaleDateString("fr-FR") : "-"}
                    </td>
                    <td className="font-bold text-slate-800">
                      {getUserName(m.utilisateurId)}
                    </td>
                    <td>
                      <span
                        className={`badge badge-sm font-semibold ${
                          m.typeMouvement === "DEBIT_CONGE"
                            ? "badge-error text-white"
                            : "badge-success text-white"
                        }`}
                      >
                        {m.typeMouvement}
                      </span>
                    </td>
                    <td className="font-medium text-slate-700">
                      {getTypeLabel(m.type_conge_id)}
                    </td>
                    <td className="font-bold text-blue-600">
                      {m.quantite} j
                    </td>
                    <td className="text-xs text-slate-500 max-w-xs truncate" title={m.commentaire}>
                      {m.commentaire || "-"}
                    </td>
                    <td className="text-xs text-slate-600">
                      {m.effectue_par ? getUserName(m.effectue_par) : "Système"}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

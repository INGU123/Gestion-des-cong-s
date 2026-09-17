"use client";

import { useEffect, useState } from "react";
import {
  getSoldesUtilisateur,
  initialiserSoldesAnnuels,
} from "../../api/soldeConge/soldeConge";
import { getAllTypeConge } from "../../api/typeConge/typeConge";

export default function SoldeConge() {
  const [user, setUser] = useState(null);
  const [soldes, setSoldes] = useState([]);
  const [typesConge, setTypesConge] = useState([]);
  const [loading, setLoading] = useState(true);
  const [initLoading, setInitLoading] = useState(false);
  const [msg, setMsg] = useState("");

  const loadData = async (userId) => {
    try {
      setLoading(true);
      const [soldesData, typesData] = await Promise.all([
        getSoldesUtilisateur(userId).catch(() => []),
        getAllTypeConge().catch(() => []),
      ]);
      setSoldes(Array.isArray(soldesData) ? soldesData : []);
      setTypesConge(Array.isArray(typesData) ? typesData : []);
    } catch (err) {
      console.error("Erreur chargement soldes:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const stored = localStorage.getItem("user");
    if (stored) {
      try {
        const u = JSON.parse(stored);
        setUser(u);
        if (u.id) {
          loadData(u.id);
        } else {
          setLoading(false);
        }
      } catch (e) {
        console.error(e);
        setLoading(false);
      }
    } else {
      setLoading(false);
    }
  }, []);

  const handleInit = async () => {
    if (!user?.id) return;
    setInitLoading(true);
    setMsg("");
    try {
      const annee = new Date().getFullYear();
      await initialiserSoldesAnnuels(user.id, annee);
      setMsg(`Soldes pour l'année ${annee} initialisés avec succès !`);
      await loadData(user.id);
    } catch (err) {
      setMsg(`Erreur lors de l'initialisation : ${err.message}`);
    } finally {
      setInitLoading(false);
    }
  };

  const getTypeInfo = (typeCongeId) => {
    const found = typesConge.find((t) => t.id === typeCongeId);
    return {
      libelle: found?.libelle || found?.code || `Type #${typeCongeId}`,
      code: found?.code || "",
    };
  };

  const totalAcquis = soldes.reduce(
    (acc, s) => acc + (Number(s.soldeAquis ?? s.soldeAcquis) || 0),
    0
  );
  const totalPris = soldes.reduce((acc, s) => acc + (Number(s.soldePris) || 0), 0);
  const totalRestant = soldes.reduce(
    (acc, s) => acc + (Number(s.soldeRestant) || 0),
    0
  );

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-800 flex items-center gap-2">
            <span>📊</span> Solde des Congés
          </h1>
          <p className="text-slate-500 text-sm mt-1">
            Consultez votre balance de congés acquis, consommés et disponibles pour l'année.
          </p>
        </div>

        <button
          onClick={handleInit}
          disabled={initLoading || !user?.id}
          className="btn btn-outline btn-primary btn-sm"
        >
          {initLoading ? (
            <span className="loading loading-spinner loading-xs"></span>
          ) : (
            "🔄 Synchroniser / Initialiser mes soldes"
          )}
        </button>
      </div>

      {msg && (
        <div className="alert alert-info text-white shadow-sm">
          <span>{msg}</span>
        </div>
      )}

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-white rounded-xl p-5 shadow-sm border border-slate-200">
          <p className="text-xs font-bold uppercase text-slate-500 tracking-wider">
            Total Jours Acquis
          </p>
          <p className="text-3xl font-extrabold text-blue-600 mt-1">
            {totalAcquis} <span className="text-sm text-slate-400 font-normal">jours</span>
          </p>
        </div>

        <div className="bg-white rounded-xl p-5 shadow-sm border border-slate-200">
          <p className="text-xs font-bold uppercase text-slate-500 tracking-wider">
            Total Jours Pris
          </p>
          <p className="text-3xl font-extrabold text-rose-500 mt-1">
            {totalPris} <span className="text-sm text-slate-400 font-normal">jours</span>
          </p>
        </div>

        <div className="bg-white rounded-xl p-5 shadow-sm border border-slate-200">
          <p className="text-xs font-bold uppercase text-slate-500 tracking-wider">
            Total Solde Disponible
          </p>
          <p className="text-3xl font-extrabold text-emerald-600 mt-1">
            {totalRestant} <span className="text-sm text-slate-400 font-normal">jours</span>
          </p>
        </div>
      </div>

      {/* Detail Table */}
      <div className="bg-white shadow-sm border border-slate-200 rounded-2xl p-6">
        <h2 className="text-lg font-bold text-slate-800 mb-4 flex items-center gap-2">
          <span>📋</span> Détail des Soldes par Type
        </h2>

        {loading ? (
          <div className="flex justify-center items-center py-12">
            <span className="loading loading-spinner loading-md text-primary"></span>
          </div>
        ) : soldes.length === 0 ? (
          <div className="text-center py-12 text-slate-500">
            <p className="text-base font-semibold">Aucun solde trouvé pour cet utilisateur.</p>
            <p className="text-sm text-slate-400 mt-1">
              Cliquez sur le bouton "Synchroniser / Initialiser mes soldes" ci-dessus pour initialiser vos droits.
            </p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="table w-full">
              <thead>
                <tr className="bg-slate-50 text-slate-600 text-xs uppercase tracking-wider">
                  <th className="py-3">Type de congé</th>
                  <th className="py-3">Période</th>
                  <th className="py-3">Solde acquis</th>
                  <th className="py-3">Solde pris</th>
                  <th className="py-3">Solde restant</th>
                  <th className="py-3">Consommation</th>
                  <th className="py-3">Dernière mise à jour</th>
                </tr>
              </thead>
              <tbody>
                {soldes.map((solde) => {
                  const type = getTypeInfo(solde.typeCongeId);
                  const acquis = Number(solde.soldeAquis ?? solde.soldeAcquis) || 0;
                  const pris = Number(solde.soldePris) || 0;
                  const restant = Number(solde.soldeRestant) || 0;
                  const pct = acquis > 0 ? Math.min(100, Math.round((pris / acquis) * 100)) : 0;
                  const rawDate = solde.date_maj || solde.dateMaj;

                  return (
                    <tr key={solde.id} className="border-b border-slate-100 hover:bg-slate-50/60">
                      <td className="py-3.5 font-bold text-slate-800">
                        {type.libelle}
                        {type.code && (
                          <span className="badge badge-sm badge-ghost ml-2 font-mono text-xs">
                            {type.code}
                          </span>
                        )}
                      </td>
                      <td className="py-3.5 text-slate-600 font-medium">
                        {solde.periode || new Date().getFullYear()}
                      </td>
                      <td className="py-3.5 text-blue-600 font-bold">
                        {acquis} j
                      </td>
                      <td className="py-3.5 text-rose-500 font-bold">
                        {pris} j
                      </td>
                      <td className="py-3.5">
                        <span className="badge badge-lg badge-success text-white font-extrabold">
                          {restant} j
                        </span>
                      </td>
                      <td className="py-3.5 w-36">
                        <div className="flex items-center gap-2">
                          <progress
                            className="progress progress-primary w-24 h-2"
                            value={pct}
                            max="100"
                          ></progress>
                          <span className="text-xs font-semibold text-slate-500">
                            {pct}%
                          </span>
                        </div>
                      </td>
                      <td className="py-3.5 text-xs text-slate-500">
                        {rawDate ? new Date(rawDate).toLocaleDateString("fr-FR") : "-"}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}


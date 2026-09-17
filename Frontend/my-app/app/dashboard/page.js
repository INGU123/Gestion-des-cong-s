"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { getMesDemandes } from "../api/demandeConge/demandeConge";
import { getSoldesUtilisateur } from "../api/soldeConge/soldeConge";
import { getAllTypeConge } from "../api/typeConge/typeConge";

export default function Dashboard() {
  const [user, setUser] = useState(null);
  const [demandes, setDemandes] = useState([]);
  const [soldes, setSoldes] = useState([]);
  const [typesConge, setTypesConge] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const storedUser = localStorage.getItem("user");
        if (!storedUser) {
          setLoading(false);
          return;
        }
        const currentUser = JSON.parse(storedUser);
        setUser(currentUser);

        const currentUserId = Number(currentUser.id);
        if (currentUserId) {
          const [demandesData, soldesData, typesData] = await Promise.all([
            getMesDemandes(currentUserId).catch(() => []),
            getSoldesUtilisateur(currentUserId).catch(() => []),
            getAllTypeConge().catch(() => []),
          ]);
          setDemandes(Array.isArray(demandesData) ? demandesData : []);
          setSoldes(Array.isArray(soldesData) ? soldesData : []);
          setTypesConge(Array.isArray(typesData) ? typesData : []);
        }
      } catch (err) {
        console.error("Erreur chargement dashboard:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const totalRestant = soldes.reduce(
    (acc, s) => acc + (Number(s.soldeRestant) || 0),
    0
  );
  const enAttenteCount = demandes.filter((d) => d.statut === "EN_ATTENTE").length;
  const valideCount = demandes.filter((d) => d.statut === "VALIDEE").length;
  const refuseCount = demandes.filter((d) => d.statut === "REFUSEE").length;

  const getTypeLabel = (typeId) => {
    const found = typesConge.find((t) => t.id === typeId);
    return found?.libelle || found?.code || `Type #${typeId}`;
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <span className="loading loading-spinner loading-lg text-primary"></span>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-blue-700 to-indigo-800 text-white rounded-2xl p-6 shadow-md flex justify-between items-center flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-extrabold tracking-tight">
            Bonjour, {user?.prenom ? `${user.prenom} ${user.nom || ""}` : user?.email || "Collaborateur"} 👋
          </h1>
          <p className="text-blue-100 text-sm mt-1">
            Bienvenue sur votre espace de gestion des congés et absences.
          </p>
        </div>
        <div className="flex gap-3">
          <Link
            href="/dashboard/demande"
            className="btn btn-warning btn-sm font-bold shadow"
          >
            + Nouvelle demande
          </Link>
          <Link
            href="/dashboard/solde"
            className="btn btn-outline btn-sm text-white border-white hover:bg-white hover:text-blue-900"
          >
            Consulter mes soldes
          </Link>
        </div>
      </div>

      {/* KPI Stats Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white rounded-xl p-5 shadow-sm border border-slate-200 flex items-center justify-between">
          <div>
            <p className="text-xs font-bold uppercase text-slate-500 tracking-wider">
              Solde Restant
            </p>
            <p className="text-3xl font-extrabold text-blue-600 mt-1">
              {totalRestant}{" "}
              <span className="text-sm font-medium text-slate-400">jours</span>
            </p>
          </div>
          <div className="w-12 h-12 rounded-full bg-blue-50 flex items-center justify-center text-2xl">
            🏖️
          </div>
        </div>

        <div className="bg-white rounded-xl p-5 shadow-sm border border-slate-200 flex items-center justify-between">
          <div>
            <p className="text-xs font-bold uppercase text-slate-500 tracking-wider">
              En attente
            </p>
            <p className="text-3xl font-extrabold text-amber-500 mt-1">
              {enAttenteCount}
            </p>
          </div>
          <div className="w-12 h-12 rounded-full bg-amber-50 flex items-center justify-center text-2xl">
            ⏳
          </div>
        </div>

        <div className="bg-white rounded-xl p-5 shadow-sm border border-slate-200 flex items-center justify-between">
          <div>
            <p className="text-xs font-bold uppercase text-slate-500 tracking-wider">
              Validées
            </p>
            <p className="text-3xl font-extrabold text-emerald-600 mt-1">
              {valideCount}
            </p>
          </div>
          <div className="w-12 h-12 rounded-full bg-emerald-50 flex items-center justify-center text-2xl">
            ✅
          </div>
        </div>

        <div className="bg-white rounded-xl p-5 shadow-sm border border-slate-200 flex items-center justify-between">
          <div>
            <p className="text-xs font-bold uppercase text-slate-500 tracking-wider">
              Refusées
            </p>
            <p className="text-3xl font-extrabold text-rose-500 mt-1">
              {refuseCount}
            </p>
          </div>
          <div className="w-12 h-12 rounded-full bg-rose-50 flex items-center justify-center text-2xl">
            ❌
          </div>
        </div>
      </div>

      {/* Quick Action Navigation */}
      <div className="bg-white rounded-xl p-6 shadow-sm border border-slate-200">
        <h2 className="text-lg font-bold text-slate-800 mb-4 flex items-center gap-2">
          <span>⚡</span> Accès Rapides
        </h2>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
          <Link
            href="/dashboard/demande"
            className="p-4 rounded-lg border border-slate-200 hover:border-blue-400 hover:bg-blue-50/50 transition-all text-center"
          >
            <div className="text-2xl mb-1">📝</div>
            <div className="font-semibold text-slate-800 text-sm">Poser un congé</div>
            <div className="text-xs text-slate-400">Créer une demande</div>
          </Link>

          <Link
            href="/dashboard/solde"
            className="p-4 rounded-lg border border-slate-200 hover:border-blue-400 hover:bg-blue-50/50 transition-all text-center"
          >
            <div className="text-2xl mb-1">📊</div>
            <div className="font-semibold text-slate-800 text-sm">Mes Soldes</div>
            <div className="text-xs text-slate-400">Détails par type</div>
          </Link>

          {(user?.role === "ADMIN" || user?.role === "MANAGER") && (
            <Link
              href="/dashboard/validation"
              className="p-4 rounded-lg border border-slate-200 hover:border-blue-400 hover:bg-blue-50/50 transition-all text-center"
            >
              <div className="text-2xl mb-1">✅</div>
              <div className="font-semibold text-slate-800 text-sm">Validation</div>
              <div className="text-xs text-slate-400">Traiter les demandes</div>
            </Link>
          )}

          {user?.role === "ADMIN" && (
            <Link
              href="/dashboard/typeConge"
              className="p-4 rounded-lg border border-slate-200 hover:border-blue-400 hover:bg-blue-50/50 transition-all text-center"
            >
              <div className="text-2xl mb-1">📑</div>
              <div className="font-semibold text-slate-800 text-sm">Types de congés</div>
              <div className="text-xs text-slate-400">Configuration</div>
            </Link>
          )}

          <Link
            href="/dashboard/parametre"
            className="p-4 rounded-lg border border-slate-200 hover:border-blue-400 hover:bg-blue-50/50 transition-all text-center"
          >
            <div className="text-2xl mb-1">⚙️</div>
            <div className="font-semibold text-slate-800 text-sm">Paramètres</div>
            <div className="text-xs text-slate-400">Mon profil</div>
          </Link>
        </div>
      </div>

      {/* Tables section : Recent Demandes & Solde Summary */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Demandes */}
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-lg font-bold text-slate-800 flex items-center gap-2">
              <span>📋</span> Mes Dernières Demandes
            </h2>
            <Link
              href="/dashboard/demande"
              className="text-xs font-semibold text-blue-600 hover:underline"
            >
              Voir tout →
            </Link>
          </div>

          {demandes.length === 0 ? (
            <div className="text-center py-8 text-slate-400 text-sm">
              Aucune demande enregistrée pour l'instant.
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="table table-sm w-full">
                <thead>
                  <tr className="text-slate-500 border-b">
                    <th>Type</th>
                    <th>Dates</th>
                    <th>Statut</th>
                  </tr>
                </thead>
                <tbody>
                  {demandes.slice(-5).reverse().map((d) => (
                    <tr key={d.id} className="hover:bg-slate-50">
                      <td className="font-medium text-slate-800">
                        {getTypeLabel(d.typeCongeId)}
                      </td>
                      <td className="text-xs text-slate-600">
                        {d.dateDebut} → {d.dateFin}
                      </td>
                      <td>
                        <span
                          className={`badge badge-sm font-semibold ${
                            d.statut === "VALIDEE"
                              ? "badge-success text-white"
                              : d.statut === "REFUSEE"
                              ? "badge-error text-white"
                              : d.statut === "ANNULEE"
                              ? "badge-ghost"
                              : "badge-warning text-slate-800"
                          }`}
                        >
                          {d.statut}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Soldes breakdown */}
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-lg font-bold text-slate-800 flex items-center gap-2">
              <span>📊</span> Aperçu de mes Soldes
            </h2>
            <Link
              href="/dashboard/solde"
              className="text-xs font-semibold text-blue-600 hover:underline"
            >
              Détails →
            </Link>
          </div>

          {soldes.length === 0 ? (
            <div className="text-center py-8 text-slate-400 text-sm">
              Aucun solde initialisé pour le moment.
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="table table-sm w-full">
                <thead>
                  <tr className="text-slate-500 border-b">
                    <th>Type</th>
                    <th>Acquis</th>
                    <th>Pris</th>
                    <th>Restant</th>
                  </tr>
                </thead>
                <tbody>
                  {soldes.map((s) => (
                    <tr key={s.id} className="hover:bg-slate-50">
                      <td className="font-medium text-slate-800">
                        {getTypeLabel(s.typeCongeId)}
                      </td>
                      <td className="text-blue-600 font-semibold">
                        {s.soldeAquis ?? s.soldeAcquis ?? 0} j
                      </td>
                      <td className="text-rose-500 font-semibold">
                        {s.soldePris ?? 0} j
                      </td>
                      <td className="text-emerald-600 font-bold">
                        {s.soldeRestant ?? 0} j
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}


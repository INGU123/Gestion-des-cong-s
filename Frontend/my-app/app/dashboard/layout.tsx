"use client";
import { useEffect, useState } from "react";
export default function DashboardLayout({ children }) {
  const [user,setUser]=useState(null);
  const [loading,setLoading]=useState(true);
  useEffect(() => {
    // 1. Récupération de l'utilisateur (ex: localStorage, API, ou session)
    const storedUser = localStorage.getItem("user");

    if (storedUser) {
      try {
        setUser(JSON.parse(storedUser));
      } catch (e) {
        console.error("Erreur lors de la lecture de l'utilisateur", e);
      }
    }

    // 2. Fin du chargement
    setLoading(false);
  }, []);
  if (loading) {
    return <p className="p-6">Chargement en cours...</p>;
  }

  return (
    <div className="flex min-h-screen flex-col">
      <header className="topbar bg-info [box-shadow:0_1px_100_0_rgb(0_0_0_/_10%)] flex items-center justify-between">
        <h1 className="text-xl font-bold mb-4">
          <strong>{user.email}</strong>
        </h1>
      </header>
      <div className="flex">
      <aside className="aside flex min-h-screen bg-gray-800 w-50">
        <nav className="sidebar text-center text-white px-6 flex flex-col space-y-4 mt-5 w-full">
          <a href="/dashboard">Dashboard</a>
          <a href="/dashboard/demande">Demande</a>
          <a href="/dashboard/types">Types de congés</a>
          <a href="/dashboard/solde">Solde</a>
          <a href="/dashboard/validation">Validation</a>
          <a href="/dashboard/historique">Historique</a>
          <a href="/dashboard/notifications">Notifications</a>
          <a href="/dashboard/utilisateurs">Utilisateurs</a>
          <a href="/dashboard/parametres">Paramètres</a>
        </nav>
      </aside>

      <main className="flex flex-col p-6 bg-green-300 flex-1">
        {children}
      </main>

      </div>

    </div>
  );
}

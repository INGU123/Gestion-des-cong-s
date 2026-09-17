"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter, usePathname } from "next/navigation";

interface DashboardUser {
  id?: number;
  nom?: string;
  prenom?: string;
  email?: string;
  role?: string;
}

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const [user, setUser] = useState<DashboardUser | null>(null);
  const [loading, setLoading] = useState(true);
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    if (storedUser) {
      try {
        setUser(JSON.parse(storedUser));
      } catch (e) {
        console.error("Erreur lors de la lecture de l'utilisateur", e);
      }
    }
    setLoading(false);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("user");
    router.push("/");
  };

  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center bg-slate-100">
        <span className="loading loading-spinner loading-lg text-primary"></span>
        <p className="ml-3 text-slate-600 font-medium">Chargement en cours...</p>
      </div>
    );
  }

  const navItems = [
    { href: "/dashboard", label: "Accueil", icon: "🏠" },
    { href: "/dashboard/demande", label: "Mes Demandes", icon: "📝" },
    { href: "/dashboard/solde", label: "Mes Soldes", icon: "📊" },
    { href: "/dashboard/typeConge", label: "Types de congés", icon: "📑" },
    { href: "/dashboard/validation", label: "Validation", icon: "✅" },
    { href: "/dashboard/historique", label: "Historique", icon: "📜" },
    { href: "/dashboard/notifications", label: "Notifications", icon: "🔔" },
    { href: "/dashboard/utilisateurs", label: "Utilisateurs", icon: "👥" },
  ];

  return (
    <div className="flex min-h-screen flex-col bg-slate-100">
      <header className="navbar bg-slate-900 text-white px-6 shadow-md flex justify-between items-center h-16">
        <div className="flex items-center gap-3">
          <span className="text-2xl">🌴</span>
          <span className="font-bold text-lg tracking-wide text-white">
            Gestion des Congés
          </span>
        </div>

        <div className="flex items-center gap-4">
          <div className="text-right">
            <p className="text-sm font-semibold text-white">
              {user ? (user.prenom ? `${user.prenom} ${user.nom || ""}` : user.email) : "Invité"}
            </p>
            {user?.role && (
              <span className="badge badge-sm badge-primary uppercase font-bold text-xs">
                {user.role}
              </span>
            )}
          </div>

          <button
            onClick={handleLogout}
            className="btn btn-outline btn-error btn-sm flex items-center gap-1"
            title="Se déconnecter"
          >
            <span>🚪</span>
            <span>Déconnexion</span>
          </button>
        </div>
      </header>

      {/* Main Container with Sidebar */}
      <div className="flex flex-1">
        <aside className="w-64 bg-slate-800 text-slate-200 flex flex-col p-4 shadow-lg shrink-0">
          <div className="text-xs uppercase tracking-wider text-slate-400 font-bold px-3 mb-3">
            Menu Principal
          </div>
          <nav className="flex flex-col space-y-1">
            {navItems.map((item) => {
              const isActive = pathname === item.href;
              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                    isActive
                      ? "bg-blue-600 text-white shadow"
                      : "text-slate-300 hover:bg-slate-700 hover:text-white"
                  }`}
                >
                  <span className="text-base">{item.icon}</span>
                  <span>{item.label}</span>
                </Link>
              );
            })}
          </nav>
        </aside>

        {/* Page Content */}
        <main className="flex-1 p-6 md:p-8 overflow-y-auto bg-slate-50">
          {children}
        </main>
      </div>
    </div>
  );
}


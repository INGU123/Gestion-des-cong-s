"use client";

import { useEffect, useState } from "react";
import {
  getNotificationNonLues,
  getToutesLesNotifications,
  marquerNotificationLue,
} from "../../api/notificationConge/notificationConge";

export default function NotificationsPage() {
  const [user, setUser] = useState(null);
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterNonLues, setFilterNonLues] = useState(false);

  useEffect(() => {
    const stored = localStorage.getItem("user");
    if (stored) {
      try {
        const u = JSON.parse(stored);
        setUser(u);
      } catch (e) {
        console.error(e);
      }
    }
  }, []);

  const loadNotifications = async () => {
    if (!user?.id) return;
    try {
      setLoading(true);
      const data = filterNonLues
        ? await getNotificationNonLues(user.id)
        : await getToutesLesNotifications(user.id);
      setNotifications(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Erreur chargement notifications:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user?.id) {
      loadNotifications();
    }
  }, [user, filterNonLues]);

  const handleMarquerLue = async (notifId) => {
    try {
      await marquerNotificationLue(notifId);
      setNotifications((prev) =>
        prev.map((n) => (n.id === notifId ? { ...n, lue: true } : n))
      );
    } catch (err) {
      console.error("Erreur mise à jour notification:", err);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex justify-between items-center flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-800 flex items-center gap-2">
            <span>🔔</span> Centre de Notifications
          </h1>
          <p className="text-slate-500 text-sm mt-1">
            Suivez en direct l'état d'avancement de vos demandes et activités.
          </p>
        </div>

        <div className="join">
          <button
            onClick={() => setFilterNonLues(false)}
            className={`join-item btn btn-sm ${!filterNonLues ? "btn-primary" : "btn-outline"}`}
          >
            Toutes
          </button>
          <button
            onClick={() => setFilterNonLues(true)}
            className={`join-item btn btn-sm ${filterNonLues ? "btn-primary" : "btn-outline"}`}
          >
            Non lues uniquement
          </button>
        </div>
      </div>

      <div className="space-y-3">
        {loading ? (
          <div className="flex justify-center items-center py-16">
            <span className="loading loading-spinner loading-lg text-primary"></span>
          </div>
        ) : notifications.length === 0 ? (
          <div className="bg-white shadow-sm border border-slate-200 rounded-2xl p-12 text-center text-slate-400">
            <span className="text-4xl block mb-2">🔕</span>
            <p className="font-semibold text-slate-600">Aucune notification pour le moment.</p>
            <p className="text-sm mt-1">Vous serez notifié dès qu'une demande est soumise, validée ou refusée.</p>
          </div>
        ) : (
          notifications.slice().reverse().map((n) => (
            <div
              key={n.id}
              className={`p-4 rounded-xl border transition-all flex items-start justify-between gap-4 ${
                n.lue
                  ? "bg-white border-slate-200 text-slate-700"
                  : "bg-blue-50/70 border-blue-200 text-slate-900 shadow-sm"
              }`}
            >
              <div className="flex gap-3 items-start">
                <span className="text-2xl mt-0.5">
                  {n.type === "VALIDATION"
                    ? "✅"
                    : n.type === "REFUS"
                    ? "❌"
                    : "ℹ️"}
                </span>
                <div>
                  <div className="flex items-center gap-2">
                    <p className="font-semibold text-sm">{n.contenu}</p>
                    {!n.lue && (
                      <span className="badge badge-xs badge-primary font-bold">
                        Nouveau
                      </span>
                    )}
                  </div>
                  <span className="text-xs text-slate-400 mt-1 block">
                    {n.date_envoi ? new Date(n.date_envoi).toLocaleDateString("fr-FR") : "Aujourd'hui"}
                  </span>
                </div>
              </div>

              {!n.lue && (
                <button
                  onClick={() => handleMarquerLue(n.id)}
                  className="btn btn-ghost btn-xs text-blue-600 hover:bg-blue-100 shrink-0"
                >
                  Marquer comme lue
                </button>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
}

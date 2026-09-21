"use client";

import { useEffect, useState } from "react";
import {
  getNotificationNonLues,
  getToutesLesNotifications,
  marquerNotificationLue,
} from "../../api/notificationConge/notificationConge";

import { getCurrentUser } from "@/lib/apiClient";
import {
  Bell,
  BellOff,
  CheckCircle2,
  XCircle,
  Info,
  Check,
  Calendar,
  Filter
} from "lucide-react";

export default function NotificationsPage() {
  const [user, setUser] = useState(null);
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterNonLues, setFilterNonLues] = useState(false);

  useEffect(() => {
    const u = getCurrentUser();
    if (u) {
      setUser(u);
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

  const getNotificationIcon = (type) => {
    switch (type) {
      case "VALIDATION":
        return <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0 mt-0.5" />;
      case "REFUS":
        return <XCircle className="w-5 h-5 text-rose-600 shrink-0 mt-0.5" />;
      default:
        return <Info className="w-5 h-5 text-blue-600 shrink-0 mt-0.5" />;
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Banner Institutionnelle SPAT */}
      <div className="bg-slate-900 text-white rounded-2xl p-6 shadow-md border-l-4 border-blue-600 flex justify-between items-center flex-wrap gap-4">
        <div>
          <div className="text-xs font-semibold uppercase tracking-widest text-blue-400 mb-1">
            SPAT — Société du Port à Gestion Autonome de Toamasina
          </div>
          <h1 className="text-2xl font-bold tracking-tight flex items-center gap-2">
            <Bell className="w-6 h-6 text-blue-400" />
            Centre de Notifications
          </h1>
          <p className="text-slate-400 text-sm mt-1">
            {`Suivez en direct l'état d'avancement de vos demandes et des alertes de votre compte.`}
          </p>
        </div>

        {/* Filtrage par état de lecture */}
        <div className="inline-flex p-1 bg-slate-800 rounded-xl border border-slate-700">
          <button
            onClick={() => setFilterNonLues(false)}
            className={`px-3.5 py-1.5 text-xs font-semibold rounded-lg transition-all flex items-center gap-1.5 ${
              !filterNonLues
                ? "bg-blue-600 text-white shadow-sm"
                : "text-slate-300 hover:text-white"
            }`}
          >
            <Filter className="w-3.5 h-3.5" />
            Toutes
          </button>
          <button
            onClick={() => setFilterNonLues(true)}
            className={`px-3.5 py-1.5 text-xs font-semibold rounded-lg transition-all flex items-center gap-1.5 ${
              filterNonLues
                ? "bg-blue-600 text-white shadow-sm"
                : "text-slate-300 hover:text-white"
            }`}
          >
            <Bell className="w-3.5 h-3.5" />
            Non lues uniquement
          </button>
        </div>
      </div>

      {/* Liste des Notifications */}
      <div className="space-y-3">
        {loading ? (
          <div className="flex justify-center items-center py-16">
            <span className="loading loading-spinner loading-lg text-primary"></span>
          </div>
        ) : notifications.length === 0 ? (
          <div className="bg-white shadow-sm border border-slate-200 rounded-2xl p-12 text-center text-slate-400">
            <BellOff className="w-12 h-12 mx-auto mb-3 text-slate-300" />
            <p className="font-semibold text-slate-600 text-base">Aucune notification pour le moment.</p>
            <p className="text-sm mt-1 text-slate-400">
             {` Vous serez notifié dès qu'une demande de congé est soumise, validée ou refusée.`}
            </p>
          </div>
        ) : (
          notifications.slice().reverse().map((n) => (
            <div
              key={n.id}
              className={`p-4 rounded-xl border transition-all flex items-start justify-between gap-4 ${
                n.lue
                  ? "bg-white border-slate-200 text-slate-700"
                  : "bg-blue-50/60 border-blue-200 text-slate-900 shadow-xs"
              }`}
            >
              <div className="flex gap-3.5 items-start">
                {getNotificationIcon(n.type)}
                <div>
                  <div className="flex items-center gap-2 flex-wrap">
                    <p className="font-semibold text-sm leading-snug">{n.contenu}</p>
                    {!n.lue && (
                      <span className="inline-flex items-center px-2 py-0.5 rounded text-[10px] font-bold bg-blue-600 text-white uppercase tracking-wider">
                        Nouveau
                      </span>
                    )}
                  </div>
                  <span className="text-xs text-slate-400 mt-1 flex items-center gap-1">
                    <Calendar className="w-3 h-3" />
                    {n.date_envoi ? new Date(n.date_envoi).toLocaleDateString("fr-FR") : "Aujourd'hui"}
                  </span>
                </div>
              </div>

              {!n.lue && (
                <button
                  onClick={() => handleMarquerLue(n.id)}
                  className="btn btn-ghost btn-xs text-blue-600 hover:bg-blue-100 hover:text-blue-700 shrink-0 gap-1"
                >
                  <Check className="w-3.5 h-3.5" />
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
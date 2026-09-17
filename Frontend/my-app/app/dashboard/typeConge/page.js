"use client";

import { useEffect, useState } from "react";
import {
  getAllTypeConge,
  creerTypeConge,
  deleteTypeConge,
} from "../../api/typeConge/typeConge";

export default function TypeCongePage() {
  const [types, setTypes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [msg, setMsg] = useState({ type: "", text: "" });

  const [formData, setFormData] = useState({
    code: "",
    libelle: "",
    nombreJoursParAn: 30,
    justificatifObligatoire: false,
    couleur: "#2563eb",
    actif: true,
  });

  const loadTypes = async () => {
    try {
      setLoading(true);
      const data = await getAllTypeConge();
      setTypes(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Erreur récupération types:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTypes();
  }, []);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleAddType = async (e) => {
    e.preventDefault();
    if (!formData.code.trim() || !formData.libelle.trim()) {
      setMsg({ type: "error", text: "Veuillez renseigner le code et le libellé." });
      return;
    }

    setSubmitting(true);
    setMsg({ type: "", text: "" });
    try {
      const payload = {
        ...formData,
        nombreJoursParAn: Number(formData.nombreJoursParAn) || 0,
      };
      const created = await creerTypeConge(payload);
      setTypes((prev) => [...prev, created]);
      setFormData({
        code: "",
        libelle: "",
        nombreJoursParAn: 30,
        justificatifObligatoire: false,
        couleur: "#2563eb",
        actif: true,
      });
      setMsg({ type: "success", text: "Type de congé créé avec succès !" });
    } catch (err) {
      setMsg({ type: "error", text: `Erreur : ${err.message}` });
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Êtes-vous sûr de vouloir supprimer ce type de congé ?")) {
      return;
    }
    try {
      await deleteTypeConge(id);
      setTypes((prev) => prev.filter((t) => t.id !== id));
      setMsg({ type: "success", text: "Type de congé supprimé." });
    } catch (err) {
      setMsg({ type: "error", text: `Erreur lors de la suppression : ${err.message}` });
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-extrabold text-slate-800 flex items-center gap-2">
          <span>📑</span> Configuration des Types de Congés
        </h1>
        <p className="text-slate-500 text-sm mt-1">
          Définissez les différentes catégories de congés, les quotas annuels et les règles justificatives.
        </p>
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

      {/* Formulaire de création */}
      <div className="bg-white shadow-sm border border-slate-200 rounded-2xl p-6">
        <h2 className="text-lg font-bold text-slate-800 mb-4 flex items-center gap-2">
          <span>➕</span> Nouveau Type de Congé
        </h2>
        <form onSubmit={handleAddType} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div>
              <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                Code (Sigle)
              </label>
              <input
                type="text"
                name="code"
                value={formData.code}
                onChange={handleChange}
                placeholder="Ex: CP, RTT, MAL"
                required
                className="input input-bordered w-full uppercase font-mono"
              />
            </div>

            <div className="sm:col-span-2">
              <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                Libellé
              </label>
              <input
                type="text"
                name="libelle"
                value={formData.libelle}
                onChange={handleChange}
                placeholder="Ex: Congés Payés Annuels"
                required
                className="input input-bordered w-full"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 items-center">
            <div>
              <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                Nombre de jours par an
              </label>
              <input
                type="number"
                name="nombreJoursParAn"
                min="0"
                value={formData.nombreJoursParAn}
                onChange={handleChange}
                required
                className="input input-bordered w-full"
              />
            </div>

            <div className="form-control">
              <label className="label cursor-pointer justify-start gap-3 mt-4">
                <input
                  type="checkbox"
                  name="justificatifObligatoire"
                  checked={formData.justificatifObligatoire}
                  onChange={handleChange}
                  className="checkbox checkbox-primary"
                />
                <span className="label-text font-medium text-slate-700">
                  Justificatif obligatoire
                </span>
              </label>
            </div>

            <div className="form-control">
              <label className="label cursor-pointer justify-start gap-3 mt-4">
                <input
                  type="checkbox"
                  name="actif"
                  checked={formData.actif}
                  onChange={handleChange}
                  className="checkbox checkbox-success"
                />
                <span className="label-text font-medium text-slate-700">
                  Type actif
                </span>
              </label>
            </div>
          </div>

          <div className="flex justify-end pt-2">
            <button
              type="submit"
              disabled={submitting}
              className="btn btn-primary px-6"
            >
              {submitting ? (
                <>
                  <span className="loading loading-spinner loading-sm"></span>
                  Ajout en cours...
                </>
              ) : (
                "Ajouter le type de congé"
              )}
            </button>
          </div>
        </form>
      </div>

      {/* Liste des types */}
      <div className="bg-white shadow-sm border border-slate-200 rounded-2xl p-6">
        <h2 className="text-lg font-bold text-slate-800 mb-4 flex items-center gap-2">
          <span>📋</span> Types de Congés Disponibles
        </h2>

        {loading ? (
          <div className="flex justify-center items-center py-12">
            <span className="loading loading-spinner loading-md text-primary"></span>
          </div>
        ) : types.length === 0 ? (
          <div className="text-center py-12 text-slate-400">
            Aucun type de congé configuré pour le moment.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="table w-full">
              <thead>
                <tr className="bg-slate-50 text-slate-600 text-xs uppercase tracking-wider">
                  <th>Code</th>
                  <th>Libellé</th>
                  <th>Jours / an</th>
                  <th>Justificatif</th>
                  <th>Statut</th>
                  <th className="text-right">Action</th>
                </tr>
              </thead>
              <tbody>
                {types.map((t) => (
                  <tr key={t.id} className="hover:bg-slate-50/60 border-b border-slate-100">
                    <td>
                      <span className="badge badge-primary font-mono font-bold text-xs">
                        {t.code || `ID-${t.id}`}
                      </span>
                    </td>
                    <td className="font-semibold text-slate-800">
                      {t.libelle || t.nom || "-"}
                    </td>
                    <td className="font-bold text-blue-600">
                      {t.nombreJoursParAn ?? 0} jours
                    </td>
                    <td>
                      {t.justificatifObligatoire ? (
                        <span className="badge badge-warning badge-sm text-xs font-semibold">
                          Obligatoire
                        </span>
                      ) : (
                        <span className="badge badge-ghost badge-sm text-xs text-slate-400">
                          Non requis
                        </span>
                      )}
                    </td>
                    <td>
                      {t.actif !== false ? (
                        <span className="badge badge-success text-white badge-sm text-xs font-bold">
                          Actif
                        </span>
                      ) : (
                        <span className="badge badge-ghost badge-sm text-xs text-slate-400">
                          Inactif
                        </span>
                      )}
                    </td>
                    <td className="text-right">
                      <button
                        onClick={() => handleDelete(t.id)}
                        className="btn btn-ghost btn-xs text-rose-600 hover:bg-rose-50"
                        title="Supprimer"
                      >
                        🗑️ Supprimer
                      </button>
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


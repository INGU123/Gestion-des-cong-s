"use client";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { useState, useEffect } from "react";
import {
  creerDemandeConge,
  getMesDemandes,
  annulerDemandeConge,
} from "../../api/demandeConge/demandeConge.js";

export default function DemandesPage() {
  const utilisateurId = 1;
  const [formData, setFormData] = useState({
    type: "",
    debut: "",
    fin: "",
    motif: "",
  });
  const [demandes, setDemandes] = useState([]);
  const [loading, setLoading] = useState(false);

  // Charger les demandes existantes
  useEffect(() => {
    const fetchDemandes = async () => {
      try {
        const data = await getMesDemandes(utilisateurId);
        setDemandes(data);
      } catch (err) {
        console.error("Erreur récupération demandes:", err);
      }
    };
    fetchDemandes();
  }, []);
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const nouvelleDemande = await creerDemandeConge(utilisateurId, formData);
      setDemandes([...demandes, nouvelleDemande]);
      setFormData({ type: "", debut: "", fin: "", motif: "" });
    } catch (err) {
      alert("Erreur lors de la création de la demande");
    } finally {
      setLoading(false);
    }
  };

  // Annuler une demande
  const handleAnnuler = async (demandeId) => {
    try {
      const updated = await annulerDemandeConge(demandeId, utilisateurId);
      setDemandes(demandes.map((d) => (d.id === demandeId ? updated : d)));
    } catch (err) {
      alert("Erreur lors de l'annulation");
    }
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">📄 Mes demandes de congé</h1>

      <form
        onSubmit={handleSubmit}
        className="bg-white shadow p-4 rounded mb-6 space-y-4"
      >
        <div className="flex space-x-2 justify-center">
          <div>
            <label className="block text-center underline font-bold">
              Date debut
            </label>

            <DatePicker
              selected={formData.debut}
              onChange={(date) => setFormData({ ...formData, debut: date })}
              dateFormat="yyyy-MM-dd"
              inline
              className="border bg-500 rounded p-2 w-full"
            />
          </div>

          <div>
            <label
              htmlFor="block"
              className="block text-center underline font-bold"
            >
              Date fin
            </label>
            <DatePicker
              selected={formData.fin}
              onChange={(date) =>
                setFormData({
                  ...formData,
                  fin: date,
                })
              }
              dateFormat="yyyy-MM-dd"
              inline
              className="border bg-gray-500 rounded p-2 w-full"
            />
          </div>
        </div>

        <div>
          <label className="text-9sm text-center block text-green-500">
            Motif
          </label>
          <textarea
            name="motif"
            value={formData.motif}
            onChange={(e) => {
              setFormData({
                ...formData,
                motif: e.target.value, // ✅ e est bien défini
              });
            }}
            className="border rounded p-2 w-full"
            rows="3"
            required
          />
          <button
            type="submit"
            disabled={loading}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          >
            {loading ? "Envoi..." : "Soumettre"}
          </button>
        </div>
      </form>

      <div className="bg-white shadow p-4 rounded">
        <h2 className="text-xl font-semibold mb-3">📜 Historique</h2>
        {demandes.length === 0 ? (
          <p>Aucune demande pour l’instant.</p>
        ) : (
          <ul className="space-y-2">
            {demandes.map((d) => (
              <li
                key={d.id}
                className="flex justify-between items-center border-b py-2"
              >
                <div>
                  <p>
                    <strong>{d.type}</strong> du {d.date_debut} au {d.date_fin}
                  </p>
                  <p>
                    Statut : <span className="font-bold">{d.statut}</span>
                  </p>
                </div>
                {d.statut === "EN_ATTENTE" && (
                  <button
                    onClick={() => handleAnnuler(d.id)}
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

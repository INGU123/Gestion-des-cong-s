"use client";

import { useEffect, useState } from "react";

export default function UtilisateursPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [newUser, setNewUser] = useState({
    nom: "",
    prenom: "",
    email: "",
    mot_de_pass: "123456",
    role: "EMPLOYE",
    date_embauche: new Date().toISOString().split("T")[0],
    actif: true,
  });
  const [msg, setMsg] = useState({ type: "", text: "" });

  const loadUsers = async () => {
    try {
      setLoading(true);
      const res = await fetch("http://localhost:8080/utilisateur/all");
      if (res.ok) {
        const data = await res.json();
        setUsers(Array.isArray(data) ? data : []);
      }
    } catch (err) {
      console.error("Erreur chargement utilisateurs:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUsers();
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    setMsg({ type: "", text: "" });
    try {
      const res = await fetch("http://localhost:8080/utilisateur/create", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newUser),
      });
      if (res.ok) {
        const created = await res.json();
        setUsers((prev) => [...prev, created]);
        setShowModal(false);
        setNewUser({
          nom: "",
          prenom: "",
          email: "",
          mot_de_pass: "123456",
          role: "EMPLOYE",
          date_embauche: new Date().toISOString().split("T")[0],
          actif: true,
        });
        setMsg({ type: "success", text: "Collaborateur créé avec succès !" });
      } else {
        const txt = await res.text();
        setMsg({ type: "error", text: txt || "Erreur lors de la création." });
      }
    } catch (err) {
      setMsg({ type: "error", text: `Erreur : ${err.message}` });
    }
  };

  const filteredUsers = users.filter((u) => {
    const term = search.toLowerCase();
    return (
      (u.nom && u.nom.toLowerCase().includes(term)) ||
      (u.prenom && u.prenom.toLowerCase().includes(term)) ||
      (u.email && u.email.toLowerCase().includes(term)) ||
      (u.role && u.role.toLowerCase().includes(term))
    );
  });

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-800 flex items-center gap-2">
            <span>👥</span> Gestion des Utilisateurs
          </h1>
          <p className="text-slate-500 text-sm mt-1">
            Consultez et administrez les comptes collaborateurs, managers et administrateurs.
          </p>
        </div>

        <button
          onClick={() => setShowModal(true)}
          className="btn btn-primary btn-sm font-bold shadow"
        >
          + Nouveau Collaborateur
        </button>
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
        <div className="flex justify-between items-center mb-4 gap-4 flex-wrap">
          <input
            type="text"
            placeholder="Rechercher par nom, email, rôle..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="input input-bordered input-sm w-full max-w-xs text-slate-800"
          />
          <span className="text-xs text-slate-500 font-medium">
            Total : {filteredUsers.length} collaborateur(s)
          </span>
        </div>

        {loading ? (
          <div className="flex justify-center items-center py-16">
            <span className="loading loading-spinner loading-lg text-primary"></span>
          </div>
        ) : filteredUsers.length === 0 ? (
          <div className="text-center py-16 text-slate-400">
            Aucun utilisateur trouvé.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="table w-full">
              <thead>
                <tr className="bg-slate-50 text-slate-600 text-xs uppercase tracking-wider">
                  <th>ID</th>
                  <th>Collaborateur</th>
                  <th>Email</th>
                  <th>Rôle</th>
                  <th>Date d'embauche</th>
                  <th>Statut</th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.map((u) => (
                  <tr key={u.id} className="hover:bg-slate-50/60 border-b border-slate-100">
                    <td className="font-mono text-xs text-slate-400">#{u.id}</td>
                    <td className="font-bold text-slate-800">
                      {u.prenom} {u.nom}
                    </td>
                    <td className="text-sm text-slate-600 font-medium">{u.email}</td>
                    <td>
                      <span
                        className={`badge badge-sm font-bold ${
                          u.role === "ADMIN"
                            ? "badge-primary text-white"
                            : u.role === "MANAGER"
                            ? "badge-secondary text-white"
                            : "badge-ghost text-slate-700"
                        }`}
                      >
                        {u.role || "EMPLOYE"}
                      </span>
                    </td>
                    <td className="text-xs text-slate-600">
                      {u.date_embauche || "-"}
                    </td>
                    <td>
                      {u.actif !== false ? (
                        <span className="badge badge-success badge-xs text-white">
                          Actif
                        </span>
                      ) : (
                        <span className="badge badge-error badge-xs text-white">
                          Inactif
                        </span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal d'ajout utilisateur */}
      {showModal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-xl max-w-lg w-full p-6 space-y-4">
            <h3 className="text-lg font-bold text-slate-800 flex items-center gap-2">
              <span>➕</span> Ajouter un collaborateur
            </h3>

            <form onSubmit={handleCreate} className="space-y-3">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                    Nom
                  </label>
                  <input
                    type="text"
                    required
                    value={newUser.nom}
                    onChange={(e) => setNewUser({ ...newUser, nom: e.target.value })}
                    className="input input-bordered input-sm w-full"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                    Prénom
                  </label>
                  <input
                    type="text"
                    required
                    value={newUser.prenom}
                    onChange={(e) => setNewUser({ ...newUser, prenom: e.target.value })}
                    className="input input-bordered input-sm w-full"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                  Email
                </label>
                <input
                  type="email"
                  required
                  value={newUser.email}
                  onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
                  className="input input-bordered input-sm w-full"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                    Mot de passe
                  </label>
                  <input
                    type="password"
                    required
                    value={newUser.mot_de_pass}
                    onChange={(e) => setNewUser({ ...newUser, mot_de_pass: e.target.value })}
                    className="input input-bordered input-sm w-full"
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                    Rôle
                  </label>
                  <select
                    value={newUser.role}
                    onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}
                    className="select select-bordered select-sm w-full"
                  >
                    <option value="EMPLOYE">Employé</option>
                    <option value="MANAGER">Manager</option>
                    <option value="ADMIN">Administrateur</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold uppercase text-slate-600 mb-1">
                  Date d'embauche
                </label>
                <input
                  type="date"
                  value={newUser.date_embauche}
                  onChange={(e) => setNewUser({ ...newUser, date_embauche: e.target.value })}
                  className="input input-bordered input-sm w-full"
                />
              </div>

              <div className="flex justify-end gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="btn btn-ghost btn-sm"
                >
                  Annuler
                </button>
                <button type="submit" className="btn btn-primary btn-sm">
                  Créer le compte
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

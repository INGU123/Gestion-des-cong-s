"use client";
import { postData } from "@/app/api/home";
import { useState } from "react";
import Link from "next/link";
export default function Utilisateur(){
   const [formData,setFormData]=useState ({
    nom: "",
    prenom: "",
    email: "",
    mot_de_pass: "",
    role: "",
    service_id: null,
    manager_id: null,
    date_embauche: "",
    actif: true,
    date_creation: new Date().toISOString(),
  });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault(); // Correction de l'orthographe

    // Formater les données pour correspondre au DTO / Entity Spring Boot
    const payload = {
      ...formData,
      service_id: formData.service_id ? Number(formData.service_id) : null,
      manager_id: formData.manager_id ? Number(formData.manager_id) : null,
    };

    try {
      const result = await postData("/utilisateur/create", payload);
      console.log("Utilisateur créé :", result);
    } catch (error) {
      console.error("Erreur lors de la création :", error.message);
    }
  };

  return (
    <div>
      <div>
        <form
          onSubmit={handleSubmit}
          className="flex flex-col items-center space-y-2"
        >
          <h1 className="text-3xl font-bold underline">Création utilisateur</h1>

          <input
            type="text"
            className="input input-sm text-white px-4"
            name="nom"
            onChange={handleChange}
            placeholder="Nom"
          />
          <input
            type="text"
            className="input input-sm text-white px-4"
            name="prenom"
            onChange={handleChange}
            placeholder="Prénom"
          />
          <input
            type="email"
            className="input input-sm text-white px-4"
            name="email"
            onChange={handleChange}
            placeholder="Email"
          />
          <input
            type="password"
            className="input input-sm text-white px-4"
            name="mot_de_pass"
            onChange={handleChange}
            placeholder="Mot de passe"
          />
          <input
            type="text"
            className="input input-sm text-white px-4"
            name="role"
            onChange={handleChange}
            placeholder="Rôle"
          />
          <input
            type="number"
            className="input input-sm text-white px-4"
            name="service_id"
            onChange={handleChange}
            placeholder="ID Service"
          />
          <input
            type="number"
            className="input input-sm text-white px-4"
            name="manager_id"
            onChange={handleChange}
            placeholder="ID Manager"
          />
          <input
            type="date"
            className="input input-sm text-white px-4"
            name="date_embauche"
            onChange={handleChange}
          />

          <button type="submit" className="btn btn-primary">
            Ajouter
          </button>
        </form>

        <div>
          <span>Déjà membre ?</span>
          <Link href="/">
            <button className="btn btn-error btn-sm">Login</button>
          </Link>
        </div>
      </div>
    </div>
  );
}

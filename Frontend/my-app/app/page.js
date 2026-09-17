"use client";
import { loginUser } from "@/lib/apiLogin.js";
import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";

const Home = () => {
  const [email, setEmail] = useState("");
  const [motDePass, setMotDePass] = useState("");
  const [error, setError] = useState("");
  const [mdpSee, setMdpSee] = useState(false);
  const router = useRouter();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const user = await loginUser(email, motDePass);

      localStorage.setItem("user", JSON.stringify(user));

      router.push("/dashboard");
    } catch (err) {
      setError("Email ou mot de passe incorrect. " + err);
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white rounded-xl shadow-2xl border border-slate-200 overflow-hidden">
        {/* En-tête Institutionnel SPAT */}
        <div className="bg-[#023E8A] p-8 text-center text-white relative">
          <div className="w-16 h-16 bg-white/10 rounded-full flex items-center justify-center mx-auto mb-3 border border-white/20">
            <span className="text-2xl font-black tracking-wider text-white">
              SPAT
            </span>
          </div>
          <h1 className="text-2xl font-bold tracking-wide">
            Port de Toamasina
          </h1>
          <p className="text-xs text-blue-200 uppercase tracking-widest mt-1">
            Société du Port à Gestion Autonome de Toamasina
          </p>
        </div>

        {/* Formulaire de connexion */}
        <form onSubmit={handleLogin} className="p-8 space-y-5">
          <div className="text-center mb-2">
            <h2 className="text-lg font-semibold text-slate-800">
              Espace Authentification
            </h2>
            <p className="text-xs text-slate-500">
              Veuillez saisir vos identifiants pour accéder au portail
            </p>
          </div>

          {error && (
            <div className="p-3 bg-red-50 border-l-4 border-red-500 rounded text-red-700 text-xs font-medium">
              {error}
            </div>
          )}

          {/* Champ Email */}
          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-600 block">
              Adresse e-mail
            </label>
            <input
              type="email"
              className="w-full px-3 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-[#023E8A] focus:bg-white transition-all"
              placeholder="nom@port-toamasina.mg"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          {/* Champ Mot de passe */}
          <div className="space-y-1">
            <label className="text-xs font-semibold text-slate-600 block">
              Mot de passe
            </label>
            <div className="relative flex items-center">
              <input
                type={mdpSee ? "text" : "password"}
                className="w-full px-3 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-[#023E8A] focus:bg-white transition-all pr-10"
                placeholder="mot de pass..."
                value={motDePass}
                onChange={(e) => setMotDePass(e.target.value)}
                required
              />
              <button
                type="button"
                onClick={() => setMdpSee(!mdpSee)}
                className="absolute right-3 text-slate-400 hover:text-slate-600 text-sm focus:outline-none"
                aria-label="Afficher ou masquer le mot de passe"
              >
                {mdpSee ? "🙈" : "👁️"}
              </button>
            </div>
          </div>

          {/* Lien Mot de passe oublié */}
          <div className="flex justify-end">
            <Link
              href="/Components/mdpForget"
              className="text-xs text-[#023E8A] hover:underline font-medium"
            >
              Mot de passe oublié ?
            </Link>
          </div>

          {/* Bouton de soumission */}
          <button
            type="submit"
            className="w-full py-2.5 bg-[#023E8A] hover:bg-[#002855] text-white font-medium text-sm rounded-lg shadow-md transition-colors duration-200 focus:ring-2 focus:ring-offset-2 focus:ring-[#023E8A]"
          >
            Se connecter
          </button>

          {/* Pied de carte */}
          <div className="pt-4 border-t border-slate-100 text-center">
            <p className="text-[10px] text-slate-400">
              © {new Date().getFullYear()} SPAT Madagascar. Tous droits
              réservés.
            </p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Home;

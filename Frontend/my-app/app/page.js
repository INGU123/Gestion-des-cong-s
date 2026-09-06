"use client";
import { loginUser } from "@/lib/apiLogin.js";
import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";

const Home = () => {
  const [email, setEmail] = useState("");
  const [motDePass, setMotDePass] = useState("");
  const [error, setError] = useState("");
  const router = useRouter();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const user = await loginUser(email, motDePass);
      
      localStorage.setItem("user", JSON.stringify(user));

      router.push("/dashboard"); 
    } catch (err) {
      setError("Email ou mot de passe incorrect."+err);
    }
  };

  return (
    <div>
      <div className="flex flex-col items-center justify-center h-screen bg-gray-500">
        <form 
          onSubmit={handleLogin}
          className="card h-100 w-200 bg-info flex flex-col items-center font-[family-monospace] space-y-4 p-6"
        >
          <h1 className="card-title text-3xl font-bold underline">
            Gestion de Conge
          </h1>

          {error && <p className="text-red-500 text-sm font-bold">{error}</p>}

          <input
            type="text"
            className="input w-[80%] text-white"
            placeholder="Email / Pseudo : ..."
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />

          <input
            type="password"
            className="input w-[80%] text-white"
            placeholder="Mot de passe : ..."
            value={motDePass}
            onChange={(e) => setMotDePass(e.target.value)}
            required
          />

          <button type="submit" className="btn btn-primary">
            Login
          </button>

          <div className="flex space-x-2 items-center">
            <span>Pas encore de compte?</span>
            <Link href="/Components/utilisateur">
              <button type="button" className="btn btn-sm">
                Créer un compte
              </button>
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Home;
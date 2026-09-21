import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";

// Configuration des polices officielles Vercel / Next.js
const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

// Métadonnées officielles de l'application SPAT
export const metadata: Metadata = {
  title: "SPAT - Gestion des Congés",
  description: "Portail d'administration de la Société du Port à Gestion Autonome de Toamasina",
  icons: {
    icon: "/favicon.ico",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="fr" className={`${geistSans.variable} ${geistMono.variable}`}>
      <body className="antialiased bg-slate-100 text-slate-900 font-sans min-h-screen">
        {children}
      </body>
    </html>
  );
}
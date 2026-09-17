"use client";
import { useEffect } from "react";
import { useRouter } from "next/navigation";

export default function TypesRedirect() {
  const router = useRouter();
  useEffect(() => {
    router.replace("/dashboard/typeConge");
  }, [router]);

  return null;
}

const API_URL="http://localhost:8080/type-conge";

export const creerTypeConge=async(typeData)=>{
    try{
    const response=await fetch(`${API_URL}/create`,{
        method:"POST",
        headers:{"Content-Type":"application/json"},
        body:JSON.stringify(typeData),
    });
    if(!response.ok)throw new Error("Erreur lors de la creation du type de conge");
    return await response.json();
    }catch(error){
        console.error("Erreur obtenu : ",error.message);
        throw error;
    }
}

export const getAllTypeConge=async()=>{
    const response=await fetch(`${API_URL}/all`);
    if(!response.ok)throw new Error("Erreur lors de la recuperation des types de conges");
    return await response.json();
}

export const updateTypeConge = async (id, typeData) => {
  const response = await fetch(`${API_URL}/update/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(typeData),
  });
  if (!response.ok) throw new Error("Erreur lors de la mise à jour du type de congé");
  return await response.json();
};

export const deleteTypeConge=async (id)=>{
    const response=await fetch(`${API_URL}/delete/${id}`,{
        method:"DELETE",
    });
    if(!response.ok)throw new Error("Erreur lors de la suppression");
    return true;
}

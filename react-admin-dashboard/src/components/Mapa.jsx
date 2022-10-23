
import React from "react"
import { useEffect, useState } from "react"

export const Mapa = ()=>{
    const [dataToFillTable, setDataToFillTable] = useState(null)

    useEffect(()=>{
        const retrieveData = async ()=>{
            const respuesta = await fetch(`https://apibbva-u67rnmwcrq-uc.a.run.app/getAllAtms`);
            const publicacion = await respuesta.json();
            setDataToFillTable(publicacion)
        }
        retrieveData()
    },[])
    console.log("Respuesta", dataToFillTable)
    return (
        <>
        <h1>MAPA</h1>
        <h2></h2>
        </>
    )
}
import { useEffect, useRef, useState } from "react"

export const Map= ({onClick, onIdle, children, style, ...options})=>{
    const ref = useRef(null)
    const [map, setMap] = useState()

    var myLatlng = new window.google.maps.LatLng(19.4270206, -99.169788);

    var mapOptions = {
        zoom: 15,
        center: myLatlng,
        mapTypeId: 'roadmap'
      };


    useEffect(()=>{
        if(ref.current && !map){
            setMap(new window.google.maps.Map(ref.current, mapOptions))
        }
    },[ref, map])

    return (
        <>
        <button onClick={()=>{console.log("VALORES DE LAS PROPIEDADES ", style)}}>MOSTRAR PROPS</button>
        <div ref={ref} style={{...style}} >{children}</div>
        
        </>
    )
}
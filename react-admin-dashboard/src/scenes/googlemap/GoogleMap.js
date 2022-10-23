/* import { Wrapper, Status } from "@googlemaps/react-wrapper";
 */import Map from '../atmmaps/maps'
/* const render = (status= Status)=>{
    return <h1>{status}</h1>
} */

const mapProperties = ()=> ({
    style:{ height: "100vh", width: "100%"},
    onclick: (e)=>{console.log("EJECUCUON D FUNCION ON CLCIK")},
    onIdle: (e)=>{console.log("EJECUCION DE LA FUNCION ON IDLE")}

})

export const PersonalizedMap = ()=>{
    return (
        <>
        
        </>
        
    )
}
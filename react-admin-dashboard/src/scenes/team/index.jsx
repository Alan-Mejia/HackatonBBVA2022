import { Box, Typography, useTheme } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { tokens } from "../../theme";
import { mockDataTeam } from "../../data/mockData";
import AdminPanelSettingsOutlinedIcon from "@mui/icons-material/AdminPanelSettingsOutlined";
import LockOpenOutlinedIcon from "@mui/icons-material/LockOpenOutlined";
import SecurityOutlinedIcon from "@mui/icons-material/SecurityOutlined";
import Header from "../../components/Header";
import { useState } from "react";
import { useEffect } from "react";
import { color } from "@mui/system";
import { useRef } from "react";

const Team = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  //const [columns, setColumns] = useState()
  const rowWithUniqueKey = useRef([])

  const [dataToFillTable, setDataToFillTable] = useState([])

  useEffect(()=>{
    const retrieveData = async ()=>{
        const respuesta = await fetch(`https://apibbvaattempt2-u67rnmwcrq-uc.a.run.app/getAllAvailable`);
        const publicacion = await respuesta.json();
        setDataToFillTable(publicacion)
    }
    retrieveData()
},[])



  const columns = [
    { 
      field: "atmId", 
      headerName: "ATM",
      type: "number",
      headerClassName: "super-app-theme--header"
    },
    {
      field: "marca",
      headerName: "Marca",
      flex: 1,
    },
   
    {
      field: "sitio",
      headerName: "Sitio",
      flex: 1,
    }, 
    {
      field: "ciudad",
      headerName: "Ciudad",
      flex: 1,
    }, 
    {
      field: "calle",
      headerName: "Calle",
      flex: 1,
    }, 
    {
      field: "colonia",
      headerName: "Colonia",
      flex: 1,
    }, 
    /* {
      field: "access",
      headerName: "Nivel de Acceso",
      flex: 1,
      renderCell: ({ row: { access } }) => {
        console.log(access)
        return (
          <Box
            width="60%"
            m="0 auto"
            p="5px"
            display="flex"
            justifyContent="center"
            backgroundColor={
              access === "admin"
                ? colors.greenAccent[600]
                : access === "manager"
                ? colors.greenAccent[700]
                : colors.greenAccent[700]
            }
            borderRadius="4px"
          >
            {access === "admin" && <AdminPanelSettingsOutlinedIcon />}
            {access === "manager" && <SecurityOutlinedIcon />}
            {access === "user" && <LockOpenOutlinedIcon />}
            <Typography color={colors.grey[100]} sx={{ ml: "5px" }}>
              {access}
            </Typography>
          </Box>
        );
      },
    }, */
  ];

  /* 
    {
      field: "marca",
      headerName: "Marca",
      flex: 1,
    },
   
    {
      field: "sitio",
      headerName: "Sitio",
      flex: 1,
    }, 
    {
      field: "ciudad",
      headerName: "Ciudad",
      flex: 1,
    }, 
    {
      field: "calle",
      headerName: "Calle",
      flex: 1,
    }, 
    {
      field: "colonia",
      headerName: "Colonia",
      flex: 1,
    }, 
    {
      field: "sitio",
      headerName: "Sitio",
      flex: 1,
    },  */

  rowWithUniqueKey.current = dataToFillTable?.map((oneObject)=>{
    return {
      id: oneObject?.atmId, 
      marca: oneObject?.marca, 
      sitio: oneObject?.sitio, 
      ciudad:oneObject?.ciudad, 
      atmId: oneObject?.atmId, 
      colonia: oneObject?.colonia,}
  })

  return (
    <Box m="20px">
      <Header title="Cajeros En Funcionamiento" /* subtitle="Managing the Team Members" */  backgroundColor={"green"}/>
      <Box
        m="40px 0 0 0"
        height="75vh"
        sx={{
          "& .MuiDataGrid-root": {
            border: "none",
          },
          "& .MuiDataGrid-cell": {
            borderBottom: "none",
          },
          "& .name-column--cell": {
            color: "white",
            fontWeight: "bold"
          },
          "& .MuiDataGrid-columnHeaders": {
            backgroundColor: "green",
            borderBottom: "none",
            color: "white"
          },
          "& .MuiDataGrid-virtualScroller": {
            backgroundColor: colors.primary[400],
          },
          "& .MuiDataGrid-footerContainer": {
            borderTop: "none",
            backgroundColor: "",
          },
          "& .MuiCheckbox-root": {
            color: `${colors.greenAccent[200]} !important`,
          },
        }}
      >
        <DataGrid checkboxSelection rows={rowWithUniqueKey.current} columns={columns}  />
      </Box>
    </Box>
  );
};

export default Team;


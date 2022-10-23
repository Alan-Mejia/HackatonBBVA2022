import { Box, Button, IconButton, Typography, useTheme } from "@mui/material";
import { tokens } from "../../theme";

const Dashboard = () => {
  const theme = useTheme();
  const colors = tokens("red");

  return (
          <Box >
            <img src="https://motor.elpais.com/wp-content/uploads/2022/01/google-maps-22.jpg"></img>
          </Box>
  );
};

export default Dashboard;

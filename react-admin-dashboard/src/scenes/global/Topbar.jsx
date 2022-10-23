import { Box, IconButton, useTheme } from "@mui/material";
import { useContext } from "react";
import { ColorModeContext, tokens } from "../../theme";
import NotificationsOutlinedIcon from "@mui/icons-material/NotificationsOutlined";
import LogoutIcon from '@mui/icons-material/Logout';

const Topbar = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const colorMode = useContext(ColorModeContext);

  return (
    <Box display="flex" justifyContent="space-between" p={2} backgroundColor={"#006EC1"}>
      {/* SEARCH BAR */}
      <Box
        display="flex"
        borderRadius="3px"
      >
        <img
                  alt="profile-user"
                  width="85px"
                  height="75px"
                  src={`https://logodownload.org/wp-content/uploads/2018/11/bbva-logo-1.png`}
                  style={{ cursor: "pointer", borderRadius: "30%" }}
                />
      </Box>

      {/* ICONS */}
      <Box display="flex">
       
        <IconButton>
          <NotificationsOutlinedIcon />
        </IconButton>
        
        <IconButton>
          <LogoutIcon color="primary"/>
        </IconButton>
      </Box>
    </Box>
  );
};

export default Topbar;

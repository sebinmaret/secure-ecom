import React from "react";
import { AuthConsumer } from "../../providers/authProvider";
import { useContext } from "react";
import { Navigate, useNavigate } from "react-router-dom";


export const LogoutCallback = () => {
    const cont=useContext(AuthConsumer)
    if(cont.isAuthenticated()){
        cont.signoutRedirectCallback();
    }
    return (
        <Navigate to={"/"}></Navigate>
    );

};
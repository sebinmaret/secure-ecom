import * as React from "react";
import { AuthConsumer } from "../../providers/authProvider";
import { useContext } from "react";
import { Navigate } from "react-router-dom";

export const Logout = () => {
    const cont = useContext(AuthConsumer)
    if (cont.isAuthenticated()) {
        cont.logout();
    }
    return (
        <Navigate to={"/"}></Navigate>
    );
};
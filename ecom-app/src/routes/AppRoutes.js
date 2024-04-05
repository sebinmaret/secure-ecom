import * as React from "react";
import { Route, Routes } from "react-router-dom";

import { Callback } from "../components/auth/Callback";
import { Logout } from "../components/auth/Logout";
import { LogoutCallback } from "../components/auth/LogoutCallback";
import { Register } from "../components/auth/Register";
import { SilentRenew } from "../components/auth/SilentRenew";
import { Container } from "react-bootstrap";
import HomePage from "../pages/HomePage";
import CartPage from "../pages/CartPage";
import FeedbackPage from "../pages/FeedbackPage";
import ChangePasswordPage from "../pages/ChangePasswordPage";
import FeedbackListPage from "../pages/FeedBackListPage";


export const AppRoutes = (
    <main>
        <Container fluid className="bg-light">
            <Routes>

                <Route path="/" element={<HomePage />} />
                <Route exact path="/signin-oidc" element={<Callback />} />
                <Route exact path="/logout" element={<Logout />} />
                <Route
                    exact
                    path="/logout/callback"
                    element={<LogoutCallback />}
                />
                <Route
                    exact={true}
                    path="/register/:form?"
                    element={<Register />}
                />
                <Route exact path="/silentrenew" element={<SilentRenew />} />
                <Route exact path="/cart" element={<CartPage />} />
                <Route exact path="/feedback" element={<FeedbackPage />} />
                <Route exact path="/feedbacklist" element={<FeedbackListPage />} />
                <Route exact path="/changepassword" element={<ChangePasswordPage />} />
                <Route path="*" element={<HomePage/>}/>




            </Routes>
        </Container>
    </main>

);
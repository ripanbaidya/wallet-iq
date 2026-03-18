import { Routes, Route, Navigate } from "react-router-dom";
import PrivateRoute from "../components/PrivateRoute";
import Layout from "../components/Layout";

import LoginPage from "../pages/auth/LoginPage";
import SignupPage from "../pages/auth/SignupPage";
import DashboardPage from "../pages/dashboard/DashboardPage";
import TransactionsPage from "../pages/transactions/TransactionsPage";
import CategoriesPage from "../pages/categories/CategoriesPage";
import BudgetsPage from "../pages/budgets/BudgetsPage";
import SavingsPage from "../pages/savings/SavingsPage";
import ChatPage from "../pages/chat/ChatPage";
import ProfilePage from "../pages/profile/ProfilePage";

const AppRoutes: React.FC = () => (
    <Routes>
        {/* Public routes */}
        <Route path="/login"  element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />

        {/* Protected routes — all wrapped in Layout */}
        <Route
            element={
                <PrivateRoute>
                    <Layout />
                </PrivateRoute>
            }
        >
            <Route path="/dashboard"    element={<DashboardPage />} />
            <Route path="/transactions" element={<TransactionsPage />} />
            <Route path="/categories"   element={<CategoriesPage />} />
            <Route path="/budgets"      element={<BudgetsPage />} />
            <Route path="/savings"      element={<SavingsPage />} />
            <Route path="/chat"         element={<ChatPage />} />
            <Route path="/profile"      element={<ProfilePage />} />
        </Route>

        {/* Default redirect */}
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
);

export default AppRoutes;
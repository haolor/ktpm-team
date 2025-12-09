import { useSearchParams } from "react-router-dom";
import HomePage from "../pages/HomePage/HomePage";

import AuthPage from "../pages/AuthPage/AuthPage.jsx";

export const routes = [
  {
    path: "/",
    page: HomePage,
    isShowHeader: true,
  },

  {
    path: "/auth", // For signup and signin
    page: AuthPage,
    isShowHeader: true,
  },
];

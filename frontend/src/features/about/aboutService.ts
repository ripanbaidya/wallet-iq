import { apiClient } from "../../lib/axios";
import type { AppInfoResponse } from "./about.types";
import type { ResponseWrapper } from "../../types/api.types";

export const aboutService = {
  getAppInfo: async (): Promise<ResponseWrapper<AppInfoResponse>> => {
    const res = await apiClient.get<ResponseWrapper<AppInfoResponse>>("/app/info");
    return res.data;
  },
};
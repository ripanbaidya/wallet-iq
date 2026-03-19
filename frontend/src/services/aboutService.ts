import { apiClient } from "../lib/axios";
import type { AppInfoResponse } from "../types/about.types";
import type { ResponseWrapper } from "../types/api.types";

export const aboutService = {
  /**
   * Get application information
   * @returns Promise<ResponseWrapper<AppInfoResponse>>
   */
  getAppInfo: async (): Promise<ResponseWrapper<AppInfoResponse>> => {
    const res = await apiClient.get<ResponseWrapper<AppInfoResponse>>('/app/info');
    return res.data;
  }
}
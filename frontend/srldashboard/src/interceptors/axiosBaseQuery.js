// axiosBaseQuery.js
import axiosInstance from "./index.js";

export const axiosBaseQuery =
  ({ baseUrl } = { baseUrl: "" }) =>
  async ({ url, method, body, params, headers }) => {
    try {
      const { data } = await axiosInstance({
        url: baseUrl + url,
        method,
        data: body,
        params,
        headers,
      });
      if (data?.success || data?.status) {
        return { data };
      }
      return { error: { status: data?.status, data: data?.message } };
    } catch (error) {
      if (error.response?.status === 401) {
        throw error;
      }

      return {
        error: {
          status: error.response?.status,
          data: error.response?.data || error.message,
        },
      };
    }
  };

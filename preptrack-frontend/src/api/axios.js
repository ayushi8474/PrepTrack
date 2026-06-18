import axios from "axios";

export const api = axios.create({
  baseURL: "https://preptrack-13mf.onrender.com",
});
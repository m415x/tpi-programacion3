export interface IUser {
  email: string;
  password: string;
  role: "client" | "admin";
}

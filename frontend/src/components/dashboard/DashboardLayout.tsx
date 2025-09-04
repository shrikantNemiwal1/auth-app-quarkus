import React from "react";
import Header from "../common/Header";

interface Props {
  children: React.ReactNode;
  navigate: (path: string) => void;
}

const DashboardLayout = ({ children, navigate }: Props) => (
  <div className="min-h-screen flex flex-col items-center bg-gray-900 text-white">
    <Header navigate={navigate} />
    <main className="container mx-auto p-4 md:p-8 w-full flex-grow">{children}</main>
  </div>
);

export default DashboardLayout;

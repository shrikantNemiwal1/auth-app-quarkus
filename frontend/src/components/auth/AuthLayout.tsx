import React from "react";

const AuthLayout = ({ children, title }: { children: React.ReactNode; title: string }) => (
  <div className="min-h-screen flex items-center justify-center bg-gray-900 text-white p-4">
    <div className="w-full max-w-md">
      <div className="bg-gray-800 border border-gray-700 rounded-2xl shadow-lg p-8">
        <h2 className="text-3xl font-bold text-center mb-6 text-blue-400">{title}</h2>
        {children}
      </div>
    </div>
  </div>
);

export default AuthLayout;


const ErrorMessage = ({ message }: { message: string | null }) => {
  if (!message) return null;
  return <p className="text-sm text-red-400 bg-red-900/50 p-3 rounded-md mt-4">{message}</p>;
};

export default ErrorMessage;
